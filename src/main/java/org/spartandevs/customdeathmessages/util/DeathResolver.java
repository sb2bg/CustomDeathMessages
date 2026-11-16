package org.spartandevs.customdeathmessages.util;

import org.bukkit.entity.*;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.projectiles.ProjectileSource;
import org.spartandevs.customdeathmessages.CustomDeathMessages;

import java.util.EnumSet;
import java.util.Set;

/**
 * Works out what killed a player from the damage that actually killed them.
 * <p>
 * {@link Player#getLastDamageCause()} is only updated for damage that was applied (cancelled damage and
 * damage negated by a totem never replace a later cause), so it is a reliable record of the lethal hit.
 */
public final class DeathResolver {
    /**
     * Damage causes whose dedicated message list is more specific than the attacking entity's list.
     */
    private static final Set<DamageCause> CAUSE_OVER_ENTITY = causes("THORNS", "DRAGON_BREATH", "SONIC_BOOM");

    private DeathResolver() {
    }

    public static Death resolve(CustomDeathMessages plugin, Player victim) {
        EntityDamageEvent lastDamage = victim.getLastDamageCause();

        if (lastDamage == null) {
            // e.g. plugins killing the player with setHealth(0)
            return new Death(DeathCause.UNKNOWN, null, false);
        }

        DamageCause damageCause = lastDamage.getCause();

        if (!(lastDamage instanceof EntityDamageByEntityEvent)) {
            return new Death(DeathCause.from(damageCause, plugin), null, false);
        }

        Entity damager = ((EntityDamageByEntityEvent) lastDamage).getDamager();
        Entity attacker = responsibleEntity(damager);

        if (victim.equals(attacker)) {
            // their own arrow, firework, trident, ender pearl...: nobody else gets credit for the kill
            DeathCause byDamager = damager.equals(victim) ? null : DeathCause.fromOrNull(damager.getType());
            return new Death(byDamager != null ? byDamager : DeathCause.from(damageCause, plugin), null, false);
        }

        if (CAUSE_OVER_ENTITY.contains(damageCause)) {
            return new Death(DeathCause.from(damageCause, plugin), attacker, false);
        }

        // TNT keeps its own messages even when a player lit it; otherwise use the responsible entity's messages
        Entity messageEntity = damager instanceof TNTPrimed ? damager : attacker;
        Entity killer = attacker;
        DeathCause cause = DeathCause.from(messageEntity.getType(), plugin);

        // WITHER is both a potion effect damage cause and an entity; as an entity it is the boss
        if (cause == DeathCause.WITHER) {
            cause = DeathCause.WITHER_BOSS;
        }

        if (isCustomNamedMob(messageEntity) && plugin.getConfigManager().isCustomNamedEntityMessageEnabled()) {
            cause = DeathCause.CUSTOM_NAMED_ENTITY;
        }

        // a melee kill is a player hitting the victim directly
        boolean melee = killer instanceof Player && damager.equals(killer);
        return new Death(cause, killer, melee);
    }

    /**
     * The entity responsible for damage dealt by {@code damager}: the shooter of a projectile, the owner of
     * evoker fangs or an area effect cloud, the igniter of TNT, otherwise the damager itself.
     */
    static Entity responsibleEntity(Entity damager) {
        Entity source = null;

        if (damager instanceof Projectile) {
            source = asEntity(((Projectile) damager).getShooter());
        } else if (damager instanceof TNTPrimed) {
            source = ((TNTPrimed) damager).getSource();
        } else {
            // these entity types do not exist on every supported version, so compare names before touching them
            String type = damager.getType().name();

            if (type.equals("AREA_EFFECT_CLOUD")) {
                source = Modern.areaEffectCloudSource(damager);
            } else if (type.equals("EVOKER_FANGS")) {
                source = Modern.evokerFangsOwner(damager);
            }
        }

        return source != null ? source : damager;
    }

    private static boolean isCustomNamedMob(Entity entity) {
        if (entity instanceof Player) {
            return false;
        }

        String name = entity.getCustomName();

        // Plugins such as mcMMO temporarily rename mobs to show a health bar; those are not real names
        return name != null && !name.contains("❤") && !name.contains("♥") && !name.contains("♡");
    }

    private static Entity asEntity(ProjectileSource source) {
        return source instanceof Entity ? (Entity) source : null;
    }

    private static Set<DamageCause> causes(String... names) {
        Set<DamageCause> causes = EnumSet.noneOf(DamageCause.class);

        for (String name : names) {
            try {
                causes.add(DamageCause.valueOf(name));
            } catch (IllegalArgumentException ignored) {
                // not present on this server version
            }
        }

        return causes;
    }

    /**
     * Only loaded when the entity types above exist, so 1.8 never resolves these classes.
     */
    private static final class Modern {
        static Entity areaEffectCloudSource(Entity cloud) {
            return asEntity(((AreaEffectCloud) cloud).getSource());
        }

        static Entity evokerFangsOwner(Entity fangs) {
            return ((EvokerFangs) fangs).getOwner();
        }
    }

    public static final class Death {
        private final DeathCause cause;
        private final Entity killer;
        private final boolean melee;

        Death(DeathCause cause, Entity killer, boolean melee) {
            this.cause = cause;
            this.killer = killer;
            this.melee = melee;
        }

        public DeathCause getCause() {
            return cause;
        }

        /**
         * @return the entity credited with the kill, never the victim themselves; {@code null} for
         * environmental and self-inflicted deaths
         */
        public Entity getKiller() {
            return killer;
        }

        /**
         * @return whether a player killed the victim with a direct hit
         */
        public boolean isMelee() {
            return melee;
        }
    }
}
