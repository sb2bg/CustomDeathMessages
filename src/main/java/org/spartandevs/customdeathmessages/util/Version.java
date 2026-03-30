package org.spartandevs.customdeathmessages.util;

public enum Version {
    V8,
    V9,
    V10,
    V11,
    V12,
    V13,
    V14,
    V15,
    V16,
    V17,
    V18,
    V19,
    V20,
    V21,
    UNKNOWN;

    public static final Version SERVER_VERSION = getServerVersion();

    private static Version getServerVersion() {
        String version = org.bukkit.Bukkit.getBukkitVersion().split("-")[0];

        if (version.startsWith("1.8")) {
            return V8;
        }

        if (version.startsWith("1.9")) {
            return V9;
        }

        if (version.startsWith("1.10")) {
            return V10;
        }

        if (version.startsWith("1.11")) {
            return V11;
        }

        if (version.startsWith("1.12")) {
            return V12;
        }

        if (version.startsWith("1.13")) {
            return V13;
        }

        if (version.startsWith("1.14")) {
            return V14;
        }

        if (version.startsWith("1.15")) {
            return V15;
        }

        if (version.startsWith("1.16")) {
            return V16;
        }

        if (version.startsWith("1.17")) {
            return V17;
        }

        if (version.startsWith("1.18")) {
            return V18;
        }

        if (version.startsWith("1.19")) {
            return V19;
        }

        if (version.startsWith("1.20")) {
            return V20;
        }

        if (version.startsWith("1.21")) {
            return V21;
        }

        return UNKNOWN;
    }

    public boolean isVersionOrHigher(Version version) {
        return this.ordinal() >= version.ordinal();
    }
}
