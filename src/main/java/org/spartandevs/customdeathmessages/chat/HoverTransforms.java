package org.spartandevs.customdeathmessages.chat;

import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.spartandevs.customdeathmessages.CustomDeathMessages;
import org.spartandevs.customdeathmessages.util.ConfigManager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class HoverTransforms {
    private final HoverTransformers transformers;
    private final String original;
    private final ItemStack item;

    public HoverTransforms(CustomDeathMessages plugin, String original, ItemStack item) {
        this.transformers = HoverTransformers.getTransforms(plugin, item);
        this.original = original;
        this.item = item;
    }

    public BaseComponent[] transform(String message) {
        return transformers.transform(message, original, item);
    }
}

interface Transform {
    BaseComponent[] transform(String message, String original, ItemStack item);
}

enum HoverTransformers {
    NONE((message, original, item) -> createBaseComponent(message)),
    @SuppressWarnings("deprecation")
    ORIGINAL_ON_HOVER((message, original, item) -> {
        BaseComponent[] component = createBaseComponent(message);
        setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, createBaseComponent(original)), component);
        return component;
    }),
    ITEM_ON_HOVER((message, original, item) -> populateKillWeapon(message, null, item)),
    ORIGINAL_AND_ITEM_ON_HOVER(HoverTransformers::populateKillWeapon);

    @SuppressWarnings("deprecation") // Backwards compatibility with old BaseComponent API
    private static BaseComponent[] populateKillWeapon(String message, String original, ItemStack item) {
        List<BaseComponent> component = new ArrayList<>();
        BaseComponent hoverItem = null;
        BaseComponent[] originalHover = original == null ? null : createBaseComponent(original);
        String[] split = message.split("%kill-weapon%");
        boolean endWithItem = message.endsWith("%kill-weapon%");


        for (int i = 0; i < split.length; i++) {
            BaseComponent[] chunk = createBaseComponent(split[i]);

            if (originalHover != null) {
                setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, originalHover), chunk);
            }

            addExtra(component, chunk);

            if (i != split.length - 1 || endWithItem) {
                if (hoverItem == null) {
                    hoverItem = ItemSerializer.serializeItemStack(item);
                }

                addExtra(component, hoverItem);
            }
        }

        return createBaseComponent(component);
    }

    private final Transform transform;

    HoverTransformers(Transform transform) {
        this.transform = transform;
    }

    public BaseComponent[] transform(String message, String original, ItemStack item) {
        return transform.transform(message, original, item);
    }

    public static HoverTransformers getTransforms(CustomDeathMessages plugin, ItemStack item) {
        ConfigManager config = plugin.getConfigManager();

        if (config.isItemOnHoverEnabled() && config.isOriginalOnHoverEnabled()) {
            return item != null && item.getType() != Material.AIR
                    ? HoverTransformers.ORIGINAL_AND_ITEM_ON_HOVER
                    : HoverTransformers.ORIGINAL_ON_HOVER;
        } else if (config.isItemOnHoverEnabled() && item != null && item.getType() != Material.AIR) {
            return HoverTransformers.ITEM_ON_HOVER;
        } else if (config.isOriginalOnHoverEnabled()) {
            return HoverTransformers.ORIGINAL_ON_HOVER;
        } else {
            return HoverTransformers.NONE;
        }
    }

    public static BaseComponent[] createBaseComponent(String message) {
        return TextComponent.fromLegacyText(message);
    }

    public static void setHoverEvent(HoverEvent hoverEvent, BaseComponent... component) {
        for (BaseComponent baseComponent : component) {
            baseComponent.setHoverEvent(hoverEvent);
        }
    }

    private static BaseComponent[] createBaseComponent(List<BaseComponent> component) {
        return component.toArray(new BaseComponent[0]);
    }

    private static void addExtra(List<BaseComponent> component, BaseComponent... extra) {
        component.addAll(Arrays.asList(extra));
    }
}
