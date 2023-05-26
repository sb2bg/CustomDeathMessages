package org.spartandevs.cdmr.customdeathmessages.chat;

import net.md_5.bungee.api.chat.TextComponent;
import org.spartandevs.cdmr.customdeathmessages.CustomDeathMessages;

public class DeathMessage {
    private final String message;
    private final CustomDeathMessages plugin;

    public DeathMessage(String message, PlaceholderPopulator populator, CustomDeathMessages plugin) {
        this.plugin = plugin;
        this.message = populator.replace(message);
    }

    public String getMessage() {
        return message;
    }

    public TextComponent getDeathMessage() {
        TextComponent textComponent = new TextComponent(plugin.translateColorCodes(message));

        plugin.getServer().spigot().broadcast(textComponent);
        return textComponent;
    }
}
