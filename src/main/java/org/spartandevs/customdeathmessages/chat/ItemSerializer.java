package org.spartandevs.customdeathmessages.chat;

import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.ItemTag;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.hover.content.Item;
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
                : ChatColor.capitalize(item.getType().name().replaceAll("_", " ").toLowerCase());
    }

    private static synchronized String getNMSItemStackTag(ItemStack itemStack) {
        try {
            Class<?> nmsCraftItemStack = Class.forName("org.bukkit.craftbukkit." + VERSION + ".inventory.CraftItemStack");
            Class<?> nmsItemStack = Class.forName("net.minecraft.server." + VERSION + ".ItemStack");
            Class<?> nbtCompoundClass = Class.forName("net.minecraft.server." + VERSION + ".NBTTagCompound");
            Method saveNmsItemStack = nmsItemStack.getMethod("save", nbtCompoundClass);

            Object nmsCopy = nmsCraftItemStack.getMethod("asNMSCopy", ItemStack.class).invoke(null, itemStack);
            Object nmsNbtTagCompound = nbtCompoundClass.getConstructor().newInstance();
            Object itemAsJson = saveNmsItemStack.invoke(nmsCopy, nmsNbtTagCompound);

            System.out.println(itemAsJson);

            return itemAsJson.toString();
        } catch (Throwable t) {
            t.printStackTrace();
            return null;
        }
    }
}