package org.spartandevs.customdeathmessages.util;

import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.spartandevs.customdeathmessages.CustomDeathMessages;

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.UUID;

public class MessagePropagator {
    private final CustomDeathMessages plugin;

    public MessagePropagator(CustomDeathMessages plugin) {
        this.plugin = plugin;
    }

    private final HashMap<UUID, MessageInfo> propagator = new HashMap<>();
    private final HashMap<UUID, BukkitTask> cancellers = new HashMap<>();

    public MessageInfo getDeathMessage(UUID uuid) {
        if (cancellers.containsKey(uuid)) {
            cancellers.remove(uuid).cancel();
        }

        return propagator.remove(uuid);
    }

    public void setDeathMessage(UUID uuid, MessageInfo messageInfo) {
        propagator.put(uuid, messageInfo);

        BukkitTask task = new BukkitRunnable() {
            @Override
            public void run() {
                propagator.remove(uuid);
                plugin.getLogger().warning(MessageFormat.format("Death message for {0} was not propagated in time", uuid));
            }
        }.runTaskLaterAsynchronously(plugin, 20 * 5);

        cancellers.put(uuid, task);
    }

    public void clear() {
        propagator.clear();
        cancellers.clear();
    }
}
