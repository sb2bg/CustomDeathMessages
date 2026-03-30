package org.spartandevs.customdeathmessages.util;

import org.bukkit.entity.Player;
import org.spartandevs.customdeathmessages.CustomDeathMessages;

import java.lang.reflect.Method;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PlaceholderApiManager {
    private static final String PLACEHOLDER_API_PLUGIN = "PlaceholderAPI";
    private static final Pattern SCOPED_PLACEHOLDER = Pattern.compile("%papi_(victim|killer)_([^%]+)%");

    private final CustomDeathMessages plugin;
    private Method setPlaceholders;

    public PlaceholderApiManager(CustomDeathMessages plugin) {
        this.plugin = plugin;
        hook();
    }

    private void hook() {
        if (!plugin.getServer().getPluginManager().isPluginEnabled(PLACEHOLDER_API_PLUGIN)) {
            return;
        }

        try {
            Class<?> placeholderApiClass = Class.forName("me.clip.placeholderapi.PlaceholderAPI");
            setPlaceholders = placeholderApiClass.getMethod("setPlaceholders", Player.class, String.class);
            plugin.getLogger().info("Successfully loaded PlaceholderAPI support.");
        } catch (ReflectiveOperationException e) {
            plugin.getLogger().warning("Detected PlaceholderAPI, but failed to hook into it.");
        }
    }

    public String replace(Player player, String message) {
        if (player == null || message == null || setPlaceholders == null) {
            return message;
        }

        try {
            return (String) setPlaceholders.invoke(null, player, message);
        } catch (ReflectiveOperationException e) {
            plugin.getLogger().warning("PlaceholderAPI replacement failed for " + player.getName() + ".");
            return message;
        }
    }

    public String replaceScoped(Player victim, Player killer, String message) {
        if (message == null || setPlaceholders == null) {
            return message;
        }

        Matcher matcher = SCOPED_PLACEHOLDER.matcher(message);
        StringBuffer rewritten = new StringBuffer();

        while (matcher.find()) {
            Player context = "victim".equals(matcher.group(1)) ? victim : killer;
            String replacement = "";

            if (context != null) {
                replacement = replace(context, "%" + matcher.group(2) + "%");
            }

            matcher.appendReplacement(rewritten, Matcher.quoteReplacement(replacement));
        }

        matcher.appendTail(rewritten);
        return rewritten.toString();
    }
}
