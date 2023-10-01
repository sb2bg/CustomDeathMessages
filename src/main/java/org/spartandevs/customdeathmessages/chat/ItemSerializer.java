package org.spartandevs.customdeathmessages.chat;

import io.github.bananapuncher714.nbteditor.NBTEditor;
import me.pikamug.localelib.LocaleManager;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.TranslatableComponent;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.spartandevs.customdeathmessages.util.Version;

public class ItemSerializer {
    private static final LocaleManager LOCALE = new LocaleManager();

    @SuppressWarnings("deprecation")
    public static BaseComponent serializeItemStack(ItemStack itemStack) {
        String nbt = NBTEditor.getNBTCompound(itemStack).toString();
        HoverEvent event = new HoverEvent(HoverEvent.Action.SHOW_ITEM, new BaseComponent[]{new TextComponent(nbt)});
        BaseComponent component = getItemName(itemStack);
        HoverTransformers.setHoverEvent(event, component);

        return component;
    }

    public static BaseComponent getItemName(ItemStack item) {
        if (item == null) {
            return null;
        }

        ItemMeta meta = item.getItemMeta();

        if (meta == null || !meta.hasDisplayName()) {
            // For now, don't use the translation key for 1.12 and below because I haven't tested it
            return Version.SERVER_VERSION.isVersionOrHigherThan(Version.V13)
                    ? new TranslatableComponent(getTranslationKey(item))
                    : new TextComponent(ChatColor.capitalize(item.getType().name().toLowerCase().replace("_", " ")));
        }

        return new TextComponent(meta.getDisplayName());
    }

    private static String getTranslationKey(ItemStack item) {
        Material material = item.getType();

        // 1.19.3 added method to get translation key
        if (Version.SERVER_VERSION.isVersionOrHigherThan(Version.V20)) {
            return material.getTranslationKey();
        }

        // 1.13 added material namespaced keys
        if (Version.SERVER_VERSION.isVersionOrHigherThan(Version.V13)) {
            return (material.isBlock() ? "block" : "item") + ".minecraft." + material.getKey().getKey();
        }

        // fallback to old method
        return LOCALE.queryItemStack(item);
    }
}