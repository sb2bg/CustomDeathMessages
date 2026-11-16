package org.spartandevs.customdeathmessages.events;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.spartandevs.customdeathmessages.chat.DeathMessage;
import org.spartandevs.customdeathmessages.chat.HoverTransforms;

public class CustomPlayerDeathEvent extends Event {
    private static final HandlerList handlers = new HandlerList();
    private final DeathMessageSetter setDeathMessage;
    private final String messagePath;
    private final boolean melee;
    private final String originalDeathMessage;
    private final PlayerDeathEvent originalEvent;
    private final Entity killer;
    private final Player victim;

    public CustomPlayerDeathEvent(String messagePath, boolean melee, String originalDeathMessage, PlayerDeathEvent originalEvent, Entity killer, Player victim, DeathMessageSetter setDeathMessage) {
        this.messagePath = messagePath;
        this.melee = melee;
        this.originalDeathMessage = originalDeathMessage;
        this.originalEvent = originalEvent;
        this.setDeathMessage = setDeathMessage;
        this.killer = killer;
        this.victim = victim;
    }

    /**
     * @return the config message list describing this death
     */
    public String getMessagePath() {
        return messagePath;
    }

    /**
     * @return whether a player killed the victim with a direct (melee) hit
     */
    public boolean isMelee() {
        return melee;
    }

    public void setDeathMessage(DeathMessage deathMessage, HoverTransforms hoverTransforms, PlayerDeathEvent event) {
        setDeathMessage.setDeathMessage(victim, deathMessage, hoverTransforms, event);
    }

    public String getOriginalDeathMessage() {
        return originalDeathMessage;
    }

    public PlayerDeathEvent getEvent() {
        return originalEvent;
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
        void setDeathMessage(Player victim, DeathMessage deathMessage, HoverTransforms hoverTransforms, PlayerDeathEvent event);
    }

    public void setEmptyMessage() {
        setDeathMessage.setDeathMessage(victim, null, null, originalEvent);
    }
}
