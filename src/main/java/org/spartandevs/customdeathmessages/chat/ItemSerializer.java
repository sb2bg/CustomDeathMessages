package org.spartandevs.customdeathmessages.chat;

import io.github.bananapuncher714.nbteditor.NBTEditor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.util.Locale;

public class ItemSerializer {
    @SuppressWarnings("deprecation")
    public static BaseComponent[] serializeItemStack(ItemStack itemStack) {
        String nbt = NBTEditor.getNBTCompound(itemStack).toString();
        HoverEvent event = createItemHoverEvent(itemStack, nbt);
        BaseComponent[] component = HoverTransformers.createBaseComponent(getItemName(itemStack));
        HoverTransformers.setHoverEvent(component, event);

        return component;
    }

    @SuppressWarnings("deprecation")
    private static HoverEvent createItemHoverEvent(ItemStack itemStack, String nbt) {
        try {
            Class<?> contentClass = Class.forName("net.md_5.bungee.api.chat.hover.content.Content");
            Class<?> itemClass = Class.forName("net.md_5.bungee.api.chat.hover.content.Item");
            Class<?> itemTagClass = Class.forName("net.md_5.bungee.api.chat.ItemTag");
            Method ofNbt = itemTagClass.getMethod("ofNbt", String.class);
            Object itemTag = ofNbt.invoke(null, nbt);
            Object hoverItem = itemClass
                    .getConstructor(String.class, int.class, itemTagClass)
                    .newInstance(getItemId(itemStack), itemStack.getAmount(), itemTag);
            Object contents = Array.newInstance(contentClass, 1);
            Array.set(contents, 0, hoverItem);

            return HoverEvent.class
                    .getConstructor(HoverEvent.Action.class, contents.getClass())
                    .newInstance(HoverEvent.Action.SHOW_ITEM, contents);
        } catch (ReflectiveOperationException ignored) {
            return new HoverEvent(HoverEvent.Action.SHOW_ITEM, new BaseComponent[]{new TextComponent(nbt)});
        }
    }

    private static String getItemId(ItemStack itemStack) {
        return "minecraft:" + itemStack.getType().name().toLowerCase(Locale.ROOT);
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
}
