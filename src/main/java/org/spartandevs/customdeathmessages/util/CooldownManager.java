package org.spartandevs.customdeathmessages.util;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Tracks the per-player global death message cooldown.
 */
public class CooldownManager {
    private final Map<UUID, Long> cooldowns = new HashMap<>();

    public void setCooldown(UUID uuid, double seconds) {
        long now = System.currentTimeMillis();
        // drop expired entries so players who left do not accumulate
        cooldowns.values().removeIf(expiry -> expiry <= now);

        if (seconds > 0) {
            cooldowns.put(uuid, now + (long) (seconds * 1000));
        }
    }

    public boolean isOnCooldown(UUID uuid) {
        Long expiry = cooldowns.get(uuid);
        return expiry != null && expiry > System.currentTimeMillis();
    }

    public void clearCooldowns() {
        cooldowns.clear();
    }
}
