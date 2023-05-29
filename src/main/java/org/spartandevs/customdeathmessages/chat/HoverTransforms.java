package org.spartandevs.customdeathmessages.chat;

import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.hover.content.Text;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.spartandevs.customdeathmessages.CustomDeathMessages;
import org.spartandevs.customdeathmessages.util.ConfigManager;

public class HoverTransforms {
    private final HoverTransformers transformers;
    private final String original;
    private final ItemStack item;

    public HoverTransforms(CustomDeathMessages plugin, String original, ItemStack item) {
        this.transformers = HoverTransformers.getTransforms(plugin, item);
        this.original = original;
        this.item = item;
    }

    public TextComponent transform(String message) {
        return transformers.transform(message, original, item);
    }
}

interface Transform {
    TextComponent transform(String message, String original, ItemStack item);
}

enum HoverTransformers {
    NONE((message, original, item) -> new TextComponent(message)),
    ORIGINAL_ON_HOVER((message, original, item) -> {
        TextComponent component = new TextComponent(message);
        component.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text(original)));
        return component;
    }),
    ITEM_ON_HOVER((message, original, item) -> {
        TextComponent component = new TextComponent();
        TextComponent hoverItem = ItemSerializer.serializeItemStack(item);
        String[] split = message.split("%kill-weapon%");

        for (int i = 0; i < split.length; i++) {
            component.addExtra(split[i]);

            if (i != split.length - 1) {
                component.addExtra(hoverItem);
            }
        }

        return component;
    }),
    ORIGINAL_AND_ITEM_ON_HOVER((message, original, item) -> {
        TextComponent component = new TextComponent();
        Text originalHover = new Text(original);
        String[] split = message.split("%kill-weapon%");
        TextComponent hoverItem = null;

        for (int i = 0; i < split.length; i++) {
            TextComponent hoverChunk = new TextComponent(split[i]);
            hoverChunk.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, originalHover));
            component.addExtra(hoverChunk);

            if (i != split.length - 1) {
                if (hoverItem == null) {
                    hoverItem = ItemSerializer.serializeItemStack(item);
                }

                component.addExtra(hoverItem);
            }
        }

        return component;
    });

    private final Transform transform;

    HoverTransformers(Transform transform) {
        this.transform = transform;
    }

    public TextComponent transform(String message, String original, ItemStack item) {
        return transform.transform(message, original, item);
    }

    public static HoverTransformers getTransforms(CustomDeathMessages plugin, ItemStack item) {
        ConfigManager config = plugin.getConfigManager();

        if (config.isItemOnHoverEnabled() && config.isOriginalOnHoverEnabled()) {
            return item != null && item.getType() != Material.AIR
                    ? HoverTransformers.ORIGINAL_AND_ITEM_ON_HOVER
                    : HoverTransformers.ORIGINAL_ON_HOVER;
        } else if (config.isItemOnHoverEnabled() && item != null) {
            return HoverTransformers.ITEM_ON_HOVER;
        } else if (config.isOriginalOnHoverEnabled()) {
            return HoverTransformers.ORIGINAL_ON_HOVER;
        } else {
            return HoverTransformers.NONE;
        }
    }
}
