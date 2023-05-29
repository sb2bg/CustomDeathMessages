package org.spartandevs.customdeathmessages.listeners;

import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.spartandevs.customdeathmessages.CustomDeathMessages;
import org.spartandevs.customdeathmessages.util.DeathCause;
import org.spartandevs.customdeathmessages.util.MessageInfo;

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

        if (entity instanceof Arrow arrow) {
            if (arrow.getShooter() instanceof Player) {
                deathCause = DeathCause.PLAYER;
            }

            if (arrow.getShooter() instanceof Skeleton) {
                deathCause = DeathCause.SKELETON;
            }

            if (arrow.getShooter() instanceof Husk) {
                deathCause = DeathCause.HUSK;
            }

            if (arrow.getShooter() instanceof Stray) {
                deathCause = DeathCause.STRAY;
            }

            if (arrow.getShooter() instanceof WitherSkeleton) {
                deathCause = DeathCause.WITHER_SKELETON;
            }

            if (arrow.getShooter() instanceof Pillager) {
                deathCause = DeathCause.PILLAGER;
            }

            if (arrow.getShooter() != null) {
                entity = (Entity) arrow.getShooter();
            }
        }

        if (deathCause == DeathCause.UNKNOWN) {
            return;
        }

        if (deathCause == DeathCause.WITHER) {
            deathCause = DeathCause.WITHER_BOSS;
        }

        if (entity.getCustomName() != null) {
            deathCause = DeathCause.CUSTOM_NAMED_ENTITY;
        }

        plugin.getMessagePropagator().setDeathMessage(player.getUniqueId(), new MessageInfo(deathCause, entity));
    }
}
