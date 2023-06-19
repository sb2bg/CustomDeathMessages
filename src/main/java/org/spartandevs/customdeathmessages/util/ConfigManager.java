package org.spartandevs.customdeathmessages.util;

import dev.dejvokep.boostedyaml.YamlDocument;
import dev.dejvokep.boostedyaml.dvs.versioning.BasicVersioning;
import dev.dejvokep.boostedyaml.route.Route;
import dev.dejvokep.boostedyaml.settings.dumper.DumperSettings;
import dev.dejvokep.boostedyaml.settings.general.GeneralSettings;
import dev.dejvokep.boostedyaml.settings.loader.LoaderSettings;
import dev.dejvokep.boostedyaml.settings.updater.UpdaterSettings;
import org.bstats.bukkit.Metrics;
import org.bstats.charts.SimplePie;
import org.spartandevs.customdeathmessages.CustomDeathMessages;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.text.MessageFormat;
import java.util.*;

public class ConfigManager extends BaseDocumentManager {
    private Set<String> keys;

    public ConfigManager(CustomDeathMessages plugin) {
        super(plugin);
        loadConfig();
    }

    private void loadConfig() {
        registerStatistics();

        keys = plugin.getConfig().getValues(true).keySet();

        if (document.getDouble("drop-head-percentage") > 1) {
            plugin.getLogger().warning("Config misconfiguration: drop-head-chance should be a decimal between 0 and 1");
        }
    }

    @Override
    protected YamlDocument getDocument() {
        InputStream resource = plugin.getResource("config.yml");

        if (resource == null) {
            warnAndDisable();
            return null;
        }

        UpdaterSettings updater = UpdaterSettings.builder()
                .setAutoSave(true)
                .setVersioning(new BasicVersioning("config-version"))
                .addRelocations("2", new HashMap<Route, Route>() {{
                    put(Route.fromString("original-hover-message"), Route.fromString("enable-original-on-hover"));
                    put(Route.fromString("enable-item-hover"), Route.fromString("enable-item-on-hover"));
                    put(Route.fromString("do-lightning"), Route.fromString("enable-lightning"));
                }})
                .build();

        try {
            return YamlDocument.create(
                    new File(plugin.getDataFolder(), "config.yml"),
                    resource,
                    GeneralSettings.DEFAULT,
                    LoaderSettings.builder().setAutoUpdate(true).build(),
                    DumperSettings.DEFAULT,
                    updater);
        } catch (IOException e) {
            warnAndDisable();
            return null;
        }
    }

    public boolean reloadConfig() {
        return sneaky(document::reload) && sneaky(document::save);
    }

    private void warnAndDisable() {
        plugin.getLogger().warning("Unable to load config.yml from jar, disabling plugin.");
        plugin.getServer().getPluginManager().disablePlugin(plugin);
    }

    public String getMessage(DeathCause cause) {
        List<String> messages = document.getStringList(cause.getPath());

        if (invalidCollection(messages, cause.getPath())) {
            return null;
        }

        return messages.get(new Random().nextInt(messages.size()));
    }

    public boolean doLightningStrike() {
        return document.getBoolean("enable-lightning");
    }

    public boolean dropHead() {
        return new Random().nextDouble() <= document.getDouble("drop-head-chance");
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

    public String getMeleeMessage() {
        List<String> meleeMessages = document.getStringList("melee-death-messages");

        if (invalidCollection(meleeMessages, "melee-death-messages")) {
            return null;
        }

        return meleeMessages.get(new Random().nextInt(meleeMessages.size()));
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

    private void registerStatistics() {
        Metrics metrics = new Metrics(plugin, 7287);
        metrics.addCustomChart(new SimplePie("head_drop_percentage", () -> String.valueOf(document.getDouble("drop-head-chance"))));
        metrics.addCustomChart(new SimplePie("give_killer_speed", () -> getBooleanString(document.getBoolean("give-killer-speed")))); // legacy
        metrics.addCustomChart(new SimplePie("heart_sucker", () -> getBooleanString(document.getBoolean("heart-sucker")))); // legacy
        metrics.addCustomChart(new SimplePie("enable_lightning", () -> getBooleanString(document.getBoolean("enable-lightning"))));
        metrics.addCustomChart(new SimplePie("enable_global_messages", () -> getBooleanString(document.getBoolean("enable-global-messages"))));
        metrics.addCustomChart(new SimplePie("enable_pvp_messages", () -> getBooleanString(document.getBoolean("enable-pvp-messages"))));
        metrics.addCustomChart(new SimplePie("enable_entity_name_messages", () -> getBooleanString(document.getBoolean("enable-entity-name-messages"))));
        metrics.addCustomChart(new SimplePie("enable_original_hover_message", () -> getBooleanString(document.getBoolean("enable-original-on-hover"))));
        metrics.addCustomChart(new SimplePie("enable_item_tooltip_message", () -> getBooleanString(document.getBoolean("enable-item-on-hover"))));
    }

    private static String getBooleanString(boolean value) {
        return value ? "Enabled" : "Disabled";
    }

    public void addCustomMessage(DeathCause cause, String message) {
        List<String> configMessages = document.getStringList(cause.getPath());
        configMessages.add(message);
        document.set(cause.getPath(), configMessages);
        sneaky(document::save);

    }

    public void removeCustomMessage(DeathCause cause, int index) {
        List<String> configMessages = document.getStringList(cause.getPath());
        configMessages.remove(index);
        document.set(cause.getPath(), configMessages);
        sneaky(document::save);
    }

    public void setBoolean(String path, boolean value) {
        document.set(path, value);
        sneaky(document::save);
    }

    public void setDouble(String path, double value) {
        document.set(path, value);
        sneaky(document::save);
    }

    public void setString(String path, String value) {
        document.set(path, value);
        sneaky(document::save);
    }

    public int getMessagesCount(DeathCause cause) {
        return document.getStringList(cause.getPath()).size();
    }

    public Set<String> getStringConfigPaths() {
        Set<String> paths = new HashSet<>();

        for (String path : keys) {
            if (document.get(path) instanceof String) {
                paths.add(path);
            }
        }

        return paths;
    }

    public Set<String> getBoolConfigPaths() {
        Set<String> paths = new HashSet<>();

        for (String path : keys) {
            if (document.get(path) instanceof Boolean) {
                paths.add(path);
            }
        }

        return paths;
    }

    public Set<String> getNumConfigPaths() {
        Set<String> paths = new HashSet<>();

        for (String path : keys) {
            if (document.get(path) instanceof Double || document.get(path) instanceof Integer) {
                paths.add(path);
            }
        }

        return paths;
    }

    public List<String> listDeathMessages(DeathCause cause) {
        return document.getStringList(cause.getPath());
    }

    private boolean invalidCollection(List<?> collection, String name) {
        if (collection == null || collection.isEmpty()) {
            plugin.getLogger().warning(MessageFormat.format("No messages found for {0}! Check your document.yml.", name));
            return true;
        }

        return false;
    }
}
