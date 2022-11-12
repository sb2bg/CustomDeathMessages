package org.spartandevs.cdmr.customdeathmessages;

import org.bukkit.plugin.java.JavaPlugin;
import org.spartandevs.cdmr.customdeathmessages.util.ConfigManager;

public final class CustomDeathMessages extends JavaPlugin {
    private final ConfigManager configManager = new ConfigManager(this);

    @Override
    public void onEnable() {
    }

    @Override
    public void onDisable() {

    }

    public ConfigManager getConfigManager() {
        return configManager;
    }
}
