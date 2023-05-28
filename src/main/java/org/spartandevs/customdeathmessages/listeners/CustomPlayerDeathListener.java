package org.spartandevs.customdeathmessages.listeners;

import dev.dbassett.skullcreator.SkullCreator;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.spartandevs.customdeathmessages.CustomDeathMessages;
import org.spartandevs.customdeathmessages.chat.DeathMessage;
import org.spartandevs.customdeathmessages.chat.JsonTransforms;
import org.spartandevs.customdeathmessages.chat.PlaceholderPopulator;
import org.spartandevs.customdeathmessages.events.CustomPlayerDeathEvent;
import org.spartandevs.customdeathmessages.util.ConfigManager;
import org.spartandevs.customdeathmessages.util.DeathCause;

import java.util.Objects;

public class CustomPlayerDeathListener implements Listener {
    private final CustomDeathMessages plugin;

    public CustomPlayerDeathListener(CustomDeathMessages plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerDeath(CustomPlayerDeathEvent event) {
        ConfigManager config = plugin.getConfigManager();

        PlaceholderPopulator populator;
        ItemStack weapon = null;

        if (event.getKiller() == null) {
            populator = new PlaceholderPopulator(plugin, event.getVictim());
        } else if (event.getKiller() instanceof Player killer) {
            weapon = getKillWeapon(killer);

            populator = config.isItemOnHoverEnabled()
                    ? new PlaceholderPopulator(plugin, event.getVictim(), event.getKiller())
                    : new PlaceholderPopulator(plugin, event.getVictim(), event.getKiller(), weapon);
        } else {
            populator = new PlaceholderPopulator(plugin, event.getVictim(), event.getKiller());
        }

        JsonTransforms jsonTransforms = new JsonTransforms(plugin, event.getOriginalDeathMessage(), weapon);

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
            DeathCause propagated = plugin.getMessagePropagator().getDeathMessage(event.getVictim().getUniqueId());

            DeathMessageResolver resolver = weapon != null && weapon.getType() == Material.AIR
                    ? config::getMeleeMessage
                    : (propagated == null
                    ? () -> config.getMessage(event.getDeathCause())
                    : () -> config.getMessage(propagated));

            String message = resolver.getMessage();

            if (message == null) {
                return;
            }

            DeathMessage.MessageType type = config.isItemOnHoverEnabled() || config.isOriginalOnHoverEnabled()
                    ? DeathMessage.MessageType.JSON
                    : DeathMessage.MessageType.STRING;

            event.setDeathMessage(new DeathMessage(message, populator, type), jsonTransforms);
        }
    }

    interface DeathMessageResolver {
        String getMessage();
    }

    private static ItemStack getKillWeapon(Player killer) {
        return killer.getInventory().getItemInMainHand();
    }
}
