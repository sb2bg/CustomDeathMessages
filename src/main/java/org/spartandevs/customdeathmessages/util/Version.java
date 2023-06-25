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
        String version = org.bukkit.Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3];

        switch (version) {
            case "v1_8_R1":
            case "v1_8_R2":
            case "v1_8_R3":
                return V8;
            case "v1_9_R1":
            case "v1_9_R2":
                return V9;
            case "v1_10_R1":
                return V10;
            case "v1_11_R1":
                return V11;
            case "v1_12_R1":
                return V12;
            case "v1_13_R1":
            case "v1_13_R2":
                return V13;
            case "v1_14_R1":
                return V14;
            case "v1_15_R1":
                return V15;
            case "v1_16_R1":
            case "v1_16_R2":
            case "v1_16_R3":
                return V16;
            case "v1_17_R1":
                return V17;
            case "v1_18_R1":
            case "v1_18_R2":
                return V18;
            case "v1_19_R1":
            case "v1_19_R2":
                return V19;
            case "v1_20_R1":
                return V20;
            case "v1_21_R1":
                return V21;
            default:
                return UNKNOWN;
        }
    }

    public boolean isVersionOrHigherThan(Version version) {
        return this.ordinal() >= version.ordinal();
    }

    public boolean isHigherThan(Version version) {
        return this.ordinal() > version.ordinal();
    }
}
