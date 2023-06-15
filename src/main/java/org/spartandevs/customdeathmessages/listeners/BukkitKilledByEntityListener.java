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
        if (!(event.getEntity() instanceof Player)) {
            return;
        }

        Player player = (Player) event.getEntity();

        if (player.getHealth() - event.getFinalDamage() > 0) {
            return;
        }

        Entity entity = event.getDamager();
        DeathCause deathCause = DeathCause.fromEntityType(entity.getType(), plugin);

        if (entity instanceof Projectile) {
            Projectile proj = (Projectile) entity;

            if (proj.getShooter() != null) {
                entity = (Entity) proj.getShooter();

                if (entity instanceof Player) {
                    deathCause = DeathCause.PLAYER;
                } else if (entity instanceof Skeleton) {
                    deathCause = DeathCause.SKELETON;
                } else if (entity instanceof Husk) {
                    deathCause = DeathCause.HUSK;
                } else if (entity instanceof Stray) {
                    deathCause = DeathCause.STRAY;
                } else if (entity instanceof WitherSkeleton) {
                    deathCause = DeathCause.WITHER_SKELETON;
                } else if (entity instanceof Pillager) {
                    deathCause = DeathCause.PILLAGER;
                } else if (entity instanceof Drowned) {
                    deathCause = DeathCause.DROWNED;
                }
            }
        }

        // WITHER is used as an effect and entity damage cause, so we need to differentiate
        // here, WITHER is the boss, so we use WITHER_BOSS
        if (deathCause == DeathCause.WITHER) {
            deathCause = DeathCause.WITHER_BOSS;
        }

        // Avoid ❤ in custom named entities (McMMO)
        if (entity.getCustomName() != null && !entity.getCustomName().contains("❤")) {
            deathCause = DeathCause.CUSTOM_NAMED_ENTITY;
        }

        plugin.getMessagePropagator().setDeathMessage(player.getUniqueId(), new MessageInfo(deathCause, entity));
    }
}
