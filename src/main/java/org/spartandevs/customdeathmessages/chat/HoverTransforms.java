package org.spartandevs.customdeathmessages.chat;

import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.hover.content.Text;
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

    public HoverTransformers getTransformers() {
        return transformers;
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

        for (String s : message.split("%")) {
            if (s.equals("kill-weapon")) {
                component.addExtra(hoverItem);
                continue;
            }

            component.addExtra(s);
        }

        return component;
    }),
    ORIGINAL_AND_ITEM_ON_HOVER((message, original, item) -> {
        TextComponent component = new TextComponent();
        TextComponent hoverItem = ItemSerializer.serializeItemStack(item);
        Text originalHover = new Text(original);

        for (String s : message.split("%")) {
            if (s.equals("kill-weapon")) {
                component.addExtra(hoverItem);
            } else {
                TextComponent hoverChunk = new TextComponent(s);
                hoverChunk.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, originalHover));
                component.addExtra(hoverChunk);
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
            return item != null ? HoverTransformers.ORIGINAL_AND_ITEM_ON_HOVER : HoverTransformers.ORIGINAL_ON_HOVER;
        } else if (config.isItemOnHoverEnabled() && item != null) {
            return HoverTransformers.ITEM_ON_HOVER;
        } else if (config.isOriginalOnHoverEnabled()) {
            return HoverTransformers.ORIGINAL_ON_HOVER;
        } else {
            return HoverTransformers.NONE;
        }
    }
}
