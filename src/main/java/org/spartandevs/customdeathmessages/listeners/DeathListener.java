package org.spartandevs.customdeathmessages.listeners;

import net.md_5.bungee.api.chat.BaseComponent;
import org.bukkit.GameRule;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;
import org.spartandevs.customdeathmessages.CustomDeathMessages;
import org.spartandevs.customdeathmessages.chat.BungeeRenderer;
import org.spartandevs.customdeathmessages.chat.PaperRenderer;
import org.spartandevs.customdeathmessages.chat.Placeholders;
import org.spartandevs.customdeathmessages.chat.Platform;
import org.spartandevs.customdeathmessages.chat.RichMessage;
import org.spartandevs.customdeathmessages.util.ConfigManager;
import org.spartandevs.customdeathmessages.util.DeathResolver;
import org.spartandevs.customdeathmessages.util.Items;
import org.spartandevs.customdeathmessages.util.MessagePaths;
import org.spartandevs.customdeathmessages.util.PlayerHeads;

import java.util.IdentityHashMap;
import java.util.Map;
import java.util.logging.Level;

/**
 * Replaces death messages and runs the death effects.
 * <p>
 * The message is set at {@link EventPriority#NORMAL}, so plugins at higher priorities (vanish plugins, Discord
 * bridges, ...) see and may change or hide it. Effects that are visible to players (lightning, PvP messages) run
 * at {@link EventPriority#MONITOR}, once it is certain the death was not cancelled.
 */
public class DeathListener implements Listener {
    private final CustomDeathMessages plugin;
    private final Map<PlayerDeathEvent, PendingDeath> pending = new IdentityHashMap<>();

