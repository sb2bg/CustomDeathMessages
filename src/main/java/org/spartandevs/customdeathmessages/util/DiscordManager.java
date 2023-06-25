package org.spartandevs.customdeathmessages.util;

import net.essentialsx.api.v2.services.discord.DiscordService;
import net.essentialsx.api.v2.services.discord.MessageType;
import org.spartandevs.customdeathmessages.CustomDeathMessages;

public class DiscordManager {
    private DiscordInterface discord;

    public DiscordManager(CustomDeathMessages plugin) {
        if (plugin.getServer().getPluginManager().isPluginEnabled("EssentialsDiscord")) {
            DiscordService essDiscord = plugin.getServer().getServicesManager().load(DiscordService.class);

            if (essDiscord == null) {
                plugin.getLogger().warning("Failed to load EssentialsDiscord integration.");
                return;
            }

            discord = message -> {
                try {
                    essDiscord.sendMessage(MessageType.DefaultTypes.DEATH, message, true);
                } catch (Exception e) {
                    plugin.getLogger().warning("Failed to send message to Discord.");
                }
            };

            plugin.getLogger().info("Successfully loaded EssentialsDiscord support.");
        } else if (plugin.getServer().getPluginManager().isPluginEnabled("DiscordSRV")) {
            plugin.getLogger().warning("DiscordSRV support is not yet implemented.");
        }
    }

    public void sendDeathMessage(String message) {
        if (discord != null) {
            discord.sendDeathMessage(message);
        }
    }

    public boolean isDiscordEnabled() {
        return discord != null;
    }
}

interface DiscordInterface {
    void sendDeathMessage(String message);
}
