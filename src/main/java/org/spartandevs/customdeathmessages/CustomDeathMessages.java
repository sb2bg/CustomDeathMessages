package org.spartandevs.customdeathmessages;

import org.bukkit.plugin.java.JavaPlugin;
import org.spartandevs.cdmr.customdeathmessages.chat.ChatColor;
import org.spartandevs.cdmr.customdeathmessages.listeners.BukkitKilledByEntityListener;
import org.spartandevs.cdmr.customdeathmessages.listeners.BukkitLoginListener;
import org.spartandevs.cdmr.customdeathmessages.listeners.BukkitPlayerDeathListener;
import org.spartandevs.cdmr.customdeathmessages.listeners.CustomPlayerDeathListener;
import org.spartandevs.cdmr.customdeathmessages.util.ConfigManager;
import org.spartandevs.cdmr.customdeathmessages.util.ServerVersion;

public final class CustomDeathMessages extends JavaPlugin {
    private final ServerVersion serverVersion = ServerVersion.getServerVersion();
    private final ConfigManager configManager = new ConfigManager(this);
    private final ChatColor chatColor = new ChatColor(this);

    @Override
    public void onEnable() {
        configManager.loadConfig();
        registerEvents();
    }

    private void registerEvents() {
        getServer().getPluginManager().registerEvents(new BukkitLoginListener(this), this);
        getServer().getPluginManager().registerEvents(new BukkitPlayerDeathListener(this), this);
        getServer().getPluginManager().registerEvents(new BukkitKilledByEntityListener(), this);
        getServer().getPluginManager().registerEvents(new CustomPlayerDeathListener(this), this);
    }

    @Override
    public void onDisable() {

    }

    public ConfigManager getConfigManager() {
        return configManager;
    }

    public ServerVersion getServerVersion() {
        return serverVersion;
    }

    public String translateColorCodes(String message) {
        return chatColor.translateAlternateColorCodes(message);
    }
}
