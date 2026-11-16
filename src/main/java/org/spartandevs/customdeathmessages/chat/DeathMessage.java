package org.spartandevs.customdeathmessages.chat;

import net.md_5.bungee.api.chat.BaseComponent;

import java.util.regex.Pattern;

public class DeathMessage {
    private final Placeholders placeholders;
    private final String message;
    private final MessageType messageType;

    public DeathMessage(String message, Placeholders placeholders, MessageType messageType) {
        this.placeholders = placeholders;
        this.message = message;
        this.messageType = messageType;
    }

    public String getStringMessage() {
        return placeholders.apply(placeholders.getVictim(), message);
    }

    public BaseComponent[] getTextComponent(HoverTransforms transforms) {
        // fill in each part around %kill-weapon% separately so the weapon can become a hover component
        String[] parts = placeholders.prepare(placeholders.getVictim(), message).split(Pattern.quote(Placeholders.KILL_WEAPON), -1);

        for (int i = 0; i < parts.length; i++) {
            parts[i] = placeholders.insert(parts[i]);
        }

        return transforms.transform(String.join(transforms.handlesItem() ? Placeholders.KILL_WEAPON : "", parts));
    }

    public enum MessageType {
        STRING,
        JSON
    }

    public MessageType getMessageType() {
        return messageType;
    }
}
