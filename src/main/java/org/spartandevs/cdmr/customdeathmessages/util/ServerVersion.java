package org.spartandevs.cdmr.customdeathmessages.util;

public enum ServerVersion {
    ALL,
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

    public static ServerVersion getServerVersion() {
        String version = org.bukkit.Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3];
        System.out.println("Server version: " + version);

        return switch (version) {
            case "v1_8_R1", "v1_8_R2", "v1_8_R3" -> V8;
            case "v1_9_R1", "v1_9_R2" -> V9;
            case "v1_10_R1" -> V10;
            case "v1_11_R1" -> V11;
            case "v1_12_R1" -> V12;
            case "v1_13_R1", "v1_13_R2" -> V13;
            case "v1_14_R1" -> V14;
            case "v1_15_R1" -> V15;
            case "v1_16_R1", "v1_16_R2", "v1_16_R3" -> V16;
            case "v1_17_R1" -> V17;
            case "v1_18_R1", "v1_18_R2" -> V18;
            case "v1_19_R1", "v1_19_R2" -> V19;
            case "v1_20_R1" -> V20;
            case "v1_21_R1" -> V21;
            default -> UNKNOWN;
        };
    }

    public boolean isVersion(ServerVersion version) {
        if (this == ALL) {
            return true;
        }

        if (this == UNKNOWN) {
            return false;
        }

        return this == version;
    }

    public boolean isVersion(ServerVersion... versions) {
        if (this == ALL) {
            return true;
        }

        if (this == UNKNOWN) {
            return false;
        }

        for (ServerVersion version : versions) {
            if (this == version) {
                return true;
            }
        }

        return false;
    }

    public boolean isVersionOrHigher(ServerVersion version) {
        if (this == ALL) {
            return true;
        }

        if (this == UNKNOWN) {
            return false;
        }

        return this.ordinal() >= version.ordinal();
    }

    public boolean isVersionOrLower(ServerVersion version) {
        if (this == ALL) {
            return true;
        }

        return this.ordinal() <= version.ordinal();
    }
}
