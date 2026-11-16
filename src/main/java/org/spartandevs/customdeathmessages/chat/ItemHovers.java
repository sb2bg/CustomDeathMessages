package org.spartandevs.customdeathmessages.chat;

import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.ItemTag;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.hover.content.Item;
import org.bukkit.Bukkit;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.spartandevs.customdeathmessages.util.Items;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Builds item tooltips with the BungeeCord chat API (Spigot, and Paper without Adventure).
 * <p>
 * The item tooltip format changed several times, so each era needs different data:
 * <ul>
 *     <li>1.8 - 1.15: a {@code value} holding the whole item as SNBT</li>
 *     <li>1.16 - 1.20.4: {@code contents} holding the id, count and the item's {@code tag} as SNBT</li>
 *     <li>1.20.5+: items use data components, which cannot be expressed through this API, so a text tooltip
 *     with the item's name, enchantments and lore is shown instead</li>
 * </ul>
 * Paper with Adventure does not use this class; it uses Paper's own item hover.
 */
final class ItemHovers {
    private static final boolean CONTENT_API = classExists("net.md_5.bungee.api.chat.hover.content.Item");
    private static final boolean DATA_COMPONENTS = method(ItemMeta.class, "getAsComponentString") != null;
    private static final Method GET_AS_STRING = method(ItemMeta.class, "getAsString");

    private ItemHovers() {
    }

    @SuppressWarnings("deprecation") // the BaseComponent[] constructor is the only one before 1.16
    static HoverEvent create(ItemStack item) {
        if (!CONTENT_API) {
            String snbt = LegacyNbt.itemSnbt(item);

            if (snbt != null) {
                return new HoverEvent(HoverEvent.Action.SHOW_ITEM, new BaseComponent[]{new TextComponent(snbt)});
            }
        } else if (!DATA_COMPONENTS) {
            String tag = tagSnbt(item);

            if (tag != null) {
                return Contents.showItem(item, tag);
            }
        }

        return new HoverEvent(HoverEvent.Action.SHOW_TEXT, describe(item));
    }

    /**
     * @return the SNBT of the item's {@code tag} compound (1.16 - 1.20.4), or {@code null}
     */
    private static String tagSnbt(ItemStack item) {
        ItemMeta meta = item.getItemMeta();

        if (GET_AS_STRING != null && meta != null) {
            try {
                return (String) GET_AS_STRING.invoke(meta);
            } catch (ReflectiveOperationException | RuntimeException ignored) {
                // fall through to the NMS lookup
            }
        }

        return LegacyNbt.tagSnbt(item);
    }

    /**
     * A text tooltip resembling the item's own: name, enchantments and lore.
     */
    static BaseComponent[] describe(ItemStack item) {
        List<BaseComponent> lines = new ArrayList<>();
        ItemMeta meta = item.getItemMeta();
        boolean named = meta != null && meta.hasDisplayName();
        addLine(lines, named ? ChatColor.SECTION + "o" + meta.getDisplayName() : Items.displayName(item));

        for (Map.Entry<Enchantment, Integer> enchantment : item.getEnchantments().entrySet()) {
            addLine(lines, ChatColor.SECTION + "7" + enchantmentName(enchantment.getKey()) + " " + roman(enchantment.getValue()));
        }

        if (meta != null && meta.hasLore() && meta.getLore() != null) {
            for (String lore : meta.getLore()) {
                addLine(lines, ChatColor.SECTION + "5" + ChatColor.SECTION + "o" + lore);
            }
        }

        return lines.toArray(new BaseComponent[0]);
    }

    private static void addLine(List<BaseComponent> lines, String legacy) {
        if (!lines.isEmpty()) {
            lines.add(new TextComponent("\n"));
        }

        for (BaseComponent component : TextComponent.fromLegacyText(legacy)) {
            lines.add(component);
        }
    }

    @SuppressWarnings("deprecation") // getName is the only option before 1.13
    private static String enchantmentName(Enchantment enchantment) {
        try {
            return Items.titleCase(enchantment.getKey().getKey());
        } catch (NoSuchMethodError e) {
            return Items.titleCase(enchantment.getName());
        }
    }

    static String roman(int level) {
        String[] numerals = {"", "I", "II", "III", "IV", "V", "VI", "VII", "VIII", "IX", "X"};
        return level >= 0 && level < numerals.length ? numerals[level] : String.valueOf(level);
    }

    private static boolean classExists(String name) {
        try {
            Class.forName(name);
            return true;
        } catch (ClassNotFoundException | LinkageError e) {
            return false;
        }
    }

    private static Method method(Class<?> owner, String name) {
        try {
            return owner.getMethod(name);
        } catch (NoSuchMethodException | LinkageError e) {
            return null;
        }
    }

    /**
     * Uses the 1.16+ hover content classes; only loaded when they exist.
     */
    private static final class Contents {
        static HoverEvent showItem(ItemStack item, String tag) {
            String id = item.getType().getKey().toString();
            return new HoverEvent(HoverEvent.Action.SHOW_ITEM, new Item(id, item.getAmount(), ItemTag.ofNbt(tag)));
        }
    }

    /**
     * Reads item NBT through CraftBukkit internals on versions where the API cannot (1.8 - 1.17). The
     * relevant names are stable from 1.8 to 1.16; anything else returns {@code null}.
     */
    private static final class LegacyNbt {
        static String itemSnbt(ItemStack item) {
            try {
                Object nms = asNms(item);
                Class<?> compound = Class.forName(nms.getClass().getPackage().getName() + ".NBTTagCompound");
                Object saved = nms.getClass().getMethod("save", compound).invoke(nms, compound.getConstructor().newInstance());
                return saved.toString();
            } catch (ReflectiveOperationException | RuntimeException | LinkageError e) {
                return null;
            }
        }

        static String tagSnbt(ItemStack item) {
            try {
                Object nms = asNms(item);
                Object tag = nms.getClass().getMethod("getTag").invoke(nms);
                return tag == null ? "{}" : tag.toString();
            } catch (ReflectiveOperationException | RuntimeException | LinkageError e) {
                return null;
            }
        }

        private static Object asNms(ItemStack item) throws ReflectiveOperationException {
            String craftPackage = Bukkit.getServer().getClass().getPackage().getName();
            Class<?> craftItemStack = Class.forName(craftPackage + ".inventory.CraftItemStack");
            return craftItemStack.getMethod("asNMSCopy", ItemStack.class).invoke(null, item);
        }
    }
}
