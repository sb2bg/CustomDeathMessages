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

public class ConfigManager {
    private final CustomDeathMessages plugin;
    private final YamlDocument config;
    private Set<String> keys;

    public ConfigManager(CustomDeathMessages plugin) {
        this.plugin = plugin;
        this.config = getConfig();
    }

    public void loadConfig() {
        registerStatistics();

        keys = plugin.getConfig().getValues(true).keySet();

        if (config.getDouble("drop-head-percentage") > 1) {
            plugin.getLogger().warning("Config misconfiguration: drop-head-chance should be a decimal between 0 and 1");
        }
    }

    private YamlDocument getConfig() {
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
        return sneaky(config::reload) && sneaky(config::save);
    }

    private void warnAndDisable() {
        plugin.getLogger().warning("Unable to load config.yml from jar, disabling plugin.");
        plugin.getServer().getPluginManager().disablePlugin(plugin);
    }

    public String getMessage(DeathCause cause) {
        List<String> messages = config.getStringList(cause.getPath());

        if (invalidCollection(messages, cause.getPath())) {
            return null;
        }

        return messages.get(new Random().nextInt(messages.size()));
    }

    public boolean doLightningStrike() {
        return config.getBoolean("enable-lightning");
    }

    public boolean dropHead() {
        return new Random().nextDouble() <= config.getDouble("drop-head-chance");
    }

    public String getHeadName() {
        return config.getString("head-name");
    }

    public boolean doPvpMessages() {
        return config.getBoolean("enable-pvp-messages");
    }

    public String getKillerMessage() {
        return config.getString("killer-message");
    }

    public String getVictimMessage() {
        return config.getString("victim-message");
    }

    public boolean isCheckUpdatesEnabled() {
        return config.getBoolean("enable-update-messages");
    }

    public boolean isGlobalMessageEnabled() {
        return config.getBoolean("enable-global-messages");
    }

    public String getMeleeMessage() {
        List<String> meleeMessages = config.getStringList("melee-death-messages");

        if (invalidCollection(meleeMessages, "melee-death-messages")) {
            return null;
        }

        return meleeMessages.get(new Random().nextInt(meleeMessages.size()));
    }

    public boolean isOriginalOnHoverEnabled() {
        return config.getBoolean("enable-original-on-hover");
    }

    public boolean isItemOnHoverEnabled() {
        return config.getBoolean("enable-item-on-hover");
    }

    public double getCooldown() {
        return config.getDouble("message-cooldown");
    }

    public boolean isDebugEnabled() {
        return config.getBoolean("developer-mode");
    }

    private void registerStatistics() {
        Metrics metrics = new Metrics(plugin, 7287);
        metrics.addCustomChart(new SimplePie("head_drop_percentage", () -> String.valueOf(config.getDouble("drop-head-chance"))));
        metrics.addCustomChart(new SimplePie("give_killer_speed", () -> getBooleanString(config.getBoolean("give-killer-speed")))); // legacy
        metrics.addCustomChart(new SimplePie("heart_sucker", () -> getBooleanString(config.getBoolean("heart-sucker")))); // legacy
        metrics.addCustomChart(new SimplePie("do_lightning", () -> getBooleanString(config.getBoolean("do-lightning"))));
        metrics.addCustomChart(new SimplePie("enable_global_messages", () -> getBooleanString(config.getBoolean("enable-global-messages"))));
        metrics.addCustomChart(new SimplePie("enable_pvp_messages", () -> getBooleanString(config.getBoolean("enable-pvp-messages"))));
        metrics.addCustomChart(new SimplePie("enable_entity_name_messages", () -> getBooleanString(config.getBoolean("enable-entity-name-messages"))));
        metrics.addCustomChart(new SimplePie("enable_original_hover_message", () -> getBooleanString(config.getBoolean("original-hover-message"))));
        metrics.addCustomChart(new SimplePie("enable_item_tooltip_message", () -> getBooleanString(config.getBoolean("enable-item-hover"))));
    }

    private static String getBooleanString(boolean value) {
        return value ? "Enabled" : "Disabled";
    }

    public void addCustomMessage(DeathCause cause, String message) {
        List<String> configMessages = config.getStringList(cause.getPath());
        configMessages.add(message);
        config.set(cause.getPath(), configMessages);
        sneaky(config::save);

    }

    public void removeCustomMessage(DeathCause cause, int index) {
        List<String> configMessages = config.getStringList(cause.getPath());
        configMessages.remove(index);
        config.set(cause.getPath(), configMessages);
        sneaky(config::save);
    }

    public void setBoolean(String path, boolean value) {
        config.set(path, value);
        sneaky(config::save);
    }

    public void setDouble(String path, double value) {
        config.set(path, value);
        sneaky(config::save);
    }

    public void setString(String path, String value) {
        config.set(path, value);
        sneaky(config::save);
    }

    interface ConfigAction {
        void run() throws IOException;
    }

    private boolean sneaky(ConfigAction action) {
        try {
            action.run();
            return true;
        } catch (IOException e) {
            plugin.getLogger().warning("Unable to save config.yml, changes will not take effect.");
            return false;
        }
    }

    public int getMessagesCount(DeathCause cause) {
        return config.getStringList(cause.getPath()).size();
    }

    public Set<String> getStringConfigPaths() {
        Set<String> paths = new HashSet<>();

        for (String path : keys) {
            if (config.get(path) instanceof String) {
                paths.add(path);
            }
        }

        return paths;
    }

    public Set<String> getBoolConfigPaths() {
        Set<String> paths = new HashSet<>();

        for (String path : keys) {
            if (config.get(path) instanceof Boolean) {
                paths.add(path);
            }
        }

        return paths;
    }

    public Set<String> getNumConfigPaths() {
        Set<String> paths = new HashSet<>();

        for (String path : keys) {
            if (config.get(path) instanceof Double || config.get(path) instanceof Integer) {
                paths.add(path);
            }
        }

        return paths;
    }

    public List<String> listDeathMessages(DeathCause cause) {
        return config.getStringList(cause.getPath());
    }

    private boolean invalidCollection(List<?> collection, String name) {
        if (collection == null || collection.isEmpty()) {
            plugin.getLogger().warning(MessageFormat.format("No messages found for {0}! Check your config.yml.", name));
            return true;
        }

        return false;
    }
}
