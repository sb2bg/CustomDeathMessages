package org.spartandevs.customdeathmessages.events;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.spartandevs.customdeathmessages.chat.DeathMessage;
import org.spartandevs.customdeathmessages.chat.JsonTransforms;
import org.spartandevs.customdeathmessages.util.DeathCause;

public class CustomPlayerDeathEvent extends Event {
    private static final HandlerList handlers = new HandlerList();
    private DeathMessageSetter setDeathMessage;
    private DeathCause deathCause;
    private final String originalDeathMessage;
    private Entity killer;
    private Player victim;

    public CustomPlayerDeathEvent(DeathCause deathCause, String originalDeathMessage, Entity killer, Player victim, DeathMessageSetter setDeathMessage) {
        this.deathCause = deathCause;
        this.originalDeathMessage = originalDeathMessage;
        this.setDeathMessage = setDeathMessage;
        this.killer = killer;
        this.victim = victim;
    }

    public DeathCause getDeathCause() {
        return deathCause;
    }

    public void setDeathMessage(DeathMessage deathMessage, JsonTransforms jsonTransforms) {
        setDeathMessage.setDeathMessage(deathMessage, jsonTransforms);
    }

    public String getOriginalDeathMessage() {
        return originalDeathMessage;
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
        void setDeathMessage(DeathMessage deathMessage, JsonTransforms jsonTransforms);
    }
}
