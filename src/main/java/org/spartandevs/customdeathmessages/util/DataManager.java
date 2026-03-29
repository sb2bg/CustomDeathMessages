package org.spartandevs.customdeathmessages.util;

import org.spartandevs.customdeathmessages.CustomDeathMessages;

public class DataManager extends BaseDocumentManager {
    public DataManager(CustomDeathMessages plugin) {
        super(plugin, "data.yml");
    }
}
