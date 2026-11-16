package org.spartandevs.customdeathmessages.chat;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Color code handling: {@code &} codes, {@code &#RRGGBB} hex colors and color state tracking.
 */
public final class ChatColor {
    public static final char SECTION = '§';
    private static final Pattern HEX_PATTERN = Pattern.compile("&#([A-Fa-f0-9]{6})");
    private static final String COLOR_CODES = "0123456789abcdef";
    private static final String FORMAT_CODES = "klmno";
    private static final String ALL_CODES = COLOR_CODES + FORMAT_CODES + "rx";
    /**
     * RGB values of the legacy colors {@code 0-f}, used to approximate hex colors before 1.16.
     */
    private static final int[] LEGACY_RGB = {
            0x000000, 0x0000AA, 0x00AA00, 0x00AAAA, 0xAA0000, 0xAA00AA, 0xFFAA00, 0xAAAAAA,
            0x555555, 0x5555FF, 0x55FF55, 0x55FFFF, 0xFF5555, 0xFF55FF, 0xFFFF55, 0xFFFFFF
    };
    private static final boolean HEX_SUPPORTED = hexSupported();

    private ChatColor() {
    }

    /**
     * Translates {@code &} color codes and {@code &#RRGGBB} hex colors. Servers older than 1.16 cannot show hex
     * colors, so there they become the closest legacy color.
     */
    public static String translate(String message) {
        return translate(message, HEX_SUPPORTED);
    }

    static String translate(String message, boolean hexSupported) {
        Matcher matcher = HEX_PATTERN.matcher(message);
        StringBuffer buffer = new StringBuffer(message.length() + 32);

        while (matcher.find()) {
            String hex = matcher.group(1);
            matcher.appendReplacement(buffer, Matcher.quoteReplacement(hexSupported ? hexCode(hex) : nearestLegacyCode(hex)));
        }

        matcher.appendTail(buffer);
        return translateAmpersands(buffer.toString());
    }

    private static String hexCode(String hex) {
        StringBuilder code = new StringBuilder(14).append(SECTION).append('x');

        for (char c : hex.toCharArray()) {
            code.append(SECTION).append(Character.toLowerCase(c));
        }

        return code.toString();
    }

    static String nearestLegacyCode(String hex) {
        int rgb = Integer.parseInt(hex, 16);
        int best = 0;
        long bestDistance = Long.MAX_VALUE;

        for (int i = 0; i < LEGACY_RGB.length; i++) {
            long distance = distance(rgb, LEGACY_RGB[i]);

            if (distance < bestDistance) {
                bestDistance = distance;
                best = i;
            }
        }

        return "" + SECTION + COLOR_CODES.charAt(best);
    }

    private static long distance(int a, int b) {
        long dr = ((a >> 16) & 0xFF) - ((b >> 16) & 0xFF);
        long dg = ((a >> 8) & 0xFF) - ((b >> 8) & 0xFF);
        long db = (a & 0xFF) - (b & 0xFF);
        return dr * dr + dg * dg + db * db;
    }

    /**
     * Same as Bukkit's {@code translateAlternateColorCodes('&', text)}.
     */
    private static String translateAmpersands(String text) {
        char[] chars = text.toCharArray();

        for (int i = 0; i < chars.length - 1; i++) {
            if (chars[i] == '&' && ALL_CODES.indexOf(Character.toLowerCase(chars[i + 1])) >= 0) {
                chars[i] = SECTION;
                chars[i + 1] = Character.toLowerCase(chars[i + 1]);
            }
        }

        return new String(chars);
    }

    /**
     * @return the color and formatting codes in effect at the end of {@code text}, so that text appended in a
     * separate chat component can continue with the same style
     */
    public static String lastColors(String text) {
        String color = "";
        StringBuilder formats = new StringBuilder();

        for (int i = 0; i < text.length() - 1; i++) {
            if (text.charAt(i) != SECTION) {
                continue;
            }

            char code = Character.toLowerCase(text.charAt(i + 1));

            if (code == 'x' && isHexSequence(text, i)) {
                color = text.substring(i, i + 14);
                formats.setLength(0);
                i += 13;
                continue;
            }

            if (COLOR_CODES.indexOf(code) >= 0) {
                color = "" + SECTION + code;
                formats.setLength(0);
            } else if (FORMAT_CODES.indexOf(code) >= 0) {
                formats.append(SECTION).append(code);
            } else if (code == 'r') {
                color = "";
                formats.setLength(0);
            }

            i++;
        }

        return color + formats;
    }

    private static boolean isHexSequence(String text, int start) {
        if (start + 14 > text.length()) {
            return false;
        }

        for (int i = 0; i < 6; i++) {
            if (text.charAt(start + 2 + 2 * i) != SECTION || Character.digit(text.charAt(start + 3 + 2 * i), 16) < 0) {
                return false;
            }
        }

        return true;
    }

    private static boolean hexSupported() {
        try {
            // added to the chat API together with hex color support in 1.16
            net.md_5.bungee.api.ChatColor.class.getMethod("of", String.class);
            return true;
        } catch (NoSuchMethodException | LinkageError e) {
            return false;
        }
    }
}
