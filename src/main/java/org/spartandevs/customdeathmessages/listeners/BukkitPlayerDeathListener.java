package org.spartandevs.customdeathmessages.listeners;

import net.md_5.bungee.api.chat.BaseComponent;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.spartandevs.customdeathmessages.CustomDeathMessages;
import org.spartandevs.customdeathmessages.chat.DeathMessage;
import org.spartandevs.customdeathmessages.chat.HoverTransforms;
import org.spartandevs.customdeathmessages.events.CustomPlayerDeathEvent;
import org.spartandevs.customdeathmessages.util.DeathResolver;

public class BukkitPlayerDeathListener implements Listener {
    public interface OriginalDeathMessageSetter {
        void setDeathMessage(String message);
    }

    private final CustomDeathMessages plugin;

    public BukkitPlayerDeathListener(CustomDeathMessages plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player victim = event.getEntity();
        DeathResolver.Death death = DeathResolver.resolve(plugin, victim);

        CustomPlayerDeathEvent customPlayerDeathEvent = new CustomPlayerDeathEvent(
                death.getMessagePath(), death.isMelee(), event.getDeathMessage(), event, death.getKiller(), victim,
                (player, deathMessage, hoverTransforms, originalEvent) ->
                        setDeathMessage(event::setDeathMessage, player, deathMessage, hoverTransforms, originalEvent));

        plugin.getServer().getPluginManager().callEvent(customPlayerDeathEvent);
    }

    private void setDeathMessage(OriginalDeathMessageSetter deathMessageSetter, Player victim, DeathMessage deathMessage,
                                 HoverTransforms hoverTransforms, PlayerDeathEvent event) {
        // message is on cooldown
        if (deathMessage == null) {
            deathMessageSetter.setDeathMessage("");
            return;
        }

        switch (deathMessage.getMessageType()) {
            case STRING:
                deathMessageSetter.setDeathMessage(deathMessage.getStringMessage());
                break;
            case JSON: {
                // Disable the default death message, so we can send a TextComponent
                deathMessageSetter.setDeathMessage("");

                BaseComponent[] textComponent = deathMessage.getTextComponent(hoverTransforms);
                plugin.getServer().spigot().broadcast(textComponent);

                // Console & Discord don't receive spigot broadcasts, so we have to send it manually
                String stringMessage = baseComponentArrayToString(textComponent);
                plugin.getServer().getConsoleSender().sendMessage(stringMessage);
                plugin.getDiscordManager().sendDeathMessage(stringMessage, victim, event);

                break;
            }
        }

        plugin.getCooldownManager().setCooldown(victim.getUniqueId(), plugin.getConfigManager().getCooldown());
    }

    private static String baseComponentArrayToString(BaseComponent[] baseComponents) {
        StringBuilder stringBuilder = new StringBuilder();

        for (BaseComponent baseComponent : baseComponents) {
            stringBuilder.append(baseComponent.toLegacyText());
        }

        return stringBuilder.toString();
    }
}
