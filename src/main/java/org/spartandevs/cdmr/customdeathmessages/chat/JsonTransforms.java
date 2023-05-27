package org.spartandevs.cdmr.customdeathmessages.chat;

import net.md_5.bungee.api.chat.TextComponent;
import org.spartandevs.cdmr.customdeathmessages.CustomDeathMessages;
import org.spartandevs.cdmr.customdeathmessages.util.ConfigManager;

interface Transform {
    TextComponent transform(String message);
}

public enum JsonTransforms {
    NONE(TextComponent::new),
    ORIGINAL_ON_HOVER(message -> new TextComponent(message)),
    ITEM_ON_HOVER(message -> new TextComponent(message)),
    ORIGINAL_AND_ITEM_ON_HOVER(message -> new TextComponent(message)),
    ;

    private final Transform transform;

    JsonTransforms(Transform transform) {
        this.transform = transform;
    }

    public static JsonTransforms getTransforms(CustomDeathMessages plugin) {
        ConfigManager config = plugin.getConfigManager();

        if (config.isItemOnHoverEnabled() && config.isOriginalOnHoverEnabled()) {
            return JsonTransforms.ORIGINAL_AND_ITEM_ON_HOVER;
        } else if (config.isItemOnHoverEnabled()) {
            return JsonTransforms.ITEM_ON_HOVER;
        } else if (config.isOriginalOnHoverEnabled()) {
            return JsonTransforms.ORIGINAL_ON_HOVER;
        } else {
            return JsonTransforms.NONE;
        }
    }

    public TextComponent transform(String message) {
        return transform.transform(message);
    }
}
