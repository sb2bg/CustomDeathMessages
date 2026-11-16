package org.spartandevs.customdeathmessages;

import co.aikar.commands.BukkitCommandManager;
import co.aikar.commands.ConditionFailedException;
import co.aikar.commands.InvalidCommandArgument;
import org.bstats.bukkit.Metrics;
import org.bstats.charts.SimplePie;
import org.bukkit.plugin.java.JavaPlugin;
import org.spartandevs.customdeathmessages.commands.CDMCommand;
import org.spartandevs.customdeathmessages.listeners.BukkitLoginListener;
import org.spartandevs.customdeathmessages.listeners.BukkitPlayerDeathListener;
import org.spartandevs.customdeathmessages.listeners.CustomPlayerDeathListener;
import org.spartandevs.customdeathmessages.util.*;

import java.util.stream.Collectors;
import java.util.stream.IntStream;

public final class CustomDeathMessages extends JavaPlugin {
    private ConfigManager configManager;
    private CooldownManager cooldownManager;
    private DiscordManager discordManager;
    private PlaceholderApiManager placeholderApiManager;

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
        // register bukkit related managers in onEnable to maintain softdepend
        discordManager = new DiscordManager(this);
        placeholderApiManager = new PlaceholderApiManager(this);
        cooldownManager = new CooldownManager();

        registerEvents();
        registerCommands();
    }

    private void registerEvents() {
        getServer().getPluginManager().registerEvents(new BukkitLoginListener(this), this);
        getServer().getPluginManager().registerEvents(new BukkitPlayerDeathListener(this), this);
        getServer().getPluginManager().registerEvents(new CustomPlayerDeathListener(this), this);
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

        commandManager.getCommandCompletions().registerStaticCompletion("stringConfigPaths", configManager::getStringConfigPaths);
        commandManager.getCommandCompletions().registerStaticCompletion("boolConfigPaths", configManager::getBoolConfigPaths);
        commandManager.getCommandCompletions().registerStaticCompletion("numConfigPaths", configManager::getNumConfigPaths);

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
                throw new ConditionFailedException("Index " + value + " is out of bounds. The maximum index is " + (count - 1) + ".");
            }

            if (count == 1) {
                throw new ConditionFailedException("Cannot remove the last death message.");
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
            t.printStackTrace();
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

    public DiscordManager getDiscordManager() {
        return discordManager;
    }

    public PlaceholderApiManager getPlaceholderApiManager() {
        return placeholderApiManager;
    }
}
