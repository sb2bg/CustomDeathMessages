package org.spartandevs.customdeathmessages.chat;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.PlayerDeathEvent;

/**
 * Renders messages with Adventure on Paper. Only load this class when {@link Platform#ADVENTURE_DEATH_MESSAGES}
 * is {@code true}.
 * <p>
 * Setting the death message on the event (instead of broadcasting it ourselves) lets the server handle the
 * {@code showDeathMessages} gamerule, console logging, other plugins (vanish, Discord bridges) and cancellation,
 * and Paper's own {@code ItemStack#asHoverEvent()} produces a correct item tooltip on every Paper version.
 */
public final class PaperRenderer {
    private static final LegacyComponentSerializer LEGACY = LegacyComponentSerializer.builder()
            .character(ChatColor.SECTION)
            .hexColors()
            .useUnusualXRepeatedCharacterHexFormat()
            .build();

    private PaperRenderer() {
    }

    /**
     * Replaces the event's death message with {@code message}. If {@code originalOnHover} is set, the vanilla
     * death message is shown when hovering it.
     */
    public static void setDeathMessage(PlayerDeathEvent event, RichMessage message, boolean originalOnHover) {
        Component original = originalOnHover ? event.deathMessage() : null;
        event.deathMessage(render(message, original));
    }

    /**
     * Sends {@code message} to a single player (used by the hover debug command).
     */
    public static void send(Player player, RichMessage message, String originalLegacy) {
        player.sendMessage(render(message, originalLegacy == null ? null : LEGACY.deserialize(originalLegacy)));
    }

    private static Component render(RichMessage message, Component originalHover) {
        TextComponent.Builder builder = Component.text();

        for (RichMessage.Segment segment : message.getSegments()) {
            Component component = LEGACY.deserialize(segment.getText());

            if (segment.isWeapon() && message.getItem() != null) {
                component = component.hoverEvent(message.getItem().asHoverEvent());
            } else if (originalHover != null) {
                component = component.hoverEvent(HoverEvent.showText(originalHover));
            }

            builder.append(component);
        }

        return builder.build();
    }
}
