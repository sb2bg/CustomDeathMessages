package org.spartandevs.cdmr.customdeathmessages.listeners;

import dev.dbassett.skullcreator.SkullCreator;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.spartandevs.cdmr.customdeathmessages.CustomDeathMessages;
import org.spartandevs.cdmr.customdeathmessages.chat.ChatColor;
import org.spartandevs.cdmr.customdeathmessages.chat.PlaceholderPopulator;
import org.spartandevs.cdmr.customdeathmessages.events.CustomPlayerDeathEvent;
import org.spartandevs.cdmr.customdeathmessages.util.ConfigManager;

public class CustomPlayerDeathListener implements Listener {
    private final CustomDeathMessages plugin;

    public CustomPlayerDeathListener(CustomDeathMessages plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerDeath(CustomPlayerDeathEvent event) {
        ConfigManager config = plugin.getConfigManager();

        ChatColor chatColor = new ChatColor(plugin);
        PlaceholderPopulator populator;
        ItemStack weapon = null;

        if (event.getKiller() == null) {
            populator = new PlaceholderPopulator(chatColor, event.getVictim());
        } else if (event.getKiller() instanceof Player killer) {
            weapon = getKillWeapon(killer);
            populator = new PlaceholderPopulator(chatColor, event.getVictim(), event.getKiller(), weapon);
        } else {
            populator = new PlaceholderPopulator(chatColor, event.getVictim(), event.getKiller());
        }

        if (config.doLightningStrike()) {
            event.getVictim().getWorld().strikeLightningEffect(event.getVictim().getLocation());
        }

        if (config.dropHead()) {
            ItemStack item = SkullCreator.itemFromUuid(event.getVictim().getUniqueId());
            event.getVictim().getWorld().dropItemNaturally(event.getVictim().getLocation(), item);
        }

        if (config.doPvpMessages() && event.getKiller() instanceof Player killer) {
            killer.sendMessage(populator.replace(config.getKillerMessage()));
            event.getVictim().sendMessage(populator.replace(config.getVictimMessage()));
        }

        if (config.isGlobalMessageEnabled()) {
            if (weapon != null && weapon.getType() == Material.AIR) {
                event.setDeathMessage(populator.replace(config.getMeleeMessage()));
                return;
            }

            event.setDeathMessage(populator.replace(config.getMessage(event.getDeathCause())));
        }
    }

    private static ItemStack getKillWeapon(Player killer) {
        ItemStack killWeapon = killer.getInventory().getItemInMainHand();

        if (killWeapon.getType() == Material.AIR) {
            return null;
        }

        return killWeapon;
    }
}
