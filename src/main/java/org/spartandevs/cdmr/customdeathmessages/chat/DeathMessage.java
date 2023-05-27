package org.spartandevs.cdmr.customdeathmessages.chat;

import net.md_5.bungee.api.chat.TextComponent;

public class DeathMessage {
    private String message;
    private TextComponent textComponent;

    public DeathMessage(String message, PlaceholderPopulator populator, JsonTransforms transforms) {
        this.message = populator.replace(message);
        this.textComponent = transforms == JsonTransforms.NONE ? null : transforms.transform(this.message);
    }

    public String getStringMessage() {
        return message;
    }

    public TextComponent getTextComponent() {
        return textComponent;
    }

    public enum MessageType {
        STRING,
        JSON
    }

    public MessageType getMessageType() {
        return textComponent == null ? MessageType.STRING : MessageType.JSON;
    }
}
