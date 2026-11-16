package org.spartandevs.customdeathmessages.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.spartandevs.customdeathmessages.CustomDeathMessages;
import org.spartandevs.customdeathmessages.chat.ChatColor;
import org.spartandevs.customdeathmessages.util.UpdateChecker;

/**
 * Tells staff with {@code cdm.updates} about a new release when they join. The version is looked up
 * periodically in the background, never per join.
 */
public class UpdateNotifyListener implements Listener {
    private static final long JOIN_DELAY_TICKS = 40L;

    private final CustomDeathMessages plugin;
    private final UpdateChecker checker;

    public UpdateNotifyListener(CustomDeathMessages plugin, UpdateChecker checker) {
        this.plugin = plugin;
        this.checker = checker;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        if (!plugin.getConfigManager().isCheckUpdatesEnabled() || !player.hasPermission("cdm.updates")) {
            return;
        }

        String newVersion = checker.newerVersionThan(plugin.getDescription().getVersion());

        if (newVersion == null) {
            return;
        }

        // delay slightly so the message is not buried under join messages
        plugin.getServer().getScheduler().runTaskLater(plugin, () -> {
            if (!player.isOnline()) {
                return;
            }

            player.sendMessage(ChatColor.translate("&8-----------------------------------------------------"));
            player.sendMessage(ChatColor.translate("&bCustomDeathMessages: &7New version &f" + newVersion + " &7is available."));
            player.sendMessage(ChatColor.translate("&fhttps://www.spigotmc.org/resources/customdeathmessages-cdm.69605/"));
            player.sendMessage(ChatColor.translate("&8-----------------------------------------------------"));
        }, JOIN_DELAY_TICKS);
    }
}
