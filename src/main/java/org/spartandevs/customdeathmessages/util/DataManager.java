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

public class DataManager extends BaseDocumentManager {
    public DataManager(CustomDeathMessages plugin) {
        super(plugin);
    }

    @Override
    protected YamlDocument getDocument() {
        InputStream resource = plugin.getResource("data.yml");

        if (resource == null) {
            return null;
        }

        try {
            return YamlDocument.create(
                    new File(plugin.getDataFolder(), "data.yml"),
                    resource,
                    GeneralSettings.DEFAULT,
                    LoaderSettings.builder()
                            .setAutoUpdate(true).build(),
                    DumperSettings.DEFAULT,
                    UpdaterSettings.builder()
                            .setAutoSave(true)
                            .build());
        } catch (IOException e) {
            return null;
        }
    }
}
