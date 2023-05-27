package org.spartandevs.cdmr.customdeathmessages.util;

import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.spartandevs.cdmr.customdeathmessages.CustomDeathMessages;
import org.spartandevs.cdmr.customdeathmessages.chat.DeathMessage;

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.UUID;

public class MessagePropagator {
    public class MessageInfo {
        private final DeathMessage deathMessage;
        private final boolean customNamedEntity;

        public MessageInfo(DeathMessage deathMessage, boolean customNamedEntity) {
            this.deathMessage = deathMessage;
            this.customNamedEntity = customNamedEntity;
        }

        public DeathMessage getDeathMessage() {
            return deathMessage;
        }

        public boolean isCustomNamedEntity() {
            return customNamedEntity;
        }
    }

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
}
