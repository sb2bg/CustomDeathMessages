package org.spartandevs.customdeathmessages.events;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.spartandevs.cdmr.customdeathmessages.chat.DeathMessage;
import org.spartandevs.cdmr.customdeathmessages.util.DeathCause;

public class CustomPlayerDeathEvent extends Event {
    private static final HandlerList handlers = new HandlerList();
    private DeathCause deathCause;
    private DeathMessageSetter setDeathMessage;
    private Entity killer;
    private Player victim;

    public CustomPlayerDeathEvent(DeathCause deathCause, Entity killer, Player victim, DeathMessageSetter setDeathMessage) {
        this.deathCause = deathCause;
        this.setDeathMessage = setDeathMessage;
        this.killer = killer;
        this.victim = victim;
    }

    public DeathCause getDeathCause() {
        return deathCause;
    }

    public void setDeathMessage(DeathMessage deathMessage) {
        setDeathMessage.setDeathMessage(deathMessage);
    }

    public Entity getKiller() {
        return killer;
    }

    public Player getVictim() {
        return victim;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public interface DeathMessageSetter {
        void setDeathMessage(DeathMessage deathMessage);
    }
}
