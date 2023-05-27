package org.spartandevs.cdmr.customdeathmessages.util;

import org.bukkit.entity.EntityType;
import org.bukkit.event.entity.EntityDamageEvent;

enum Range {
    ABOVE,
    BELOW,
    EQUAL,
}

public enum DeathCause {

    UNKNOWN("unknown-messages", ServerVersion.ALL, Range.EQUAL),
    CUSTOM("unknown-messages", ServerVersion.ALL, Range.EQUAL),
    PLAYER("player-messages", ServerVersion.ALL, Range.EQUAL),
    ENTITY("entity-messages", ServerVersion.ALL, Range.EQUAL),
    BLOCK("falling-block-messages", ServerVersion.ALL, Range.EQUAL),
    VOID("void-messages", ServerVersion.ALL, Range.EQUAL),
    FALL("fall-damage-messages", ServerVersion.ALL, Range.EQUAL),
    FIRE("fire-messages", ServerVersion.ALL, Range.EQUAL),
    FIRE_TICK("fire-tick-messages", ServerVersion.ALL, Range.EQUAL),
    SUICIDE("suicide-messages", ServerVersion.ALL, Range.EQUAL),
    LAVA("lava-messages", ServerVersion.ALL, Range.EQUAL),
    DROWNING("drowning-messages", ServerVersion.ALL, Range.EQUAL),
    STARVATION("starvation-messages", ServerVersion.ALL, Range.EQUAL),
    //    POISON("potion-messages", ServerVersion.ALL, Range.EQUAL),
    MAGIC("potion-messages", ServerVersion.ALL, Range.EQUAL),
    WITHER("wither-messages", ServerVersion.ALL, Range.EQUAL),
    //    ANVIL("anvil-messages", ServerVersion.ALL, Range.EQUAL),
    FALLING_BLOCK("falling-block-messages", ServerVersion.ALL, Range.EQUAL),
    //    THORNS("thorns-messages", ServerVersion.ALL, Range.EQUAL),
//    DRAGON_BREATH("dragon-breath-messages", ServerVersion.ALL, Range.EQUAL),
    SUFFOCATION("suffocation-messages", ServerVersion.ALL, Range.EQUAL);


    private final String path;
    private final ServerVersion messageVersion;
    private final Range range;

    DeathCause(String path, ServerVersion messageVersion, Range range) {
        this.path = path;
        this.messageVersion = messageVersion;
        this.range = range;
    }

    public String getPath() {
        return path;
    }

    public boolean validForVersion(ServerVersion serverVersion) {
        return switch (range) {
            case ABOVE -> messageVersion.isVersionOrHigher(serverVersion);
            case BELOW -> messageVersion.isVersionOrLower(serverVersion);
            case EQUAL -> messageVersion.isVersion(serverVersion);
        };
    }

    public static DeathCause fromDamageCause(EntityDamageEvent.DamageCause cause) {
        if (cause == null) {
            return DeathCause.UNKNOWN;
        }

        for (DeathCause deathCause : DeathCause.values()) {
            if (deathCause.name().equals(cause.name())) {
                return deathCause;
            }
        }

        return DeathCause.UNKNOWN;
    }

    public static DeathCause fromEntityType(EntityType entityType) {
        if (entityType == null) {
            return DeathCause.UNKNOWN;
        }

        for (DeathCause deathCause : DeathCause.values()) {
            if (deathCause.name().equals(entityType.name())) {
                return deathCause;
            }
        }

        return DeathCause.UNKNOWN;
    }
}
