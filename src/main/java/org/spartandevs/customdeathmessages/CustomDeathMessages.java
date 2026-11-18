package org.spartandevs.customdeathmessages;

import co.aikar.commands.BukkitCommandManager;
import co.aikar.commands.ConditionFailedException;
import co.aikar.commands.InvalidCommandArgument;
import org.bstats.bukkit.Metrics;
import org.bstats.charts.SimplePie;
import org.bukkit.plugin.java.JavaPlugin;
import org.spartandevs.customdeathmessages.commands.CDMCommand;
import org.spartandevs.customdeathmessages.listeners.UpdateNotifyListener;
import org.spartandevs.customdeathmessages.listeners.DeathListener;
import org.spartandevs.customdeathmessages.util.*;

import java.io.IOException;
import java.util.logging.Level;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public final class CustomDeathMessages extends JavaPlugin {
    private static final int SPIGOT_RESOURCE_ID = 69605;
    private static final long UPDATE_CHECK_INTERVAL_TICKS = 20L * 60 * 60 * 6;

    private ConfigManager configManager;
    private CooldownManager cooldownManager;
    private PlaceholderApiHook placeholderApi;

    @Override
    public void onEnable() {
        configManager = new ConfigManager(this);
        String error = configManager.load();

        if (error != null) {
            getLogger().severe("Could not load config.yml, so CustomDeathMessages is disabled. Fix the file and restart:");
            getLogger().severe(error);
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        registerMetrics();
        // created in onEnable so soft dependencies (PlaceholderAPI) are already enabled
        placeholderApi = new PlaceholderApiHook(this);
        cooldownManager = new CooldownManager();

        getServer().getPluginManager().registerEvents(new DeathListener(this), this);
        startUpdateChecks();
        registerCommands();
    }

    private void startUpdateChecks() {
        UpdateChecker updateChecker = new UpdateChecker(SPIGOT_RESOURCE_ID);
        getServer().getPluginManager().registerEvents(new UpdateNotifyListener(this, updateChecker), this);
        // check at startup and every few hours in the background, never once per join
        getServer().getScheduler().runTaskTimerAsynchronously(this, () -> {
            if (!configManager.isCheckUpdatesEnabled()) {
                return;
            }

            try {
                updateChecker.check();
            } catch (IOException e) {
                getLogger().info("Failed to check for updates: " + e.getMessage());
            }
        }, 0L, UPDATE_CHECK_INTERVAL_TICKS);
    }

    @SuppressWarnings("deprecation") // Unstable API
    private void registerCommands() {
        BukkitCommandManager commandManager = new BukkitCommandManager(this);
        commandManager.enableUnstableAPI("help");

        commandManager.getCommandCompletions().registerAsyncCompletion("messagePaths", c -> configManager.getMessagePaths());
        commandManager.getCommandCompletions().registerAsyncCompletion("deathMessageIndices", c -> {
            String path = c.getContextValueByName(String.class, "path");
            return IntStream.range(0, configManager.getMessagesCount(path)).mapToObj(String::valueOf).collect(Collectors.toList());
        });

        // async (re-evaluated on every tab) so completions reflect the config after a reload
        commandManager.getCommandCompletions().registerAsyncCompletion("stringConfigPaths", c -> configManager.getStringConfigPaths());
        commandManager.getCommandCompletions().registerAsyncCompletion("boolConfigPaths", c -> configManager.getBoolConfigPaths());
        commandManager.getCommandCompletions().registerAsyncCompletion("numConfigPaths", c -> configManager.getNumConfigPaths());

        commandManager.getCommandConditions().addCondition(String.class, "messagePath", (c, exec, value) -> {
            if (!configManager.hasMessageList(value)) {
                throw new InvalidCommandArgument("Unknown death message list '" + value + "'.");
            }
        });

        commandManager.getCommandConditions().addCondition(String.class, "newMessagePath", (c, exec, value) -> {
            if (!MessagePaths.isMessagePath(value)) {
                throw new InvalidCommandArgument("Death message lists must end with '" + MessagePaths.SUFFIX + "'.");
            }
        });

        commandManager.getCommandConditions().addCondition(int.class, "indexInBounds", (c, exec, value) -> {
            if (value < 0) {
                throw new ConditionFailedException("Index cannot be negative.");
            }

            String path = (String) exec.getPassedArgs().get("path");
            int count = configManager.getMessagesCount(path);

            if (value >= count) {
                throw new ConditionFailedException(count == 0
                        ? "'" + path + "' has no messages to remove."
                        : "Index " + value + " is out of bounds. The maximum index is " + (count - 1) + ".");
            }
        });

        commandManager.getCommandConditions().addCondition("debugEnabled", c -> {
            if (!configManager.isDebugEnabled()) {
                throw new ConditionFailedException("Debug mode is not enabled.");
            }
        });

        commandManager.getCommandConditions().addCondition(String.class, "validStringConfigPath", (c, exec, value) -> {
            if (!configManager.getStringConfigPaths().contains(value)) {
                throw new InvalidCommandArgument("Invalid config path.");
            }
        });

        commandManager.getCommandConditions().addCondition(String.class, "validBoolConfigPath", (c, exec, value) -> {
            if (!configManager.getBoolConfigPaths().contains(value)) {
                throw new InvalidCommandArgument("Invalid config path.");
            }
        });

        commandManager.getCommandConditions().addCondition(String.class, "validNumConfigPath", (c, exec, value) -> {
            if (!configManager.getNumConfigPaths().contains(value)) {
                throw new InvalidCommandArgument("Invalid config path.");
            }
        });

        commandManager.registerCommand(new CDMCommand().setExceptionHandler((command, registeredCommand, sender, args, t) -> {
            sender.sendMessage("An error occurred while executing the command. Please check the console for more information.");
            getLogger().log(Level.SEVERE, "Error executing /" + command.getName() + " " + String.join(" ", args), t);
            return true;
        }));
    }

    private void registerMetrics() {
        Metrics metrics = new Metrics(this, 7287);
        metrics.addCustomChart(new SimplePie("head_drop_percentage", () -> String.valueOf(configManager.getHeadDropChance())));
        metrics.addCustomChart(new SimplePie("enable_lightning", () -> enabled(configManager.doLightningStrike())));
        metrics.addCustomChart(new SimplePie("enable_global_messages", () -> enabled(configManager.isGlobalMessageEnabled())));
        metrics.addCustomChart(new SimplePie("enable_pvp_messages", () -> enabled(configManager.doPvpMessages())));
        metrics.addCustomChart(new SimplePie("enable_entity_name_messages", () -> enabled(configManager.isCustomNamedEntityMessageEnabled())));
        metrics.addCustomChart(new SimplePie("enable_original_hover_message", () -> enabled(configManager.isOriginalOnHoverEnabled())));
        metrics.addCustomChart(new SimplePie("enable_item_tooltip_message", () -> enabled(configManager.isItemOnHoverEnabled())));
    }

    private static String enabled(boolean value) {
        return value ? "Enabled" : "Disabled";
    }

    /**
     * Reloads config.yml. If it cannot be loaded the previous configuration stays active.
     *
     * @return {@code null} on success, otherwise a description of the problem
     */
    public String reload() {
        String error = configManager.load();

        if (error == null) {
            cooldownManager.clearCooldowns();
        }

        return error;
    }

    public ConfigManager getConfigManager() {
        return configManager;
    }

    public CooldownManager getCooldownManager() {
        return cooldownManager;
    }

    public PlaceholderApiHook getPlaceholderApi() {
        return placeholderApi;
    }
}
