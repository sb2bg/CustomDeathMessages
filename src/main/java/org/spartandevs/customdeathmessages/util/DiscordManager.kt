package org.spartandevs.customdeathmessages.util

import net.essentialsx.api.v2.services.discord.DiscordService
import net.essentialsx.api.v2.services.discord.MessageType
import org.bukkit.entity.Player
import org.bukkit.event.entity.PlayerDeathEvent
import org.spartandevs.customdeathmessages.CustomDeathMessages


class DiscordManager(plugin: CustomDeathMessages) {
    private var discord: (message: String, player: Player, event: PlayerDeathEvent) -> Unit = { _, _, _ -> }

    init {
        if (plugin.server.pluginManager.isPluginEnabled("EssentialsDiscord")) {
            val essDiscord = plugin.server.servicesManager.load(DiscordService::class.java)

            essDiscord?.let {
                discord = { message, _, _ ->
                    it.sendMessage(MessageType.DefaultTypes.DEATH, ":skull: $message", false)
                }

                plugin.logger.info("Successfully loaded EssentialsDiscord support.")
            }
        } else if (plugin.server.pluginManager.isPluginEnabled("DiscordSRV")) {
            discord = { message, player, event ->
                SRVSender.send(message, player, event);
                plugin.logger.info("Successfully loaded DiscordSRV support.")
            }
        }
    }

    fun sendDeathMessage(message: String, player: Player, event: PlayerDeathEvent) {
        discord(message, player, event)
    }
}
