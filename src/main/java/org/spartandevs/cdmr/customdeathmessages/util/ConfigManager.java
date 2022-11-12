package org.spartandevs.cdmr.customdeathmessages.util;

import org.spartandevs.cdmr.customdeathmessages.CustomDeathMessages;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class ConfigManager {
    private final CustomDeathMessages plugin;
    private final Map<DeathCause, List<String>> messages = new HashMap<>();
    private boolean checkUpdatesEnabled;
    private boolean globalMessagesEnabled;
    private boolean debugEnabled;

    public ConfigManager(CustomDeathMessages plugin) {
        this.plugin = plugin;
        loadConfig();
    }

    public void loadConfig() {
        plugin.saveDefaultConfig();

        for (DeathCause cause : DeathCause.values()) {
            messages.put(cause, plugin.getConfig().getStringList(cause.getPath()));
        }

        checkUpdatesEnabled = plugin.getConfig().getBoolean("enable-update-messages");
        globalMessagesEnabled = plugin.getConfig().getBoolean("enable-global-messages");
        debugEnabled = plugin.getConfig().getBoolean("developer-mode");
    }

    public String getMessage(DeathCause cause) {
        return messages.get(cause).get(new Random().nextInt(messages.get(cause).size()));
    }

    public boolean isCheckUpdatesEnabled() {
        return checkUpdatesEnabled;
    }

    public boolean isGlobalMessageEnabled() {
        return globalMessagesEnabled;
    }

    public boolean isDebugEnabled() {
        return debugEnabled;
    }
}
