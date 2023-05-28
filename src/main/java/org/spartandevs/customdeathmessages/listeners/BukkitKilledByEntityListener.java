package org.spartandevs.customdeathmessages.listeners;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.spartandevs.customdeathmessages.CustomDeathMessages;
import org.spartandevs.customdeathmessages.util.DeathCause;

public class BukkitKilledByEntityListener implements Listener {
    private final CustomDeathMessages plugin;

    public BukkitKilledByEntityListener(CustomDeathMessages plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerDeath(EntityDamageByEntityEvent event) {
        if (!(event.getEntity() instanceof Player player)) {
            return;
        }

        if (player.getHealth() - event.getFinalDamage() > 0) {
            return;
        }

        Entity entity = event.getDamager();
        DeathCause deathCause = DeathCause.fromEntityType(entity.getType());

        if (deathCause == DeathCause.UNKNOWN) {
            return;
        }

        if (deathCause == DeathCause.WITHER) {
            deathCause = DeathCause.WITHER_BOSS;
        }

        if (entity.getCustomName() != null) {
            deathCause = DeathCause.CUSTOM_NAMED_ENTITY;
        }

        plugin.getMessagePropagator().setDeathMessage(player.getUniqueId(), deathCause);
    }
}
