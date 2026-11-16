package org.spartandevs.customdeathmessages.chat;

import org.bukkit.inventory.ItemStack;
import org.spartandevs.customdeathmessages.util.Items;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;

/**
 * A filled-in death message split into segments, so that {@code %kill-weapon%} can become its own chat
 * component with an item tooltip.
 * <p>
 * Every segment starts with the color and formatting in effect at that point of the template, so splitting
 * the message into components does not lose colors ({@code &b%kill-weapon%} keeps the weapon aqua, and the
 * text after it continues in the template's style rather than the item name's).
 */
public final class RichMessage {
    private static final String RESET = ChatColor.SECTION + "r";

    private final List<Segment> segments;
    private final ItemStack item;

    private RichMessage(List<Segment> segments, ItemStack item) {
        this.segments = Collections.unmodifiableList(segments);
        this.item = item;
    }

    /**
     * @param weapon     the killer's weapon, or {@code null}
     * @param itemHover  whether the weapon should show its item tooltip on hover
     */
    public static RichMessage create(Placeholders placeholders, String template, ItemStack weapon, boolean itemHover) {
        String[] parts = placeholders.prepare(placeholders.getVictim(), template).split(Pattern.quote(Placeholders.KILL_WEAPON), -1);
        String weaponName = Items.isEmpty(weapon) ? "" : Items.displayName(weapon);
        List<Segment> segments = new ArrayList<>();
        StringBuilder before = new StringBuilder();

        for (int i = 0; i < parts.length; i++) {
            String part = placeholders.insert(parts[i]);
            String style = ChatColor.lastColors(before.toString());

            if (i > 0 && !weaponName.isEmpty()) {
                segments.add(new Segment(style + weaponName, true));
            }

            segments.add(new Segment(style + part, false));
            before.append(part);
        }

        return new RichMessage(segments, itemHover && !Items.isEmpty(weapon) ? weapon : null);
    }

    public List<Segment> getSegments() {
        return segments;
    }

    /**
     * @return the item to show when hovering the weapon segments, or {@code null} for no item tooltip
     */
    public ItemStack getItem() {
        return item;
    }

    /**
     * @return the message as plain legacy text, without hover events
     */
    public String toLegacy() {
        StringBuilder legacy = new StringBuilder();

        for (Segment segment : segments) {
            // segments are separate components in rich form, so styles must not leak from one into the next
            if (legacy.length() > 0) {
                legacy.append(RESET);
            }

            legacy.append(segment.getText());
        }

        return legacy.toString();
    }

    public static final class Segment {
        private final String text;
        private final boolean weapon;

        Segment(String text, boolean weapon) {
            this.text = text;
            this.weapon = weapon;
        }

        /**
         * @return legacy formatted text
         */
        public String getText() {
            return text;
        }

        public boolean isWeapon() {
            return weapon;
        }
    }
}
