package org.spartandevs.customdeathmessages.chat;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.spartandevs.customdeathmessages.util.PlaceholderApiManager;

public class PlaceholderContext {
    private final PlaceholderApiManager placeholderApiManager;
    private final Player victim;
    private final Entity killer;
    private final Player killerPlayer;
    private final ItemStack item;

    public PlaceholderContext(PlaceholderApiManager placeholderApiManager, Player victim, Entity killer, ItemStack item) {
        this.placeholderApiManager = placeholderApiManager;
        this.victim = victim;
        this.killer = killer;
        killerPlayer = killer instanceof Player ? (Player) killer : null;
        this.item = item;
    }

    public PlaceholderApiManager getPlaceholderApiManager() {
        return placeholderApiManager;
    }

    public Player getVictim() {
        return victim;
    }

    public Entity getKiller() {
        return killer;
    }

    public Player getKillerPlayer() {
        return killerPlayer;
    }

    public ItemStack getItem() {
        return item;
    }

    public Player getDefaultContext() {
        return victim;
    }
}
