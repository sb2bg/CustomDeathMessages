package org.spartandevs.customdeathmessages.util;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;

/**
 * Maps damage causes and entity types to the config message list used for them.
 * <p>
 * Every entity type and damage cause also has a conventional list name: its lower-case name with dashes,
 * followed by {@code -messages} (e.g. {@code CREAKING -> creaking-messages}). When the config contains such a
 * list it is used, so new mobs only need a config entry, not a plugin update. Otherwise the built-in mapping
 * below applies, falling back to {@link #UNKNOWN}.
 */
public final class MessagePaths {
    public static final String SUFFIX = "-messages";
    public static final String UNKNOWN = "unknown-messages";
    public static final String PVP = "global-pvp-death-messages";
    public static final String MELEE = "melee-death-messages";
    public static final String CUSTOM_NAMED_ENTITY = "custom-name-entity-messages";

    private static final Map<String, String> DAMAGE_CAUSES = damageCauses();
    private static final Map<String, String> ENTITY_TYPES = entityTypes();
    /**
     * Entity types whose conventional list name means something else ({@code wither-messages} is the
     * wither effect, not the boss).
     */
    private static final Set<String> NO_CONVENTIONAL_ENTITY_PATH = Collections.singleton("WITHER");

    private MessagePaths() {
    }

    /**
     * @param hasList whether the config contains a message list at a path
     * @return the list for the damage cause, or {@code null} if it has none
     */
    public static String forDamageCause(Enum<?> cause, Predicate<String> hasList) {
        return resolve(cause.name(), DAMAGE_CAUSES, hasList, true);
    }

    /**
     * @param hasList whether the config contains a message list at a path
     * @return the list for the entity type, or {@code null} if it has none
     */
    public static String forEntityType(Enum<?> type, Predicate<String> hasList) {
        String name = type.name();
        return resolve(name, ENTITY_TYPES, hasList, !NO_CONVENTIONAL_ENTITY_PATH.contains(name));
    }

    public static String conventionalPath(String enumName) {
        return enumName.toLowerCase(Locale.ROOT).replace('_', '-') + SUFFIX;
    }

    public static boolean isMessagePath(String path) {
        return path.endsWith(SUFFIX);
    }

    private static String resolve(String name, Map<String, String> mapping, Predicate<String> hasList, boolean allowConventional) {
        if (allowConventional) {
            String conventional = conventionalPath(name);

            if (hasList.test(conventional)) {
                return conventional;
            }
        }

        return mapping.get(name);
    }

    private static Map<String, String> damageCauses() {
        Map<String, String> paths = new HashMap<>();
        map(paths, "unknown-messages", "CUSTOM", "ENTITY_ATTACK", "ENTITY_SWEEP_ATTACK");
        map(paths, "arrow-messages", "PROJECTILE");
        map(paths, "falling-block-messages", "FALLING_BLOCK");
        map(paths, "void-messages", "VOID", "WORLD_BORDER");
        map(paths, "fall-damage-messages", "FALL");
        map(paths, "fire-messages", "FIRE", "CAMPFIRE", "MELTING");
        map(paths, "fire-tick-messages", "FIRE_TICK");
        map(paths, "suicide-messages", "SUICIDE", "KILL");
        map(paths, "lava-messages", "LAVA");
        map(paths, "drowning-messages", "DROWNING", "DRYOUT");
        map(paths, "starvation-messages", "STARVATION");
        map(paths, "potion-messages", "POISON", "MAGIC");
        map(paths, "wither-messages", "WITHER");
        map(paths, "thorns-messages", "THORNS");
        map(paths, "suffocation-messages", "SUFFOCATION");
        map(paths, "cactus-messages", "CONTACT");
        map(paths, "tnt-messages", "BLOCK_EXPLOSION");
        map(paths, "creeper-messages", "ENTITY_EXPLOSION");
        map(paths, "lightning-messages", "LIGHTNING");
        map(paths, "dragon-breath-messages", "DRAGON_BREATH");
        map(paths, "elytra-messages", "FLY_INTO_WALL");
        map(paths, "magma-block-messages", "HOT_FLOOR");
        map(paths, "cramming-messages", "CRAMMING");
        map(paths, "freeze-messages", "FREEZE");
        map(paths, "warden-sonic-boom-messages", "SONIC_BOOM");
        return Collections.unmodifiableMap(paths);
    }