    public DeathListener(CustomDeathMessages plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onDeath(PlayerDeathEvent event) {
        if (isCancelled(event)) {
            return;
        }

        Player victim = event.getEntity();
        ConfigManager config = plugin.getConfigManager();

        if (plugin.getCooldownManager().isOnCooldown(victim.getUniqueId())) {
            event.setDeathMessage("");
            return;
        }

        DeathResolver.Death death = DeathResolver.resolve(plugin, victim);
        // the item held by the killer, for players and mobs alike (e.g. a skeleton's bow)
        ItemStack weapon = Items.mainHand(death.getKiller());
        Placeholders placeholders = new Placeholders(plugin.getPlaceholderApi(), victim, death.getKiller(), weapon);
        PendingDeath pendingDeath = new PendingDeath(death.getKiller(), placeholders);
        pending.put(event, pendingDeath);

        if (config.dropHead()) {
            dropHead(event, placeholders.apply(victim, config.getHeadName()));
        }

        if (config.isGlobalMessageEnabled()) {
            setGlobalMessage(event, death, weapon, placeholders, pendingDeath);
        }
    }

    private void setGlobalMessage(PlayerDeathEvent event, DeathResolver.Death death, ItemStack weapon,
                                  Placeholders placeholders, PendingDeath pendingDeath) {
        String original = event.getDeathMessage();

        if (original == null || original.isEmpty()) {
            // another plugin already hid this death message
            return;
        }

        ConfigManager config = plugin.getConfigManager();
        Player victim = event.getEntity();
        // the melee list is for players killed by a direct hit from an empty-handed player
        String path = death.isMelee() && Items.isEmpty(weapon) ? MessagePaths.MELEE : death.getMessagePath();
        String template = config.getMessage(path);

        if (template == null) {
            // empty list: keep the vanilla message
            return;
        }

        plugin.getCooldownManager().setCooldown(victim.getUniqueId(), config.getCooldown());

        if (template.isEmpty()) {
            // a blank message hides the death message entirely
            event.setDeathMessage("");
            return;
        }

        boolean itemHover = config.isItemOnHoverEnabled() && !Items.isEmpty(weapon);
        boolean originalHover = config.isOriginalOnHoverEnabled();

        if (!itemHover && !originalHover) {
            event.setDeathMessage(placeholders.apply(victim, template));
            return;
        }

        RichMessage message = RichMessage.create(placeholders, template, weapon, itemHover);

        if (Platform.ADVENTURE_DEATH_MESSAGES) {
            PaperRenderer.setDeathMessage(event, message, originalHover);
            return;
        }

        // Without Adventure, death messages can only be plain text. Set the plain version now so other plugins
        // see it, and swap in the hoverable version at MONITOR if nobody changed it.
        pendingDeath.legacyMessage = message.toLegacy();
        pendingDeath.richMessage = BungeeRenderer.render(message, originalHover ? original : null);
        event.setDeathMessage(pendingDeath.legacyMessage);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onDeathMonitor(PlayerDeathEvent event) {
        PendingDeath pendingDeath = pending.remove(event);

        if (pendingDeath == null || isCancelled(event)) {
            return;
        }

        Player victim = event.getEntity();
        ConfigManager config = plugin.getConfigManager();

        if (config.doLightningStrike()) {
            victim.getWorld().strikeLightningEffect(victim.getLocation());
        }

        if (config.doPvpMessages() && pendingDeath.killer instanceof Player) {
            Player killer = (Player) pendingDeath.killer;
            killer.sendMessage(pendingDeath.placeholders.apply(killer, config.getKillerMessage()));
            victim.sendMessage(pendingDeath.placeholders.apply(victim, config.getVictimMessage()));
        }

        if (pendingDeath.richMessage != null && pendingDeath.legacyMessage.equals(event.getDeathMessage())) {
            broadcast(event, pendingDeath);
        }
    }

    /**
     * The killer is resolved from {@link Player#getLastDamageCause()}, which survives respawning. On 1.8, damage
     * without a source (and killing with {@code setHealth(0)} on any version) fires no damage event, so a death
     * in the new life would otherwise be blamed on whatever killed the player last time.
     */
    @EventHandler
    public void onRespawn(PlayerRespawnEvent event) {
        event.getPlayer().setLastDamageCause(null);
    }

    /**
     * Sends the hoverable message ourselves (servers without Adventure), honoring the gamerule like vanilla.
     */
    private void broadcast(PlayerDeathEvent event, PendingDeath pendingDeath) {
        if (!showsDeathMessages(event.getEntity().getWorld())) {
            return;
        }

        event.setDeathMessage("");
        plugin.getServer().spigot().broadcast(pendingDeath.richMessage);
        plugin.getServer().getConsoleSender().sendMessage(pendingDeath.legacyMessage);
    }

    @SuppressWarnings("deprecation") // string game rules are the only option before 1.13
    private static boolean showsDeathMessages(World world) {
        try {
            // typed game rules exist since 1.13, and since 1.21.11 the string names are no longer accepted
            Boolean value = world.getGameRuleValue(GameRule.SHOW_DEATH_MESSAGES);
            return value == null || value;
        } catch (LinkageError e) {
            try {
                return !"false".equalsIgnoreCase(world.getGameRuleValue("showDeathMessages"));
            } catch (IllegalArgumentException unknownGameRule) {
                return true;
            }
        }
    }

    private void dropHead(PlayerDeathEvent event, String name) {
        try {
            // Add to the event drops (rather than spawning it) so grave/death-chest plugins can capture it
            event.getDrops().add(PlayerHeads.create(event.getEntity(), name));
        } catch (RuntimeException | LinkageError e) {
            // A failing head drop must never prevent the death message from being sent
            plugin.getLogger().log(Level.WARNING, "Could not create a player head for " + event.getEntity().getName(), e);
        }
    }

    /**
     * Paper makes death events cancellable; Spigot does not.
     */
    private static boolean isCancelled(PlayerDeathEvent event) {
        return event instanceof Cancellable && ((Cancellable) event).isCancelled();
    }

    private static final class PendingDeath {
        private final Entity killer;
        private final Placeholders placeholders;
        private String legacyMessage;
        private BaseComponent[] richMessage;

        private PendingDeath(Entity killer, Placeholders placeholders) {
            this.killer = killer;
            this.placeholders = placeholders;
        }
    }
}
