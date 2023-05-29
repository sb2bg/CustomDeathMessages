package org.spartandevs.customdeathmessages.util;

import org.bukkit.entity.Entity;

public record MessageInfo(DeathCause deathCause, Entity killer) {
}
