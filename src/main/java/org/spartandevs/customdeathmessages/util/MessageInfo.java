package org.spartandevs.customdeathmessages.util;

import org.bukkit.entity.Entity;

public final class MessageInfo {
    private final DeathCause deathCause;
    private final Entity killer;

    public MessageInfo(DeathCause deathCause, Entity killer) {
        this.deathCause = deathCause;
        this.killer = killer;
    }

    public DeathCause getDeathCause() {
        return deathCause;
    }

    public Entity getKiller() {
        return killer;
    }
}
