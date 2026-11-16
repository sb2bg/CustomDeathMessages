package org.spartandevs.customdeathmessages.chat;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ChatColor {
    private static final Pattern HEX_PATTERN = Pattern.compile("&#([A-Fa-f0-9]{6})");
    private static final char COLOR_CHAR = '§';

    public static String translate(String message) {
        Matcher matcher = HEX_PATTERN.matcher(message);
        StringBuilder buffer = new StringBuilder(message.length() + 4 * 8);

        while (matcher.find()) {
            String group = matcher.group(1);

            buffer.append(message, 0, matcher.start())
                    .append(COLOR_CHAR).append("x").append(COLOR_CHAR)
                    .append(group.charAt(0)).append(COLOR_CHAR).append(group.charAt(1))
                    .append(COLOR_CHAR).append(group.charAt(2)).append(COLOR_CHAR)
                    .append(group.charAt(3)).append(COLOR_CHAR).append(group.charAt(4))
                    .append(COLOR_CHAR).append(group.charAt(5));

            message = message.substring(matcher.end());
            matcher.reset(message);
        }

        buffer.append(message);
        return org.bukkit.ChatColor.translateAlternateColorCodes('&', buffer.toString());
    }

}