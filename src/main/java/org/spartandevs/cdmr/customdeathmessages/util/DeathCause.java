package org.spartandevs.cdmr.customdeathmessages.util;

public enum DeathCause {
    UNKNOWN("unknown-messages");

    private final String path;

    DeathCause(String path) {
        this.path = path;
    }

    public String getPath() {
        return path;
    }
}