    private static Map<String, String> entityTypes() {
        Map<String, String> paths = new HashMap<>();
        map(paths, PVP, "PLAYER");
        map(paths, "arrow-messages", "ARROW", "SPECTRAL_ARROW", "TIPPED_ARROW");
        map(paths, "fireball-messages", "FIREBALL", "SMALL_FIREBALL");
        map(paths, "witherboss-messages", "WITHER", "WITHER_SKULL");
        map(paths, "tnt-messages", "TNT", "PRIMED_TNT", "TNT_MINECART", "MINECART_TNT");
        map(paths, "firework-messages", "FIREWORK", "FIREWORK_ROCKET");
        map(paths, "lightning-messages", "LIGHTNING", "LIGHTNING_BOLT");
        map(paths, "falling-block-messages", "FALLING_BLOCK");
        map(paths, "end-crystal-messages", "ENDER_CRYSTAL", "END_CRYSTAL");
        map(paths, "elderguardian-messages", "ELDER_GUARDIAN");
        map(paths, "witherskeleton-messages", "WITHER_SKELETON");
        map(paths, "stray-messages", "STRAY");
        map(paths, "husk-messages", "HUSK");
        map(paths, "shulker-messages", "SHULKER", "SHULKER_BULLET");
        map(paths, "dragon-messages", "ENDER_DRAGON", "DRAGON_FIREBALL");
        map(paths, "zombievillager-messages", "ZOMBIE_VILLAGER");
        map(paths, "evoker-messages", "EVOKER", "EVOKER_FANGS");
        map(paths, "vex-messages", "VEX");
        map(paths, "vindicator-messages", "VINDICATOR");
        map(paths, "illusioner-messages", "ILLUSIONER");
        map(paths, "creeper-messages", "CREEPER");
        map(paths, "skeleton-messages", "SKELETON", "BOGGED", "PARCHED");
        map(paths, "spider-messages", "SPIDER");
        map(paths, "zombie-messages", "ZOMBIE", "GIANT");
        map(paths, "slime-messages", "SLIME");
        map(paths, "ghast-messages", "GHAST");
        map(paths, "zombified-piglin-messages", "ZOMBIFIED_PIGLIN");
        map(paths, "pigman-messages", "PIG_ZOMBIE");
        map(paths, "enderman-messages", "ENDERMAN");
        map(paths, "cavespider-messages", "CAVE_SPIDER");
        map(paths, "silverfish-messages", "SILVERFISH");
        map(paths, "blaze-messages", "BLAZE");
        map(paths, "magmacube-messages", "MAGMA_CUBE");
        map(paths, "witch-messages", "WITCH");
        map(paths, "endermite-messages", "ENDERMITE");
        map(paths, "guardian-messages", "GUARDIAN");
        map(paths, "wolf-messages", "WOLF");
        map(paths, "golem-messages", "IRON_GOLEM");
        map(paths, "polar-bear-messages", "POLAR_BEAR");
        map(paths, "llama-messages", "LLAMA", "TRADER_LLAMA", "LLAMA_SPIT");
        map(paths, "phantom-messages", "PHANTOM");
        map(paths, "drowned-messages", "DROWNED", "TRIDENT");
        map(paths, "pufferfish-messages", "PUFFERFISH");
        map(paths, "dolphin-messages", "DOLPHIN");
        map(paths, "panda-messages", "PANDA");
        map(paths, "pillager-messages", "PILLAGER");
        map(paths, "ravager-messages", "RAVAGER");
        map(paths, "fox-messages", "FOX");
        map(paths, "bee-messages", "BEE");
        map(paths, "hoglin-messages", "HOGLIN");
        map(paths, "piglin-messages", "PIGLIN", "PIGLIN_BRUTE");
        map(paths, "zoglin-messages", "ZOGLIN");
        map(paths, "goat-messages", "GOAT");
        map(paths, "breeze-messages", "BREEZE", "BREEZE_WIND_CHARGE");
        map(paths, "warden-messages", "WARDEN");
        return Collections.unmodifiableMap(paths);
    }

    private static void map(Map<String, String> paths, String path, String... names) {
        for (String name : names) {
            paths.put(name, path);
        }
    }

    /**
     * @return every message list path the plugin knows about by default
     */
    public static Set<String> builtInPaths() {
        Set<String> paths = new HashSet<>(DAMAGE_CAUSES.values());
        paths.addAll(ENTITY_TYPES.values());
        paths.add(UNKNOWN);
        paths.add(MELEE);
        paths.add(CUSTOM_NAMED_ENTITY);
        return paths;
    }
}
