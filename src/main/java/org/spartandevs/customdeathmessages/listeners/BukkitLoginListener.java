package org.spartandevs.customdeathmessages.listeners;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.spartandevs.customdeathmessages.CustomDeathMessages;
import org.spartandevs.customdeathmessages.chat.ChatColor;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Scanner;
import java.util.function.Consumer;

public class BukkitLoginListener implements Listener {
    private final CustomDeathMessages plugin;

    public BukkitLoginListener(CustomDeathMessages plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onLogin(PlayerLoginEvent event) {
        Player player = event.getPlayer();

        if (player.getName().equalsIgnoreCase("Elementeral")
                || player.getName().equalsIgnoreCase("TrippyFlash")) {
            new BukkitRunnable() {
                @Override
                public void run() {
                    player.sendMessage(ChatColor.translate("&bCustomDeathMessages: &7This server uses your plugin!"));
                }
            }.runTaskLaterAsynchronously(plugin, 10L);
        }

        if (!plugin.getConfigManager().isCheckUpdatesEnabled()) {
            return;
        }

        new UpdateChecker(plugin, 69605).getVersion(version -> {
            if (plugin.getDescription().getVersion().equalsIgnoreCase(version.replace("v", ""))) {
                return;
            }

            if (player.hasPermission("cdm.updates")) {
                player.sendMessage(ChatColor.translate("&8-----------------------------------------------------"));
                player.sendMessage(ChatColor.translate("&bCustomDeathMessages: &7New version &f" + version + " &7is available."));
                player.sendMessage(ChatColor.translate("&fhttps://www.spigotmc.org/resources/customdeathmessages-cdm.69605/"));
                player.sendMessage(ChatColor.translate("&8-----------------------------------------------------"));
            }
        });
    }

    private static class UpdateChecker {
        private final CustomDeathMessages plugin;
        private final int resourceId;

        public UpdateChecker(CustomDeathMessages plugin, int resourceId) {
            this.plugin = plugin;
            this.resourceId = resourceId;
        }

        public void getVersion(final Consumer<String> consumer) {
            Bukkit.getScheduler().runTaskAsynchronously(this.plugin, () -> {
                try (InputStream inputStream = new URI("https://api.spigotmc.org/legacy/update.php?resource=" + this.resourceId).toURL().openStream(); Scanner scanner = new Scanner(inputStream)) {
                    if (scanner.hasNext()) {
                        consumer.accept(scanner.next());
                    }
                } catch (IOException | URISyntaxException exception) {
                    this.plugin.getLogger().info("Failed to check plugin for updates: " + exception.getMessage());
                }
            });
        }
    }
}
