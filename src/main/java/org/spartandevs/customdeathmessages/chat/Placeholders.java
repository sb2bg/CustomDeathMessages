package org.spartandevs.customdeathmessages.chat;

import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.spartandevs.customdeathmessages.util.Items;
import org.spartandevs.customdeathmessages.util.PlaceholderApiHook;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Fills in message templates.
 * <p>
 * Templates are processed in a fixed order so that text controlled by players (names, nicknames, item names)
 * can never inject color codes or placeholders: PlaceholderAPI and color codes are applied to the template
 * first, and only then are the built-in placeholder values inserted, verbatim, in a single pass.
 */
public final class Placeholders {
    public static final String KILL_WEAPON = "%kill-weapon%";
    private static final Pattern PLACEHOLDER = Pattern.compile("%([a-z-]+)%");

    private final PlaceholderApiHook placeholderApi;
    private final Player victim;
    private final Player killerPlayer;
    private final Map<String, String> values = new HashMap<>();

    /**
     * @param killer the entity credited with the kill, or {@code null}
     * @param weapon the item the killer was holding, or {@code null}
     */
    public Placeholders(PlaceholderApiHook placeholderApi, Player victim, Entity killer, ItemStack weapon) {
        this.placeholderApi = placeholderApi;
        this.victim = victim;
        this.killerPlayer = killer instanceof Player ? (Player) killer : null;

        values.put("victim", victim.getName());
        values.put("victim-nick", victim.getDisplayName());
        putLocation("victim", victim.getLocation());

        values.put("killer", killer == null ? "" : killer.getName());
        values.put("entity-name", killer == null ? "" : killer.getName());
        values.put("killer-nick", killerPlayer == null ? (killer == null ? "" : killer.getName()) : killerPlayer.getDisplayName());
        putLocation("killer", killer == null ? null : killer.getLocation());

        values.put("kill-weapon", Items.isEmpty(weapon) ? "" : Items.displayName(weapon));
    }

    private void putLocation(String prefix, Location location) {
        values.put(prefix + "-x", location == null ? "" : String.valueOf(location.getBlockX()));
        values.put(prefix + "-y", location == null ? "" : String.valueOf(location.getBlockY()));
        values.put(prefix + "-z", location == null ? "" : String.valueOf(location.getBlockZ()));
        values.put(prefix + "-world", location == null || location.getWorld() == null ? "" : location.getWorld().getName());
    }

    public Player getVictim() {
        return victim;
    }

    /**
     * Fills in a template completely.
     *
     * @param context the player PlaceholderAPI placeholders without a scope are resolved for
     */
    public String apply(Player context, String template) {
        return insert(prepare(context, template));
    }

    /**
     * Resolves PlaceholderAPI placeholders and translates color codes, leaving the built-in placeholders in place.
     */
    public String prepare(Player context, String template) {
        return ChatColor.translate(placeholderApi.apply(context, victim, killerPlayer, template));
    }

    /**
     * Inserts the built-in placeholder values into a {@link #prepare prepared} template. Unknown placeholders
     * are left untouched.
     */
    public String insert(String prepared) {
        Matcher matcher = PLACEHOLDER.matcher(prepared);
        StringBuffer result = new StringBuffer(prepared.length());

        while (matcher.find()) {
            String value = values.get(matcher.group(1));
            matcher.appendReplacement(result, Matcher.quoteReplacement(value == null ? matcher.group() : value));
        }

        matcher.appendTail(result);
        return result.toString();
    }
}
