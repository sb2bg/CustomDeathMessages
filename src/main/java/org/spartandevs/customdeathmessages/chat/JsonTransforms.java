package org.spartandevs.customdeathmessages.chat;

import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.hover.content.Text;
import org.bukkit.inventory.ItemStack;
import org.spartandevs.customdeathmessages.CustomDeathMessages;
import org.spartandevs.customdeathmessages.util.ConfigManager;

public class JsonTransforms {
    private final JsonTransformers transformers;
    private final String original;
    private final ItemStack item;

    public JsonTransforms(CustomDeathMessages plugin, String original, ItemStack item) {
        this.transformers = JsonTransformers.getTransforms(plugin, item);
        this.original = original;
        this.item = item;
    }

    public TextComponent transform(String message) {
        return transformers.transform(message, original, item);
    }

    public JsonTransformers getTransformers() {
        return transformers;
    }
}

interface Transform {
    TextComponent transform(String message, String original, ItemStack item);
}

enum JsonTransformers {
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

    JsonTransformers(Transform transform) {
        this.transform = transform;
    }

    public TextComponent transform(String message, String original, ItemStack item) {
        return transform.transform(message, original, item);
    }

    public static JsonTransformers getTransforms(CustomDeathMessages plugin, ItemStack item) {
        ConfigManager config = plugin.getConfigManager();

        if (config.isItemOnHoverEnabled() && config.isOriginalOnHoverEnabled()) {
            return item != null ? JsonTransformers.ORIGINAL_AND_ITEM_ON_HOVER : JsonTransformers.ORIGINAL_ON_HOVER;
        } else if (config.isItemOnHoverEnabled() && item != null) {
            return JsonTransformers.ITEM_ON_HOVER;
        } else if (config.isOriginalOnHoverEnabled()) {
            return JsonTransformers.ORIGINAL_ON_HOVER;
        } else {
            return JsonTransformers.NONE;
        }
    }
}
