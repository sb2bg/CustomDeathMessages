package org.spartandevs.customdeathmessages.util;

import net.essentialsx.api.v2.services.discord.DiscordService;
import net.essentialsx.api.v2.services.discord.MessageType;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.spartandevs.customdeathmessages.CustomDeathMessages;

public class DiscordManager {
    private final DiscordSender discord;

    public DiscordManager(CustomDeathMessages plugin) {
        DiscordSender sender = (message, player, event) -> {
        };

        if (plugin.getServer().getPluginManager().isPluginEnabled("EssentialsDiscord")) {
            DiscordService essentialsDiscord = plugin.getServer().getServicesManager().load(DiscordService.class);

            if (essentialsDiscord != null) {
                sender = (message, player, event) ->
                        essentialsDiscord.sendMessage(MessageType.DefaultTypes.DEATH, ":skull: " + message, false);

                plugin.getLogger().info("Successfully loaded EssentialsDiscord support.");
            }
        } else if (plugin.getServer().getPluginManager().isPluginEnabled("DiscordSRV")) {
            plugin.getLogger().info("DiscordSRV support is not yet implemented. EssentialsDiscord is currently the only supported Discord plugin. This will be fixed in a future update.");
            // sender = (message, player, event) -> {
            //     SRVSender.send(message, player, event);
            //     plugin.getLogger().info("Successfully loaded DiscordSRV support.");
            // };
        }

        this.discord = sender;
    }

    public void sendDeathMessage(String message, Player player, PlayerDeathEvent event) {
        discord.send(message, player, event);
    }

    @FunctionalInterface
    private interface DiscordSender {
        void send(String message, Player player, PlayerDeathEvent event);
    }
}
