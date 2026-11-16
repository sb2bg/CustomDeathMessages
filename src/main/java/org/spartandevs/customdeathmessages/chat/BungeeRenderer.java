package org.spartandevs.customdeathmessages.chat;

import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;

import java.util.ArrayList;
import java.util.List;

/**
 * Renders messages with the BungeeCord chat API, for Spigot and Paper versions without Adventure.
 */
public final class BungeeRenderer {
    private BungeeRenderer() {
    }

    /**
     * @param originalHover legacy text shown when hovering the message, or {@code null}
     */
    @SuppressWarnings("deprecation") // the BaseComponent[] hover constructor is the only one on 1.8 - 1.15
    public static BaseComponent[] render(RichMessage message, String originalHover) {
        List<BaseComponent> components = new ArrayList<>();
        HoverEvent original = originalHover == null ? null
                : new HoverEvent(HoverEvent.Action.SHOW_TEXT, TextComponent.fromLegacyText(originalHover));
        HoverEvent item = null;

        for (RichMessage.Segment segment : message.getSegments()) {
            HoverEvent hover = original;

            if (segment.isWeapon() && message.getItem() != null) {
                if (item == null) {
                    item = ItemHovers.create(message.getItem());
                }

                hover = item;
            }

            for (BaseComponent component : TextComponent.fromLegacyText(segment.getText())) {
                if (hover != null) {
                    component.setHoverEvent(hover);
                }

                components.add(component);
            }
        }

        return components.toArray(new BaseComponent[0]);
    }
}
