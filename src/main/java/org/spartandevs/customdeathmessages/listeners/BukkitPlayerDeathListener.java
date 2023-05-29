package org.spartandevs.customdeathmessages.listeners;

import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.spartandevs.customdeathmessages.CustomDeathMessages;
import org.spartandevs.customdeathmessages.chat.DeathMessage;
import org.spartandevs.customdeathmessages.chat.HoverTransforms;
import org.spartandevs.customdeathmessages.events.CustomPlayerDeathEvent;
import org.spartandevs.customdeathmessages.util.DeathCause;

public class BukkitPlayerDeathListener implements Listener {
    public interface OriginalDeathMessageSetter {
        void setDeathMessage(String message);
    }

    private final CustomDeathMessages plugin;

    private OriginalDeathMessageSetter deathMessageSetter;

    public BukkitPlayerDeathListener(CustomDeathMessages plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player victim = event.getEntity();
        Player killer = victim.getKiller();

        if (victim.getLastDamageCause() == null) {
            return;
        }

        DeathCause deathCause = DeathCause.fromDamageCause(victim.getLastDamageCause().getCause());

        deathMessageSetter = event::setDeathMessage;

        CustomPlayerDeathEvent customPlayerDeathEvent = new CustomPlayerDeathEvent(
                deathCause, event.getDeathMessage(), killer, victim, this::setDeathMessage);

        plugin.getServer().getPluginManager().callEvent(customPlayerDeathEvent);
    }

    private void setDeathMessage(DeathMessage deathMessage, HoverTransforms hoverTransforms) {
        switch (deathMessage.getMessageType()) {
            case STRING -> deathMessageSetter.setDeathMessage(deathMessage.getStringMessage());
            case JSON -> {
                deathMessageSetter.setDeathMessage("");
                TextComponent textComponent = deathMessage.getTextComponent(hoverTransforms);
                plugin.getServer().spigot().broadcast(textComponent);
                // Console doesn't receive spigot broadcasts, so we have to send it manually
                plugin.getServer().getConsoleSender().sendMessage(textComponent.toLegacyText());
            }
        }
    }
}
