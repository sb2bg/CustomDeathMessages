package org.spartandevs.customdeathmessages.util;

import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Locale;

/**
 * Item helpers that work on every supported server version.
 */
public final class Items {
    private Items() {
    }

    /**
     * @return the item held in the main hand of a player or mob, or {@code null}
     */
    @SuppressWarnings("deprecation") // getItemInHand is the only option on 1.8
    public static ItemStack mainHand(Entity entity) {
        if (entity instanceof Player) {
            Player player = (Player) entity;

            try {
                return player.getInventory().getItemInMainHand();
            } catch (NoSuchMethodError ignored) {
                return player.getItemInHand();
            }
        }

        if (!(entity instanceof LivingEntity)) {
            return null;
        }

        EntityEquipment equipment = ((LivingEntity) entity).getEquipment();

        if (equipment == null) {
            return null;
        }

        try {
            return equipment.getItemInMainHand();
        } catch (NoSuchMethodError ignored) {
            return equipment.getItemInHand();
        }
    }

    public static boolean isEmpty(ItemStack item) {
        return item == null || item.getType() == Material.AIR || item.getAmount() <= 0;
    }

    /**
     * @return the custom name of the item, or its type in title case (e.g. "Diamond Sword")
     */
    public static String displayName(ItemStack item) {
        ItemMeta meta = item.getItemMeta();

        if (meta != null && meta.hasDisplayName()) {
            return meta.getDisplayName();
        }

        return titleCase(item.getType().name());
    }

    /**
     * @return {@code DIAMOND_SWORD} as {@code Diamond Sword}
     */
    public static String titleCase(String enumName) {
        StringBuilder result = new StringBuilder(enumName.length());
        boolean capitalizeNext = true;

        for (char ch : enumName.toLowerCase(Locale.ROOT).replace('_', ' ').toCharArray()) {
            if (Character.isWhitespace(ch)) {
                capitalizeNext = true;
            } else if (capitalizeNext) {
                ch = Character.toTitleCase(ch);
                capitalizeNext = false;
            }

            result.append(ch);
        }

        return result.toString();
    }
}
