package org.spartandevs.customdeathmessages.util;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

/**
 * Creates player head items on every supported server version (1.8 and newer).
 */
public final class PlayerHeads {
    private PlayerHeads() {
    }

    public static ItemStack create(Player owner, String displayName) {
        ItemStack head = newHeadItem();
        ItemMeta meta = head.getItemMeta();

        if (meta == null) {
            return head;
        }

        if (meta instanceof SkullMeta) {
            setOwner((SkullMeta) meta, owner);
        }

        meta.setDisplayName(displayName);
        head.setItemMeta(meta);
        return head;
    }

    @SuppressWarnings("deprecation") // legacy item data values are required before 1.13
    private static ItemStack newHeadItem() {
        Material playerHead = Material.matchMaterial("PLAYER_HEAD");

        if (playerHead != null) {
            return new ItemStack(playerHead);
        }

        // 1.8 - 1.12: a player head is SKULL_ITEM with data value 3
        return new ItemStack(Material.valueOf("SKULL_ITEM"), 1, (short) 3);
    }

    @SuppressWarnings("deprecation") // setOwner(String) is the only option before 1.12.1
    private static void setOwner(SkullMeta meta, Player owner) {
        try {
            meta.setOwningPlayer(owner);
        } catch (NoSuchMethodError ignored) {
            meta.setOwner(owner.getName());
        }
    }
}
