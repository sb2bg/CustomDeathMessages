package org.spartandevs.cdmr.customdeathmessages.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

public class BukkitKilledByEntityListener implements Listener {
    @EventHandler
    public void onPlayerDeath(EntityDamageByEntityEvent event) {
        if (!(event.getEntity() instanceof Player player)) {
            return;
        }

        if (player.getHealth() - event.getFinalDamage() > 0) {
            return;
        }


    }
}
