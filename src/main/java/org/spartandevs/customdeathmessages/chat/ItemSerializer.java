package org.spartandevs.customdeathmessages.chat;

import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.ItemTag;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.hover.content.Item;
import org.apache.commons.text.WordUtils;
import org.bukkit.Bukkit;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.lang.reflect.Method;

public class ItemSerializer {
    private static final String VERSION = Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3];

    public static TextComponent serializeItemStack(ItemStack itemStack) {
        Item item = new Item(itemStack.getType().getKey().getKey(), itemStack.getAmount(), ItemTag.ofNbt(getNMSItemStackTag(itemStack)));
        HoverEvent event = new HoverEvent(HoverEvent.Action.SHOW_ITEM, item);

        TextComponent component = new TextComponent(getItemName(itemStack));
        component.setHoverEvent(event);

        return component;
    }

    public static String getItemName(ItemStack item) {
        if (item == null) {
            return null;
        }

        ItemMeta meta = item.getItemMeta();

        if (meta == null) {
            return null;
        }

        return meta.hasDisplayName()
                ? meta.getDisplayName()
                : WordUtils.capitalize(item.getType().name().replaceAll("_", " ").toLowerCase());
    }

    private static synchronized String getNMSItemStackTag(ItemStack itemStack) {
        try {
            Class<?> nmsItemClass = Class.forName("org.bukkit.craftbukkit." + VERSION + "inventory.CraftItemStack");
            Class<?> nbtTagCompoundClass = Class.forName("net.minecraft.server." + VERSION + "NBTTagCompound");
            Object nbtTagCompound = nbtTagCompoundClass.getConstructor().newInstance();
            Method saveNmsItemStackMethod = nmsItemClass.getMethod("save", nbtTagCompoundClass);
            Object nmsItemStack = nmsItemClass.getMethod("asNMSCopy", ItemStack.class).invoke(null, itemStack);
            Object tag = saveNmsItemStackMethod.invoke(nmsItemStack, nbtTagCompound);

            return tag.toString();
        } catch (Throwable t) {
            return null;
        }
    }
}