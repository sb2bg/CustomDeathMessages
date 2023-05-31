package org.spartandevs.customdeathmessages.chat;

import io.github.bananapuncher714.nbteditor.NBTEditor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class ItemSerializer {
    @SuppressWarnings("deprecation")
    public static BaseComponent[] serializeItemStack(ItemStack itemStack) {
        String nbt = NBTEditor.getNBTCompound(itemStack).toString();
        HoverEvent event = new HoverEvent(HoverEvent.Action.SHOW_ITEM, new BaseComponent[]{new TextComponent(nbt)});
        BaseComponent[] component = HoverTransformers.createBaseComponent(getItemName(itemStack));
        HoverTransformers.setHoverEvent(component, event);

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
}