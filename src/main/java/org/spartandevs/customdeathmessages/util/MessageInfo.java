package org.spartandevs.customdeathmessages.util;

import org.bukkit.entity.Entity;

public final class MessageInfo {
    private final DeathCause deathCause;
    private final Entity killer;

    public MessageInfo(DeathCause deathCause, Entity killer) {
        this.deathCause = deathCause;
        this.killer = killer;
    }

    public DeathCause deathCause() {
        return deathCause;
    }

    public Entity killer() {
        return killer;
    }
}
