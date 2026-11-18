package org.spartandevs.customdeathmessages.chat;

/**
 * Detects which chat APIs the server provides. Deliberately free of Adventure references so it can be
 * loaded on any server.
 */
public final class Platform {
    /**
     * Testing aid: forces the BungeeCord chat code path on Paper.
     */
    private static final String FORCE_BUNGEE_PROPERTY = "customdeathmessages.forceBungeeChat";

    /**
     * Whether the server is Paper 1.16.5+ with {@code PlayerDeathEvent#deathMessage(Component)} and
     * {@code ItemStack#asHoverEvent()}.
     */
    public static final boolean ADVENTURE_DEATH_MESSAGES = detectAdventure();

    private Platform() {
    }

    private static boolean detectAdventure() {
        if (Boolean.getBoolean(FORCE_BUNGEE_PROPERTY)) {
            return false;
        }

        try {
            Class<?> component = Class.forName("net.kyori.adventure.text.Component");
            Class<?> hoverSource = Class.forName("net.kyori.adventure.text.event.HoverEventSource");
            Class.forName("net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer");
            org.bukkit.event.entity.PlayerDeathEvent.class.getMethod("deathMessage", component);
            return hoverSource.isAssignableFrom(org.bukkit.inventory.ItemStack.class);
        } catch (ReflectiveOperationException | LinkageError e) {
            return false;
        }
    }
}
