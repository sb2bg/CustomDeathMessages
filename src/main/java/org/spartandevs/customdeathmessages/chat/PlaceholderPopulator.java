package org.spartandevs.customdeathmessages.chat;

import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.spartandevs.customdeathmessages.CustomDeathMessages;

import java.util.*;

public class PlaceholderPopulator {
    private final CustomDeathMessages plugin;

    interface PlayerPropertyGetter {
        String get(Player player);
    }

    interface EntityPropertyGetter {
        String get(Entity entity);
    }

    interface ItemPropertyGetter {
        String get(ItemStack item);
    }

    private static final Map<String, PlayerPropertyGetter> VICTIM = new HashMap<>();

    static {
        VICTIM.put("victim", Player::getName);
        VICTIM.put("victim-x", p -> String.valueOf(p.getLocation().getBlockX()));
        VICTIM.put("victim-y", p -> String.valueOf(p.getLocation().getBlockY()));
        VICTIM.put("victim-z", p -> String.valueOf(p.getLocation().getBlockZ()));
        VICTIM.put("victim-nick", Player::getDisplayName);
        VICTIM.put("victim-world", player -> player.getWorld().getName());
    }

    private static final Map<String, EntityPropertyGetter> ENTITY_KILLER = new HashMap<>();

    static {
        ENTITY_KILLER.put("killer", Entity::getName);
        ENTITY_KILLER.put("entity-name", Entity::getCustomName);
        ENTITY_KILLER.put("killer-x", e -> String.valueOf(e.getLocation().getBlockX()));
        ENTITY_KILLER.put("killer-y", e -> String.valueOf(e.getLocation().getBlockY()));
        ENTITY_KILLER.put("killer-z", e -> String.valueOf(e.getLocation().getBlockZ()));
        ENTITY_KILLER.put("killer-nick", Entity::getCustomName);
        ENTITY_KILLER.put("killer-world", e -> e.getWorld().getName());
    }

    private static final Map<String, ItemPropertyGetter> ITEM = new HashMap<>();

    static {
        ITEM.put("kill-weapon", ItemSerializer::getItemName);
    }

    private static final Set<Map.Entry<String, String>> populatedPlaceholders = new HashSet<>();

    private void addFrom(String key, String value) {
        if (value != null) {
            populatedPlaceholders.add(new AbstractMap.SimpleEntry<>(key, value));
        }
    }

    public PlaceholderPopulator(CustomDeathMessages plugin, Player victim) {
        this.plugin = plugin;

        for (Map.Entry<String, PlayerPropertyGetter> entry : VICTIM.entrySet()) {
            addFrom(entry.getKey(), entry.getValue().get(victim));
        }
    }

    public PlaceholderPopulator(CustomDeathMessages plugin, Player victim, Entity killer) {
        this(plugin, victim);

        for (Map.Entry<String, EntityPropertyGetter> entry : ENTITY_KILLER.entrySet()) {
            addFrom(entry.getKey(), entry.getValue().get(killer));
        }
    }

    public PlaceholderPopulator(CustomDeathMessages plugin, Player victim, Entity killer, ItemStack item) {
        this(plugin, victim, killer);

        if (item == null || item.getType() == Material.AIR) {
            return;
        }

        if (!item.hasItemMeta() || !Objects.requireNonNull(item.getItemMeta()).hasDisplayName()) {
            return;
        }

        for (Map.Entry<String, ItemPropertyGetter> entry : ITEM.entrySet()) {
            addFrom(entry.getKey(), entry.getValue().get(item));
        }
    }

    public String replace(String message) {
        for (Map.Entry<String, String> placeholder : populatedPlaceholders) {
            message = message.replace("%" + placeholder.getKey() + "%", placeholder.getValue());
        }

        return plugin.translateColorCodes(message);
    }
}
