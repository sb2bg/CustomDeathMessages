package org.spartandevs.customdeathmessages.chat;

import net.md_5.bungee.api.chat.BaseComponent;
import org.spartandevs.customdeathmessages.CustomDeathMessages;

public class DeathMessage {
    private final CustomDeathMessages plugin;
    private final PlaceholderPopulator populator;
    private final HoverTransforms hoverTransforms;
    private final String message;
    private final MessageType messageType;

    public DeathMessage(CustomDeathMessages plugin, String message, PlaceholderPopulator populator, HoverTransforms hoverTransforms, MessageType messageType) {
        this.plugin = plugin;
        this.populator = populator;
        this.hoverTransforms = hoverTransforms;
        this.message = message;
        this.messageType = messageType;
    }

    public String getStringMessage() {
        return populator.replace(message);
    }

    public BaseComponent[] getTextComponent() {
        if (hoverTransforms == null) {
            plugin.getLogger().warning("Hover transforms are null for message: " + message);
            return new BaseComponent[0];
        }

        return hoverTransforms.transform(getStringMessage());
    }

    public enum MessageType {
        STRING,
        JSON
    }

    public MessageType getMessageType() {
        return messageType;
    }
}
