package org.spartandevs.customdeathmessages.util;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class CooldownManager {
    private final Map<UUID, Long> cooldowns = new HashMap<>();

    public void setCooldown(UUID uuid, double seconds) {
        cooldowns.put(uuid, System.currentTimeMillis() + (long) (seconds * 1000));
    }

    public boolean isOnCooldown(UUID uuid) {
        if (cooldowns.containsKey(uuid) && cooldowns.get(uuid) > System.currentTimeMillis()) {
            return true;
        }

        cooldowns.remove(uuid);
        return false;
    }

    public void clearCooldowns() {
        cooldowns.clear();
    }
}
