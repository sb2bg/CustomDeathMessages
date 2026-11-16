package org.spartandevs.customdeathmessages.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.spartandevs.customdeathmessages.CustomDeathMessages;
import org.spartandevs.customdeathmessages.chat.DeathMessage;
import org.spartandevs.customdeathmessages.chat.HoverTransforms;
import org.spartandevs.customdeathmessages.chat.Placeholders;
import org.spartandevs.customdeathmessages.events.CustomPlayerDeathEvent;
import org.spartandevs.customdeathmessages.util.ConfigManager;
import org.spartandevs.customdeathmessages.util.Items;
import org.spartandevs.customdeathmessages.util.PlayerHeads;

import java.util.logging.Level;

public class CustomPlayerDeathListener implements Listener {
    private final CustomDeathMessages plugin;

    public CustomPlayerDeathListener(CustomDeathMessages plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerDeath(CustomPlayerDeathEvent event) {
        if (plugin.getCooldownManager().isOnCooldown(event.getVictim().getUniqueId())) {
            event.setEmptyMessage();
            return;
        }

        ConfigManager config = plugin.getConfigManager();
        // the item held by the killer, for players and mobs alike (e.g. a skeleton's bow)
        ItemStack weapon = Items.mainHand(event.getKiller());
        Placeholders placeholders = new Placeholders(plugin.getPlaceholderApi(), event.getVictim(), event.getKiller(), weapon);
        HoverTransforms hoverTransforms = new HoverTransforms(plugin, event.getOriginalDeathMessage(), weapon);

        if (config.doLightningStrike()) {
            event.getVictim().getWorld().strikeLightningEffect(event.getVictim().getLocation());
        }

        if (config.dropHead()) {
            dropHead(event, placeholders.apply(event.getVictim(), config.getHeadName()));
        }

        if (config.doPvpMessages() && event.getKiller() instanceof Player) {
            Player killer = (Player) event.getKiller();
            killer.sendMessage(placeholders.apply(killer, config.getKillerMessage()));
            event.getVictim().sendMessage(placeholders.apply(event.getVictim(), config.getVictimMessage()));
        }

        if (config.isGlobalMessageEnabled()) {
            // the melee list is for players killed by a direct hit from an empty-handed player
            DeathMessageResolver resolver = event.isMelee() && Items.isEmpty(weapon)
                    ? config::getMeleeMessage
                    : () -> config.getMessage(event.getMessagePath());

            String message = resolver.getMessage();

            if (message == null) {
                // empty list: keep the vanilla message
                return;
            }

            if (message.isEmpty()) {
                // a blank message hides the death message entirely
                event.setEmptyMessage();
                return;
            }

            DeathMessage.MessageType type = config.isItemOnHoverEnabled() || config.isOriginalOnHoverEnabled()
                    ? DeathMessage.MessageType.JSON
                    : DeathMessage.MessageType.STRING;

            event.setDeathMessage(new DeathMessage(message, placeholders, type), hoverTransforms, event.getEvent());
        }
    }

    private void dropHead(CustomPlayerDeathEvent event, String name) {
        try {
            // Add to the event drops (rather than spawning it) so grave/death-chest plugins can capture it
            event.getEvent().getDrops().add(PlayerHeads.create(event.getVictim(), name));
        } catch (RuntimeException | LinkageError e) {
            // A failing head drop must never prevent the death message from being sent
            plugin.getLogger().log(Level.WARNING, "Could not create a player head for " + event.getVictim().getName(), e);
        }
    }

    interface DeathMessageResolver {
        String getMessage();
    }

}
