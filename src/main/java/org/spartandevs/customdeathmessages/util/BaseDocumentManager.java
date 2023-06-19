package org.spartandevs.customdeathmessages.util;

import dev.dejvokep.boostedyaml.YamlDocument;
import dev.dejvokep.boostedyaml.settings.dumper.DumperSettings;
import dev.dejvokep.boostedyaml.settings.general.GeneralSettings;
import dev.dejvokep.boostedyaml.settings.loader.LoaderSettings;
import dev.dejvokep.boostedyaml.settings.updater.UpdaterSettings;
import org.spartandevs.customdeathmessages.CustomDeathMessages;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

public abstract class BaseDocumentManager {
    protected final CustomDeathMessages plugin;
    protected YamlDocument document;
    private final String resource;

    public BaseDocumentManager(CustomDeathMessages plugin, String resource) {
        this.plugin = plugin;
        this.resource = resource;
        loadDocument();
    }

    protected void loadDocument() {
        InputStream resourceStream = plugin.getResource(resource);

        if (resourceStream == null) {
            errorAndDisable("Could not find resource " + resource);
            return;
        }

        if (!sneaky(() -> document = YamlDocument.create(
                new File(plugin.getDataFolder(), resource),
                resourceStream,
                GeneralSettings.DEFAULT,
                LoaderSettings.builder().setAutoUpdate(true).build(),
                DumperSettings.DEFAULT,
                updater()))) {
            errorAndDisable("Could not load document " + resource);
        }
    }

    protected UpdaterSettings updater() {
        return UpdaterSettings.DEFAULT;
    }

    interface ConfigAction {
        void run() throws IOException;
    }

    protected boolean sneaky(ConfigAction action) {
        try {
            action.run();
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    protected void save() {
        if (sneaky(document::save)) {
            return;
        }

        plugin.getLogger().severe("Could not save " + resource);
    }

    public void reload() {
        if (sneaky(document::reload)) {
            return;
        }

        plugin.getLogger().severe("Could not reload " + resource);
    }

    protected void errorAndDisable(String message) {
        plugin.getLogger().severe(message);
        plugin.getServer().getPluginManager().disablePlugin(plugin);
    }
}

