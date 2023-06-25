package org.spartandevs.customdeathmessages.util

import github.scarsz.discordsrv.DiscordSRV
import net.essentialsx.api.v2.services.discord.DiscordService
import net.essentialsx.api.v2.services.discord.MessageType
import org.spartandevs.customdeathmessages.CustomDeathMessages

class DiscordManager(plugin: CustomDeathMessages) {
    private var discord: (message: String) -> Unit = {}

    init {
        if (plugin.server.pluginManager.isPluginEnabled("EssentialsDiscord")) {
            val essDiscord = plugin.server.servicesManager.load(DiscordService::class.java)

            essDiscord?.let {
                discord = { message ->
                    it.sendMessage(MessageType.DefaultTypes.DEATH, ":skull: $message", false)
                }

                plugin.logger.info("Successfully loaded EssentialsDiscord support.")
            }
        } else if (plugin.server.pluginManager.isPluginEnabled("DiscordSRV")) {
            val srv = DiscordSRV.getPlugin()
            val channelName = srv.getOptionalChannel("deaths")
            val messageFormat = srv.getMessageFromConfiguration("MinecraftPlayerDeathMessage")
            
            if (messageFormat == null) {
                return
            }
        }
    }

    fun sendDeathMessage(message: String) {
        discord(message)
    }
}
