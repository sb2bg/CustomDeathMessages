package org.spartandevs.customdeathmessages.chat;

import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.spartandevs.customdeathmessages.CustomDeathMessages;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

public class PlaceholderPopulator {
    interface PlayerPropertyGetter {
        String get(Player player);
    }

    interface EntityPropertyGetter {
        String get(Entity entity);
    }

    interface ItemPropertyGetter {
        String get(ItemStack item);
    }

    private static final Map<String, PlayerPropertyGetter> VICTIM = victimPlaceholders();
    private static final Map<String, EntityPropertyGetter> ENTITY_KILLER = entityKillerPlaceholders();
    private static final Map<String, PlayerPropertyGetter> PLAYER_KILLER = playerKillerPlaceholders();
    private static final Map<String, ItemPropertyGetter> ITEM = itemPlaceholders();

    private final PlaceholderContext context;
    private final Map<String, String> placeholders = new LinkedHashMap<>();

    public PlaceholderPopulator(CustomDeathMessages plugin, Player victim, Entity killer, ItemStack item) {
        context = new PlaceholderContext(plugin.getPlaceholderApiManager(), victim, killer, item);
        populatePlayerPlaceholders(VICTIM, context.getVictim());
        populateEntityPlaceholders(ENTITY_KILLER, context.getKiller());
        populatePlayerPlaceholders(PLAYER_KILLER, context.getKillerPlayer());
        populateItemPlaceholders(ITEM, context.getItem());
    }

    public String replace(String message) {
        return replace(context.getDefaultContext(), message);
    }

    public String replace(Player context, String message) {
        for (Map.Entry<String, String> placeholder : placeholders.entrySet()) {
            message = message.replace("%" + placeholder.getKey() + "%", placeholder.getValue());
        }

        message = this.context.getPlaceholderApiManager().replaceScoped(this.context.getVictim(), this.context.getKillerPlayer(), message);
        message = this.context.getPlaceholderApiManager().replace(context, message);
        return ChatColor.translate(message);
    }

    @Override
    public String toString() {
        return placeholders.toString();
    }

    private static Map<String, PlayerPropertyGetter> victimPlaceholders() {
        Map<String, PlayerPropertyGetter> placeholders = new LinkedHashMap<>();
        placeholders.put("victim", Player::getName);
        placeholders.put("victim-x", p -> String.valueOf(p.getLocation().getBlockX()));
        placeholders.put("victim-y", p -> String.valueOf(p.getLocation().getBlockY()));
        placeholders.put("victim-z", p -> String.valueOf(p.getLocation().getBlockZ()));
        placeholders.put("victim-nick", Player::getDisplayName);
        placeholders.put("victim-world", player -> player.getWorld().getName());
        return Collections.unmodifiableMap(placeholders);
    }

    private static Map<String, EntityPropertyGetter> entityKillerPlaceholders() {
        Map<String, EntityPropertyGetter> placeholders = new LinkedHashMap<>();
        placeholders.put("killer", Entity::getName);
        placeholders.put("entity-name", Entity::getName);
        placeholders.put("killer-x", e -> String.valueOf(e.getLocation().getBlockX()));
        placeholders.put("killer-y", e -> String.valueOf(e.getLocation().getBlockY()));
        placeholders.put("killer-z", e -> String.valueOf(e.getLocation().getBlockZ()));
        placeholders.put("killer-world", e -> e.getWorld().getName());
        return Collections.unmodifiableMap(placeholders);
    }

    private static Map<String, PlayerPropertyGetter> playerKillerPlaceholders() {
        Map<String, PlayerPropertyGetter> placeholders = new LinkedHashMap<>();
        placeholders.put("killer-nick", Player::getDisplayName);
        return Collections.unmodifiableMap(placeholders);
    }

    private static Map<String, ItemPropertyGetter> itemPlaceholders() {
        Map<String, ItemPropertyGetter> placeholders = new LinkedHashMap<>();
        placeholders.put("kill-weapon", ItemSerializer::getItemName);
        return Collections.unmodifiableMap(placeholders);
    }

    private void populatePlayerPlaceholders(Map<String, PlayerPropertyGetter> source, Player player) {
        if (player == null) {
            return;
        }

        for (Map.Entry<String, PlayerPropertyGetter> entry : source.entrySet()) {
            add(entry.getKey(), entry.getValue().get(player));
        }
    }

    private void populateEntityPlaceholders(Map<String, EntityPropertyGetter> source, Entity entity) {
        if (entity == null) {
            return;
        }

        for (Map.Entry<String, EntityPropertyGetter> entry : source.entrySet()) {
            add(entry.getKey(), entry.getValue().get(entity));
        }
    }

    private void populateItemPlaceholders(Map<String, ItemPropertyGetter> source, ItemStack item) {
        if (item == null || item.getType() == Material.AIR) {
            return;
        }

        for (Map.Entry<String, ItemPropertyGetter> entry : source.entrySet()) {
            add(entry.getKey(), entry.getValue().get(item));
        }
    }

    private void add(String key, String value) {
        if (value != null) {
            placeholders.put(key, value);
        }
    }
}
