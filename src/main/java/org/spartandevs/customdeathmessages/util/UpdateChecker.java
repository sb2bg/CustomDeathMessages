package org.spartandevs.customdeathmessages.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

/**
 * Looks up the latest released version on SpigotMC.
 */
public final class UpdateChecker {
    private static final int TIMEOUT_MILLIS = 5000;

    private final String url;
    private volatile String latestVersion;

    public UpdateChecker(int resourceId) {
        this.url = "https://api.spigotmc.org/legacy/update.php?resource=" + resourceId;
    }

    /**
     * Fetches the latest version. Blocks, so call it off the main thread.
     */
    public void check() throws IOException {
        HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
        connection.setConnectTimeout(TIMEOUT_MILLIS);
        connection.setReadTimeout(TIMEOUT_MILLIS);
        connection.setRequestProperty("User-Agent", "CustomDeathMessages update checker");

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8))) {
            String line = reader.readLine();

            if (line != null && !line.trim().isEmpty()) {
                latestVersion = line.trim();
            }
        } finally {
            connection.disconnect();
        }
    }

    /**
     * @return the latest released version if it is newer than {@code currentVersion}, otherwise {@code null}
     */
    public String newerVersionThan(String currentVersion) {
        String latest = latestVersion;
        return latest != null && compareVersions(latest, currentVersion) > 0 ? latest : null;
    }

    /**
     * Compares dotted version numbers numerically ({@code 2.0.10 > 2.0.9}), ignoring a leading {@code v} and
     * any suffix such as {@code -SNAPSHOT}.
     */
    public static int compareVersions(String a, String b) {
        String[] left = numericParts(a);
        String[] right = numericParts(b);

        for (int i = 0; i < Math.max(left.length, right.length); i++) {
            int l = i < left.length ? parse(left[i]) : 0;
            int r = i < right.length ? parse(right[i]) : 0;

            if (l != r) {
                return Integer.compare(l, r);
            }
        }

        return 0;
    }

    private static String[] numericParts(String version) {
        String trimmed = version.trim();

        if (trimmed.startsWith("v") || trimmed.startsWith("V")) {
            trimmed = trimmed.substring(1);
        }

        int suffix = trimmed.indexOf('-');
        return (suffix >= 0 ? trimmed.substring(0, suffix) : trimmed).split("\\.");
    }

    private static int parse(String part) {
        StringBuilder digits = new StringBuilder();

        for (char c : part.toCharArray()) {
            if (!Character.isDigit(c)) {
                break;
            }

            digits.append(c);
        }

        try {
            return digits.length() == 0 ? 0 : Integer.parseInt(digits.toString());
        } catch (NumberFormatException e) {
            return Integer.MAX_VALUE;
        }
    }
}
