package org.spartandevs.cdmr.customdeathmessages.listeners;

import dev.dbassett.skullcreator.SkullCreator;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.spartandevs.cdmr.customdeathmessages.CustomDeathMessages;
import org.spartandevs.cdmr.customdeathmessages.chat.DeathMessage;
import org.spartandevs.cdmr.customdeathmessages.chat.JsonTransforms;
import org.spartandevs.cdmr.customdeathmessages.chat.PlaceholderPopulator;
import org.spartandevs.cdmr.customdeathmessages.events.CustomPlayerDeathEvent;
import org.spartandevs.cdmr.customdeathmessages.util.ConfigManager;

import java.util.Objects;

public class CustomPlayerDeathListener implements Listener {
    private final CustomDeathMessages plugin;

    public CustomPlayerDeathListener(CustomDeathMessages plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerDeath(CustomPlayerDeathEvent event) {
        ConfigManager config = plugin.getConfigManager();

        JsonTransforms jsonTransforms = JsonTransforms.getTransforms(plugin);
        PlaceholderPopulator populator;
        ItemStack weapon = null;

        if (event.getKiller() == null) {
            populator = new PlaceholderPopulator(plugin, event.getVictim());
        } else if (event.getKiller() instanceof Player killer) {
            weapon = getKillWeapon(killer);
            populator = new PlaceholderPopulator(plugin, event.getVictim(), event.getKiller(), weapon);
        } else {
            populator = new PlaceholderPopulator(plugin, event.getVictim(), event.getKiller());
        }

        if (config.doLightningStrike()) {
            event.getVictim().getWorld().strikeLightningEffect(event.getVictim().getLocation());
        }

        if (config.dropHead()) {
            ItemStack item = SkullCreator.itemFromUuid(event.getVictim().getUniqueId());
            Objects.requireNonNull(item.getItemMeta()).setDisplayName(populator.replace(config.getHeadName()));
            event.getVictim().getWorld().dropItemNaturally(event.getVictim().getLocation(), item);
        }

        if (config.doPvpMessages() && event.getKiller() instanceof Player killer) {
            killer.sendMessage(populator.replace(config.getKillerMessage()));
            event.getVictim().sendMessage(populator.replace(config.getVictimMessage()));
        }

        if (config.isGlobalMessageEnabled()) {
            DeathMessageResolver resolver = weapon != null && weapon.getType() == Material.AIR
                    ? config::getMeleeMessage
                    : () -> config.getMessage(event.getDeathCause());

            String message = resolver.getStrMessage();

            if (message == null) {
                return;
            }

            event.setDeathMessage(new DeathMessage(message, populator, jsonTransforms));
        }
    }

    interface DeathMessageResolver {
        String getStrMessage();
    }

    private static ItemStack getKillWeapon(Player killer) {
        ItemStack killWeapon = killer.getInventory().getItemInMainHand();

        if (killWeapon.getType() == Material.AIR) {
            return null;
        }

        return killWeapon;
    }
}
