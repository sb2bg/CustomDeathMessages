package org.spartandevs.customdeathmessages.util;

import dev.dejvokep.boostedyaml.YamlDocument;
import dev.dejvokep.boostedyaml.dvs.versioning.BasicVersioning;
import dev.dejvokep.boostedyaml.route.Route;
import dev.dejvokep.boostedyaml.settings.dumper.DumperSettings;
import dev.dejvokep.boostedyaml.settings.general.GeneralSettings;
import dev.dejvokep.boostedyaml.settings.loader.LoaderSettings;
import dev.dejvokep.boostedyaml.settings.updater.UpdaterSettings;
import org.spartandevs.customdeathmessages.CustomDeathMessages;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Consumer;

/**
 * Owns config.yml.
 * <p>
 * The live document is only ever replaced by a document that parsed successfully, so a broken file never
 * leaves the plugin running on defaults, and edits made through commands re-read the file first so they
 * never overwrite changes made by hand (or save defaults over a file that failed to parse).
 */
public class ConfigManager {
    private static final String FILE_NAME = "config.yml";

    private final CustomDeathMessages plugin;
    private final File file;
    private volatile YamlDocument document;

    public ConfigManager(CustomDeathMessages plugin) {
        this.plugin = plugin;
        this.file = new File(plugin.getDataFolder(), FILE_NAME);
    }

    /**
     * Loads (or reloads) config.yml from disk. On failure the previously loaded config stays active.
     *
     * @return {@code null} on success, otherwise a description of the problem
     */
    public synchronized String load() {
        try {
            document = open();
            validate();
            return null;
        } catch (IOException | RuntimeException e) {
            return describe(e);
        }
    }

    /**
     * Re-reads config.yml from disk, applies {@code change} and saves it.
     *
     * @return {@code null} on success, otherwise a description of the problem (nothing is saved)
     */
    public synchronized String modify(Consumer<YamlDocument> change) {
        try {
            YamlDocument fresh = open();
            change.accept(fresh);
            fresh.save();
            document = fresh;
            return null;
        } catch (IOException | RuntimeException e) {
            return describe(e);
        }
    }

    private YamlDocument open() throws IOException {
        InputStream defaults = plugin.getResource(FILE_NAME);

        if (defaults == null) {
            throw new IOException("The plugin jar does not contain " + FILE_NAME);
        }

        return YamlDocument.create(file, defaults, GeneralSettings.DEFAULT,
                LoaderSettings.builder().setAutoUpdate(true).build(), DumperSettings.DEFAULT, updater());
    }

    private static UpdaterSettings updater() {
        Map<Route, Route> v2 = new HashMap<>();
        v2.put(Route.fromString("original-hover-message"), Route.fromString("enable-original-on-hover"));
        v2.put(Route.fromString("enable-item-hover"), Route.fromString("enable-item-on-hover"));
        v2.put(Route.fromString("do-lightning"), Route.fromString("enable-lightning"));

        return UpdaterSettings.builder()
                .setAutoSave(true)
                .setVersioning(new BasicVersioning("config-version"))
                .addRelocations("2", v2)
                .build();
    }

    private void validate() {
        double chance = getHeadDropChance();

        if (chance < 0 || chance > 1) {
            plugin.getLogger().warning("drop-head-percentage should be a decimal between 0 and 1 (0.25 = 25%), but is " + chance);
        }
    }

    /**
     * Turns a load failure into a short, readable message. YAML syntax errors already describe the line and
     * column, so no stack trace is needed.
     */
    private static String describe(Exception e) {
        String message = e.getMessage();
        return message == null || message.trim().isEmpty() ? e.toString() : FILE_NAME + ": " + message.trim();
    }

    public String getMessage(String path) {
        List<String> messages = document.getStringList(path);

        if (messages == null || messages.isEmpty()) {
            plugin.getLogger().warning("No messages found for " + path + "! Check your " + FILE_NAME + ".");
            return null;
        }

        return messages.get(ThreadLocalRandom.current().nextInt(messages.size()));
    }

    public boolean hasMessageList(String path) {
        return document.isList(path);
    }

    /**
     * @return every death message list in the config
     */
    public Set<String> getMessagePaths() {
        Set<String> paths = new TreeSet<>();

        for (String route : document.getRoutesAsStrings(false)) {
            if (MessagePaths.isMessagePath(route) && document.isList(route)) {
                paths.add(route);
            }
        }

        return paths;
    }

    public List<String> listDeathMessages(String path) {
        return document.getStringList(path);
    }

    public int getMessagesCount(String path) {
        return document.getStringList(path).size();
    }

    public String getMeleeMessage() {
        return getMessage(MessagePaths.MELEE);
    }

    public boolean doLightningStrike() {
        return document.getBoolean("enable-lightning");
    }

    public boolean dropHead() {
        // strictly less than, so a chance of 0 can never drop a head
        return ThreadLocalRandom.current().nextDouble() < getHeadDropChance();
    }

    public double getHeadDropChance() {
        return document.getDouble("drop-head-percentage");
    }

    public String getHeadName() {
        return document.getString("head-name");
    }

    public boolean doPvpMessages() {
        return document.getBoolean("enable-pvp-messages");
    }

    public String getKillerMessage() {
        return document.getString("killer-message");
    }

    public String getVictimMessage() {
        return document.getString("victim-message");
    }

    public boolean isCheckUpdatesEnabled() {
        return document.getBoolean("enable-update-messages");
    }

    public boolean isGlobalMessageEnabled() {
        return document.getBoolean("enable-global-messages");
    }

    public boolean isOriginalOnHoverEnabled() {
        return document.getBoolean("enable-original-on-hover");
    }

    public boolean isItemOnHoverEnabled() {
        return document.getBoolean("enable-item-on-hover");
    }

    public double getCooldown() {
        return document.getDouble("message-cooldown");
    }

    public boolean isCustomNamedEntityMessageEnabled() {
        return document.getBoolean("enable-custom-name-entity-messages");
    }

    public boolean isDebugEnabled() {
        return document.getBoolean("developer-mode");
    }

    public String addCustomMessage(String path, String message) {
        return modify(doc -> {
            List<String> messages = new ArrayList<>(doc.getStringList(path));
            messages.add(message);
            doc.set(path, messages);
        });
    }

    public String removeCustomMessage(String path, int index) {
        return modify(doc -> {
            List<String> messages = new ArrayList<>(doc.getStringList(path));

            if (index >= messages.size()) {
                throw new IllegalStateException("Index " + index + " no longer exists; " + FILE_NAME + " changed on disk.");
            }

            messages.remove(index);
            doc.set(path, messages);
        });
    }

    public String set(String path, Object value) {
        return modify(doc -> doc.set(path, value));
    }

    public Set<String> getStringConfigPaths() {
        return getConfigPathsOfType(String.class);
    }

    public Set<String> getBoolConfigPaths() {
        return getConfigPathsOfType(Boolean.class);
    }

    public Set<String> getNumConfigPaths() {
        return getConfigPathsOfType(Number.class);
    }

    private Set<String> getConfigPathsOfType(Class<?> type) {
        Set<String> paths = new LinkedHashSet<>();

        for (String path : document.getRoutesAsStrings(false)) {
            if (type.isInstance(document.get(path))) {
                paths.add(path);
            }
        }

        return paths;
    }
}
