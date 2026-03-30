package org.spartandevs.customdeathmessages.util;

import org.spartandevs.customdeathmessages.CustomDeathMessages;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;

public class MessagePropagator {
    private static final long PROPAGATION_TIMEOUT_MS = 60_000L;

    private final CustomDeathMessages plugin;
    private final HashMap<UUID, PropagatedMessage> propagator = new HashMap<>();

    public MessagePropagator(CustomDeathMessages plugin) {
        this.plugin = plugin;
    }

    public MessageInfo getDeathMessage(UUID uuid) {
        cleanupExpiredEntries();

        PropagatedMessage propagatedMessage = propagator.remove(uuid);

        if (propagatedMessage == null) {
            return null;
        }

        if (propagatedMessage.isExpired(System.currentTimeMillis())) {
            logExpiry(uuid);
            return null;
        }

        return propagatedMessage.getMessageInfo();
    }

    public void setDeathMessage(UUID uuid, MessageInfo messageInfo) {
        cleanupExpiredEntries();
        propagator.put(uuid, new PropagatedMessage(messageInfo, System.currentTimeMillis() + PROPAGATION_TIMEOUT_MS));
    }

    public void clear() {
        propagator.clear();
    }

    private void cleanupExpiredEntries() {
        long now = System.currentTimeMillis();
        Iterator<Map.Entry<UUID, PropagatedMessage>> iterator = propagator.entrySet().iterator();

        while (iterator.hasNext()) {
            Map.Entry<UUID, PropagatedMessage> entry = iterator.next();

            if (!entry.getValue().isExpired(now)) {
                continue;
            }

            iterator.remove();
            logExpiry(entry.getKey());
        }
    }

    private void logExpiry(UUID uuid) {
        plugin.getLogger().warning("Death message for " + uuid + " expired before a death event consumed it");
    }

    private static final class PropagatedMessage {
        private final MessageInfo messageInfo;
        private final long expiresAtMillis;

        private PropagatedMessage(MessageInfo messageInfo, long expiresAtMillis) {
            this.messageInfo = messageInfo;
            this.expiresAtMillis = expiresAtMillis;
        }

        private MessageInfo getMessageInfo() {
            return messageInfo;
        }

        private boolean isExpired(long now) {
            return now > expiresAtMillis;
        }
    }
}
