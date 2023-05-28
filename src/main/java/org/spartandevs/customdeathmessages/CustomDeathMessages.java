package org.spartandevs.customdeathmessages;

import org.bukkit.plugin.java.JavaPlugin;
import org.spartandevs.customdeathmessages.chat.ChatColor;
import org.spartandevs.customdeathmessages.listeners.BukkitKilledByEntityListener;
import org.spartandevs.customdeathmessages.listeners.BukkitLoginListener;
import org.spartandevs.customdeathmessages.listeners.BukkitPlayerDeathListener;
import org.spartandevs.customdeathmessages.listeners.CustomPlayerDeathListener;
import org.spartandevs.customdeathmessages.util.ConfigManager;
import org.spartandevs.customdeathmessages.util.MessagePropagator;

public final class CustomDeathMessages extends JavaPlugin {
    private final ConfigManager configManager = new ConfigManager(this);
    private final ChatColor chatColor = new ChatColor(this);
    private final MessagePropagator messagePropagator = new MessagePropagator(this);

    @Override
    public void onEnable() {
        configManager.loadConfig();
        registerEvents();
    }

    private void registerEvents() {
        getServer().getPluginManager().registerEvents(new BukkitLoginListener(this), this);
        getServer().getPluginManager().registerEvents(new BukkitPlayerDeathListener(this), this);
        getServer().getPluginManager().registerEvents(new BukkitKilledByEntityListener(this), this);
        getServer().getPluginManager().registerEvents(new CustomPlayerDeathListener(this), this);
    }

    public ConfigManager getConfigManager() {
        return configManager;
    }
    
    public String translateColorCodes(String message) {
        return chatColor.translateAlternateColorCodes(message);
    }

    public void reload() {
        configManager.reloadConfig();
        messagePropagator.clear();
    }

    public MessagePropagator getMessagePropagator() {
        return messagePropagator;
    }
}
