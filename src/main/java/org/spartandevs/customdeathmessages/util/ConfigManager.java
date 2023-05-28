package org.spartandevs.customdeathmessages.util;

import org.bstats.bukkit.Metrics;
import org.bstats.charts.SimplePie;
import org.spartandevs.customdeathmessages.CustomDeathMessages;

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class ConfigManager {
    private final CustomDeathMessages plugin;
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
    }

    public void loadConfig() {
        plugin.saveDefaultConfig();

        for (DeathCause cause : DeathCause.values()) {
            messages.put(cause, plugin.getConfig().getStringList(cause.getPath()));
        }

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

    private void registerStatistics() {
        Metrics metrics = new Metrics(plugin, 7287);
        metrics.addCustomChart(new SimplePie("head_drop_percentage", () -> String.valueOf(dropHeadChance)));
        metrics.addCustomChart(new SimplePie("give_killer_speed", () -> getBooleanString(plugin.getConfig().getBoolean("give-killer-speed")))); // legacy
        metrics.addCustomChart(new SimplePie("heart_sucker", () -> getBooleanString(plugin.getConfig().getBoolean("heart-sucker")))); // legacy
        metrics.addCustomChart(new SimplePie("do_lightning", () -> getBooleanString(doLightningStrike)));
        metrics.addCustomChart(new SimplePie("enable_global_messages", () -> getBooleanString(globalMessagesEnabled)));
        metrics.addCustomChart(new SimplePie("enable_pvp_messages", () -> getBooleanString(doPvpMessages)));
        metrics.addCustomChart(new SimplePie("enable_entity_name_messages", () -> getBooleanString(plugin.getConfig().getBoolean("enable-entity-name-messages"))));
        metrics.addCustomChart(new SimplePie("enable_original_hover_message", () -> getBooleanString(originalOnHoverEnabled)));
        metrics.addCustomChart(new SimplePie("enable_item_tooltip_message", () -> getBooleanString(itemOnHoverEnabled)));
    }

    private static String getBooleanString(boolean value) {
        return value ? "Enabled" : "Disabled";
    }

    private static boolean invalidCollection(List<?> collection, String name, CustomDeathMessages plugin) {
        if (collection == null || collection.isEmpty()) {
            plugin.getLogger().warning(MessageFormat.format("No messages found for {0}! Check your config.yml.", name));
            return true;
        }

        return false;
    }
}
