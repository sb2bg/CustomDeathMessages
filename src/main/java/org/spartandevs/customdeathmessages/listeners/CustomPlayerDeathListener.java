package org.spartandevs.customdeathmessages.listeners;

import dev.dbassett.skullcreator.SkullCreator;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.spartandevs.customdeathmessages.CustomDeathMessages;
import org.spartandevs.customdeathmessages.chat.DeathMessage;
import org.spartandevs.customdeathmessages.chat.HoverTransforms;
import org.spartandevs.customdeathmessages.chat.PlaceholderPopulator;
import org.spartandevs.customdeathmessages.events.CustomPlayerDeathEvent;
import org.spartandevs.customdeathmessages.util.ConfigManager;

public class CustomPlayerDeathListener implements Listener {
    private final CustomDeathMessages plugin;

    public CustomPlayerDeathListener(CustomDeathMessages plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerDeath(CustomPlayerDeathEvent event) {
        ConfigManager config = plugin.getConfigManager();
        ItemStack weapon = null;

        if (event.getKiller() instanceof Player) {
            Player killer = (Player) event.getKiller();
            weapon = getKillWeapon(killer);
        }

        PlaceholderPopulator populator = new PlaceholderPopulator(event.getVictim(),
                event.getKiller(), config.isItemOnHoverEnabled() ? null : weapon);
        HoverTransforms hoverTransforms = new HoverTransforms(plugin, event.getOriginalDeathMessage(), weapon);

        if (config.doLightningStrike()) {
            event.getVictim().getWorld().strikeLightningEffect(event.getVictim().getLocation());
        }

        if (config.dropHead()) {
            ItemStack item = SkullCreator.itemFromUuid(event.getVictim().getUniqueId());
            ItemMeta meta = item.getItemMeta();

            if (meta == null) {
                return;
            }

            meta.setDisplayName(populator.replace(config.getHeadName()));
            item.setItemMeta(meta);

            event.getVictim().getWorld().dropItemNaturally(event.getVictim().getLocation(), item);
        }

        if (config.doPvpMessages() && event.getKiller() instanceof Player) {
            Player killer = (Player) event.getKiller();
            killer.sendMessage(populator.replace(config.getKillerMessage()));
            event.getVictim().sendMessage(populator.replace(config.getVictimMessage()));
        }

        if (config.isGlobalMessageEnabled()) {
            DeathMessageResolver resolver = weapon != null && weapon.getType() == Material.AIR
                    ? config::getMeleeMessage
                    : () -> config.getMessage(event.getDeathCause());

            String message = resolver.getMessage();

            if (message == null) {
                return;
            }

            DeathMessage.MessageType type = config.isItemOnHoverEnabled() || config.isOriginalOnHoverEnabled()
                    ? DeathMessage.MessageType.JSON
                    : DeathMessage.MessageType.STRING;

            event.setDeathMessage(new DeathMessage(message, populator, type), hoverTransforms);
        }
    }

    interface DeathMessageResolver {
        String getMessage();
    }

    private static ItemStack getKillWeapon(Player killer) {
        return killer.getInventory().getItemInMainHand();
    }
}
