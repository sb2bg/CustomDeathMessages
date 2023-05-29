package org.spartandevs.customdeathmessages;

import co.aikar.commands.BukkitCommandManager;
import co.aikar.commands.ConditionFailedException;
import co.aikar.commands.InvalidCommandArgument;
import org.bukkit.plugin.java.JavaPlugin;
import org.spartandevs.customdeathmessages.commands.CDMCommand;
import org.spartandevs.customdeathmessages.listeners.BukkitKilledByEntityListener;
import org.spartandevs.customdeathmessages.listeners.BukkitLoginListener;
import org.spartandevs.customdeathmessages.listeners.BukkitPlayerDeathListener;
import org.spartandevs.customdeathmessages.listeners.CustomPlayerDeathListener;
import org.spartandevs.customdeathmessages.util.ConfigManager;
import org.spartandevs.customdeathmessages.util.DeathCause;
import org.spartandevs.customdeathmessages.util.MessagePropagator;

import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public final class CustomDeathMessages extends JavaPlugin {
    private final ConfigManager configManager = new ConfigManager(this);
    private final MessagePropagator messagePropagator = new MessagePropagator(this);
    private Set<String> configPaths;

    @Override
    public void onEnable() {
        configManager.loadConfig();
        configPaths = configManager.getConfigPaths();
        registerEvents();
        registerCommands();
    }

    private void registerEvents() {
        getServer().getPluginManager().registerEvents(new BukkitLoginListener(this), this);
        getServer().getPluginManager().registerEvents(new BukkitPlayerDeathListener(this), this);
        getServer().getPluginManager().registerEvents(new BukkitKilledByEntityListener(this), this);
        getServer().getPluginManager().registerEvents(new CustomPlayerDeathListener(this), this);
    }

    private void registerCommands() {
        BukkitCommandManager commandManager = new BukkitCommandManager(this);
        commandManager.enableUnstableAPI("help");

        commandManager.getCommandContexts().registerContext(DeathCause.class, c -> {
            String path = c.popFirstArg();
            DeathCause cause = DeathCause.fromPathSingle(path);

            if (cause == null) {
                throw new InvalidCommandArgument("Invalid path.");
            }

            return cause;
        });

        commandManager.getCommandCompletions().registerAsyncCompletion("deathMessageIndices", c -> {
            DeathCause path = c.getContextValue(DeathCause.class);
            return IntStream.range(0, configManager.getMessagesCount(path)).mapToObj(String::valueOf).collect(Collectors.toList());
        });

        commandManager.getCommandCompletions().registerStaticCompletion("configPaths", configPaths);
        commandManager.getCommandCompletions().registerStaticCompletion("deathMessagePaths", DeathCause.PATH_SET);

        commandManager.getCommandConditions().addCondition(int.class, "indexInBounds", (c, exec, value) -> {
            if (value < 0) {
                throw new ConditionFailedException("Index cannot be negative.");
            }

            DeathCause path = (DeathCause) exec.getResolvedArg(DeathCause.class);
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

        commandManager.getCommandConditions().addCondition(String.class, "validConfigPath", (c, exec, value) -> {
            if (!configPaths.contains(value)) {
                throw new InvalidCommandArgument("Invalid config path.");
            }
        });

        commandManager.registerCommand(new CDMCommand().setExceptionHandler((command, registeredCommand, sender, args, t) -> {
            sender.sendMessage("An error occurred while executing the command. Please check the console for more information.");
            t.printStackTrace();
            return true;
        }));
    }

    public ConfigManager getConfigManager() {
        return configManager;
    }

    public void reload() {
        configManager.reloadConfig();
        messagePropagator.clear();
    }

    public MessagePropagator getMessagePropagator() {
        return messagePropagator;
    }
}
