package org.spartandevs.customdeathmessages.chat;

import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.hover.content.Text;
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
    ORIGINAL_ON_HOVER((message, original, item) -> {
        BaseComponent[] component = createBaseComponent(message);
        setHoverEvent(component, new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text(original)));
        return component;
    }),
    ITEM_ON_HOVER((message, original, item) -> {
        List<BaseComponent> component = new ArrayList<>();
        BaseComponent[] hoverItem = ItemSerializer.serializeItemStack(item);
        String[] split = message.split("%kill-weapon%");

        for (int i = 0; i < split.length; i++) {
            addExtra(component, createBaseComponent(split[i]));

            if (i != split.length - 1) {
                addExtra(component, hoverItem);
            }
        }

        return createBaseComponent(component);
    }),
    ORIGINAL_AND_ITEM_ON_HOVER((message, original, item) -> {
        List<BaseComponent> component = new ArrayList<>();
        Text originalHover = new Text(original);
        String[] split = message.split("%kill-weapon%");
        BaseComponent[] hoverItem = null;

        for (int i = 0; i < split.length; i++) {
            BaseComponent[] hoverChunk = createBaseComponent(split[i]);
            setHoverEvent(hoverChunk, new HoverEvent(HoverEvent.Action.SHOW_TEXT, originalHover));
            addExtra(component, hoverChunk);

            if (i != split.length - 1) {
                if (hoverItem == null) {
                    hoverItem = ItemSerializer.serializeItemStack(item);
                }

                addExtra(component, hoverItem);
            }
        }

        return createBaseComponent(component);
    });

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

    public static void setHoverEvent(BaseComponent[] component, HoverEvent hoverEvent) {
        for (BaseComponent baseComponent : component) {
            baseComponent.setHoverEvent(hoverEvent);
        }
    }

    private static BaseComponent[] createBaseComponent(List<BaseComponent> component) {
        return component.toArray(new BaseComponent[0]);
    }

    private static void addExtra(List<BaseComponent> component, BaseComponent[] extra) {
        component.addAll(Arrays.asList(extra));
    }
}
