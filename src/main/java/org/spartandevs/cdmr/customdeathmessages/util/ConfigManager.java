package org.spartandevs.cdmr.customdeathmessages.util;

import org.spartandevs.cdmr.customdeathmessages.CustomDeathMessages;

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class ConfigManager {
    private final CustomDeathMessages plugin;
    private final ServerVersion serverVersion;
    private final Map<DeathCause, List<String>> messages = new HashMap<>();
    private boolean checkUpdatesEnabled;
    private boolean globalMessagesEnabled;
    private boolean doLightningStrike;
    private double dropHeadChance;
    private String headName;
    private boolean doPvpMessages;
    private String killerMessage;
    private String victimMessage;
    private List<String> meleeMessages;
    private boolean originalOnHoverEnabled;
    private boolean itemOnHoverEnabled;
    private boolean debugEnabled;

    public ConfigManager(CustomDeathMessages plugin) {
        this.plugin = plugin;
        this.serverVersion = plugin.getServerVersion();
    }

    public void loadConfig() {
        plugin.saveDefaultConfig();

        for (DeathCause cause : DeathCause.values()) {
            if (cause.validForVersion(serverVersion)) {
                messages.put(cause, plugin.getConfig().getStringList(cause.getPath()));
            }
        }

        System.out.println(messages);

        checkUpdatesEnabled = plugin.getConfig().getBoolean("enable-update-messages");
        globalMessagesEnabled = plugin.getConfig().getBoolean("enable-global-messages");
        doLightningStrike = plugin.getConfig().getBoolean("do-lightning");
        dropHeadChance = plugin.getConfig().getDouble("drop-head-chance");
        headName = plugin.getConfig().getString("head-name");
        doPvpMessages = plugin.getConfig().getBoolean("enable-pvp-messages");
        killerMessage = plugin.translateColorCodes(plugin.getConfig().getString("killer-message"));
        victimMessage = plugin.translateColorCodes(plugin.getConfig().getString("victim-message"));
        meleeMessages = plugin.getConfig().getStringList("melee-death-messages");
        originalOnHoverEnabled = plugin.getConfig().getBoolean("original-hover-message");
        itemOnHoverEnabled = plugin.getConfig().getBoolean("enable-item-hover");
        debugEnabled = plugin.getConfig().getBoolean("developer-mode");
    }

    public void reloadConfig() {
        plugin.reloadConfig();
        loadConfig();
    }

    public String getMessage(DeathCause cause) {
        List<String> messages = this.messages.get(cause);

        if (invalidCollection(messages, cause.getPath(), plugin)) {
            return null;
        }

        return messages.get(new Random().nextInt(messages.size()));
    }

    public boolean doLightningStrike() {
        return doLightningStrike;
    }

    public boolean dropHead() {
        return new Random().nextDouble() <= dropHeadChance;
    }

    public String getHeadName() {
        return headName;
    }

    public boolean doPvpMessages() {
        return doPvpMessages;
    }

    public String getKillerMessage() {
        return killerMessage;
    }

    public String getVictimMessage() {
        return victimMessage;
    }

    public boolean isCheckUpdatesEnabled() {
        return checkUpdatesEnabled;
    }

    public boolean isGlobalMessageEnabled() {
        return globalMessagesEnabled;
    }

    public String getMeleeMessage() {
        if (invalidCollection(meleeMessages, "melee-death-messages", plugin)) {
            return null;
        }

        return meleeMessages.get(new Random().nextInt(meleeMessages.size()));
    }

    public boolean isOriginalOnHoverEnabled() {
        return originalOnHoverEnabled;
    }

    public boolean isItemOnHoverEnabled() {
        return itemOnHoverEnabled;
    }

    public boolean isDebugEnabled() {
        return debugEnabled;
    }

    private static boolean invalidCollection(List<?> collection, String name, CustomDeathMessages plugin) {
        if (collection == null || collection.isEmpty()) {
            plugin.getLogger().warning(MessageFormat.format("No messages found for {0}! Check your config.yml.", name));
            return true;
        }

        return false;
    }
}
