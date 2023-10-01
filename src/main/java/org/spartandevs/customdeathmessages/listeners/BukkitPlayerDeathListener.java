package org.spartandevs.customdeathmessages.listeners;

import net.md_5.bungee.api.chat.BaseComponent;
import org.bukkit.GameRule;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.spartandevs.customdeathmessages.CustomDeathMessages;
import org.spartandevs.customdeathmessages.chat.DeathMessage;
import org.spartandevs.customdeathmessages.events.CustomPlayerDeathEvent;
import org.spartandevs.customdeathmessages.util.DeathCause;
import org.spartandevs.customdeathmessages.util.MessageInfo;
import org.spartandevs.customdeathmessages.util.Version;

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
        Entity killer = victim.getKiller();

        if (victim.getLastDamageCause() == null) {
            return;
        }

        DeathCause deathCause = DeathCause.from(victim.getLastDamageCause().getCause(), plugin);
        MessageInfo propagated = plugin.getMessagePropagator().getDeathMessage(victim.getUniqueId());

        if (propagated != null) {
            deathCause = propagated.getDeathCause();

            if (killer == null) {
                killer = propagated.getKiller();
            }
        }

        deathMessageSetter = event::setDeathMessage;

        CustomPlayerDeathEvent customPlayerDeathEvent = new CustomPlayerDeathEvent(
                deathCause, event.getDeathMessage(), killer, victim, this::setDeathMessage, showDeathMessageGameRule(event));

        plugin.getServer().getPluginManager().callEvent(customPlayerDeathEvent);
    }

    private void setDeathMessage(Player victim, DeathMessage deathMessage) {
        // Message is on cooldown
        if (deathMessage == null) {
            deathMessageSetter.setDeathMessage(null);
            return;
        }

        switch (deathMessage.getMessageType()) {
            case STRING:
                deathMessageSetter.setDeathMessage(deathMessage.getStringMessage());
                break;
            case JSON: {
                deathMessageSetter.setDeathMessage(null);
                BaseComponent[] textComponent = deathMessage.getTextComponent();
                plugin.getServer().spigot().broadcast(textComponent);
                // Console doesn't receive spigot broadcasts, so we have to send it manually
                plugin.getServer().getConsoleSender().sendMessage(baseComponentArrayToString(textComponent));
                break;
            }
        }

        plugin.getCooldownManager().setCooldown(victim.getUniqueId(), plugin.getConfigManager().getCooldown());
    }

    private static String baseComponentArrayToString(BaseComponent[] baseComponents) {
        StringBuilder stringBuilder = new StringBuilder();

        for (BaseComponent baseComponent : baseComponents) {
            stringBuilder.append(baseComponent.toPlainText());
        }

        return stringBuilder.toString();
    }

    @SuppressWarnings("deprecation") // GameRules changed in 1.13
    private static boolean showDeathMessageGameRule(PlayerDeathEvent event) {
        return Version.SERVER_VERSION.isHigherThan(Version.V12)
                ? Boolean.TRUE.equals(event.getEntity().getWorld().getGameRuleValue(GameRule.SHOW_DEATH_MESSAGES))
                : event.getEntity().getWorld().isGameRule("showDeathMessages") && event.getEntity().getWorld().getGameRuleValue("showDeathMessages").equals("true");
    }
}
