package org.spartandevs.customdeathmessages.chat;

import org.spartandevs.cdmr.customdeathmessages.CustomDeathMessages;
import org.spartandevs.cdmr.customdeathmessages.util.ServerVersion;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ChatColor {
    private static final Pattern HEX_PATTERN = Pattern.compile("&#([A-Fa-f0-9]{6})");
    private static final char COLOR_CHAR = '§';
    private final CustomDeathMessages plugin;

    public ChatColor(CustomDeathMessages plugin) {
        this.plugin = plugin;
    }

    private String translate(String message) {
        Matcher matcher = ChatColor.HEX_PATTERN.matcher(message);
        StringBuilder buffer = new StringBuilder(message.length() + 4 * 8);

        while (matcher.find()) {
            String group = matcher.group(1);

            matcher.appendReplacement(buffer, COLOR_CHAR + "x"
                    + COLOR_CHAR + group.charAt(0) + COLOR_CHAR + group.charAt(1)
                    + COLOR_CHAR + group.charAt(2) + COLOR_CHAR + group.charAt(3)
                    + COLOR_CHAR + group.charAt(4) + COLOR_CHAR + group.charAt(5)
            );
        }

        return org.bukkit.ChatColor.translateAlternateColorCodes('&', matcher.appendTail(buffer).toString());
    }

    public String translateAlternateColorCodes(String textToTranslate) {
        if (plugin.getServerVersion().isVersionOrHigher(ServerVersion.V16)) {
            return translate(textToTranslate);
        }

        return org.bukkit.ChatColor.translateAlternateColorCodes('&', textToTranslate);
    }
}