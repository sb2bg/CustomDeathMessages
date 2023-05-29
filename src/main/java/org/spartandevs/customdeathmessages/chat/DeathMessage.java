package org.spartandevs.customdeathmessages.chat;

import net.md_5.bungee.api.chat.TextComponent;

public class DeathMessage {
    private final PlaceholderPopulator populator;
    private final String message;
    private final MessageType messageType;

    public DeathMessage(String message, PlaceholderPopulator populator, MessageType messageType) {
        this.populator = populator;
        this.message = message;
        this.messageType = messageType;
    }

    public String getStringMessage() {
        return populator.replace(message);
    }

    public TextComponent getTextComponent(HoverTransforms transforms) {
        return transforms.transform(getStringMessage());
    }

    public enum MessageType {
        STRING,
        JSON
    }

    public MessageType getMessageType() {
        return messageType;
    }
}
