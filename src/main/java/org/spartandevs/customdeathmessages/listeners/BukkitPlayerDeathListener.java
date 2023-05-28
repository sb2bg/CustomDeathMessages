package org.spartandevs.customdeathmessages.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.spartandevs.customdeathmessages.CustomDeathMessages;
import org.spartandevs.customdeathmessages.chat.DeathMessage;
import org.spartandevs.customdeathmessages.chat.JsonTransforms;
import org.spartandevs.customdeathmessages.events.CustomPlayerDeathEvent;
import org.spartandevs.customdeathmessages.util.DeathCause;

public class BukkitPlayerDeathListener implements Listener {
    public interface OriginalDeathMessageSetter {
        void setDeathMessage(String message);
    }

    private final CustomDeathMessages plugin;

    private OriginalDeathMessageSetter deathMessageSetter;

    public BukkitPlayerDeathListener(CustomDeathMessages plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player victim = event.getEntity();
        Player killer = victim.getKiller();

        if (victim.getLastDamageCause() == null) {
            return;
        }

        DeathCause deathCause = DeathCause.fromDamageCause(victim.getLastDamageCause().getCause());

        deathMessageSetter = event::setDeathMessage;

        CustomPlayerDeathEvent customPlayerDeathEvent = new CustomPlayerDeathEvent(
                deathCause, event.getDeathMessage(), killer, victim, this::setDeathMessage);

        plugin.getServer().getPluginManager().callEvent(customPlayerDeathEvent);
    }

    private void setDeathMessage(DeathMessage deathMessage, JsonTransforms jsonTransforms) {
        switch (deathMessage.getMessageType()) {
            case STRING -> deathMessageSetter.setDeathMessage(deathMessage.getStringMessage());
            case JSON -> {
                deathMessageSetter.setDeathMessage("");
                plugin.getServer().spigot().broadcast(deathMessage.getTextComponent(jsonTransforms));
            }
        }
    }
}