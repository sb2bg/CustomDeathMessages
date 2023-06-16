package org.spartandevs.customdeathmessages.util;

import dev.dejvokep.boostedyaml.YamlDocument;
import org.spartandevs.customdeathmessages.CustomDeathMessages;

import java.io.IOException;

public abstract class BaseDocumentManager {
    protected final CustomDeathMessages plugin;
    protected final YamlDocument document;

    public BaseDocumentManager(CustomDeathMessages plugin) {
        this.plugin = plugin;
        this.document = getDocument();
    }

    protected abstract YamlDocument getDocument();

    interface ConfigAction {
        void run() throws IOException;
    }

    protected boolean sneaky(ConfigManager.ConfigAction action) {
        try {
            action.run();
            return true;
        } catch (IOException e) {
            plugin.getLogger().warning("Unable to save config.yml, changes will not take effect.");
            return false;
        }
    }
}
