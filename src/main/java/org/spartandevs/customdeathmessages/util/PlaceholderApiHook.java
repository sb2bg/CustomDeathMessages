package org.spartandevs.customdeathmessages.util;

import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Optional PlaceholderAPI support. PlaceholderAPI classes are only touched when the plugin is installed.
 */
public final class PlaceholderApiHook {
    /**
     * {@code %papi_victim_<placeholder>%} and {@code %papi_killer_<placeholder>%} resolve a placeholder for a
     * specific player instead of the message's default player.
     */
    private static final Pattern SCOPED_PLACEHOLDER = Pattern.compile("%papi_(victim|killer)_([^%]+)%");

    private final boolean enabled;

    public PlaceholderApiHook(Plugin plugin) {
        enabled = plugin.getServer().getPluginManager().isPluginEnabled("PlaceholderAPI");

        if (enabled) {
            plugin.getLogger().info("Successfully loaded PlaceholderAPI support.");
        }
    }

    /**
     * Resolves scoped placeholders, then regular placeholders for {@code context}.
     */
    public String apply(Player context, Player victim, Player killer, String text) {
        if (!enabled || text == null) {
            return text;
        }

        Matcher matcher = SCOPED_PLACEHOLDER.matcher(text);
        StringBuffer rewritten = new StringBuffer();

        while (matcher.find()) {
            Player scope = "victim".equals(matcher.group(1)) ? victim : killer;
            String replacement = scope == null ? "" : Bridge.set(scope, "%" + matcher.group(2) + "%");
            matcher.appendReplacement(rewritten, Matcher.quoteReplacement(replacement));
        }

        matcher.appendTail(rewritten);
        return context == null ? rewritten.toString() : Bridge.set(context, rewritten.toString());
    }

    /**
     * Isolates the PlaceholderAPI reference so this class loads without PlaceholderAPI installed.
     */
    private static final class Bridge {
        static String set(Player player, String text) {
            return PlaceholderAPI.setPlaceholders(player, text);
        }
    }
}
