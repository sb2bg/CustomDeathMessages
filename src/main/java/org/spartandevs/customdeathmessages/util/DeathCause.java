package org.spartandevs.customdeathmessages.util;

import org.spartandevs.customdeathmessages.CustomDeathMessages;

import java.util.Arrays;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public enum DeathCause {

    UNKNOWN("unknown-messages"),
    CUSTOM("unknown-messages"),
    ENTITY_ATTACK("unknown-messages"),
    CUSTOM_NAMED_ENTITY("custom-name-entity-messages"),
    PROJECTILE("arrow-messages"),
    PLAYER("global-pvp-death-messages"),
    BLOCK("falling-block-messages"),
    VOID("void-messages"),
    WORLD_BORDER("void-messages"),
    FALL("fall-damage-messages"),
    FIRE("fire-messages"),
    CAMPFIRE("fire-messages"),
    FIRE_TICK("fire-tick-messages"),
    SUICIDE("suicide-messages"),
    KILL("suicide-messages"),
    LAVA("lava-messages"),
    DROWNING("drowning-messages"),
    STARVATION("starvation-messages"),
    POISON("potion-messages"),
    MAGIC("potion-messages"),
    WITHER("wither-messages"),
    WITHER_BOSS("witherboss-messages"),
    //    ANVIL("anvil-messages"),
    FALLING_BLOCK("falling-block-messages"),
    THORNS("thorns-messages"), // todo: add thorns messages?
    //    DRAGON_BREATH("dragon-breath-messages"),
    SUFFOCATION("suffocation-messages"),
    CONTACT("cactus-messages"),
    BLOCK_EXPLOSION("tnt-messages"),
    ENTITY_EXPLOSION("creeper-messages"),
    LIGHTNING("lightning-messages"),
    DRAGON_BREATH("dragon-breath-messages"),
    FLY_INTO_WALL("elytra-messages"),
    HOT_FLOOR("magma-block-messages"),
    CRAMMING("cramming-messages"),
    DRYOUT("drowning-messages"),
    FREEZE("freeze-messages"),
    MELTING("fire-messages"),
    SONIC_BOOM("warden-sonic-boom-messages"),
    ENTITY_SWEEP_ATTACK("unknown-messages"),
    ELDER_GUARDIAN("elderguardian-messages"),
    WITHER_SKELETON("witherskeleton-messages"),
    STRAY("stray-messages"),
    ARROW("arrow-messages"),
    FIREBALL("fireball-messages"),
    SMALL_FIREBALL("fireball-messages"),
    WITHER_SKULL("witherboss-messages"),
    PRIMED_TNT("tnt-messages"),
    FIREWORK("firework-messages"),
    HUSK("husk-messages"),
    SPECTRAL_ARROW("arrow-messages"),
    SHULKER_BULLET("shulker-messages"),
    DRAGON_FIREBALL("dragon-messages"),
    ZOMBIE_VILLAGER("zombievillager-messages"),
    EVOKER_FANGS("evoker-messages"),
    EVOKER("evoker-messages"),
    VEX("vex-messages"),
    VINDICATOR("vindicator-messages"),
    ILLUSIONER("illusioner-messages"),
    CREEPER("creeper-messages"),
    SKELETON("skeleton-messages"),
    SPIDER("spider-messages"),
    GIANT("zombie-messages"), // Maybe change to giant-messages?
    ZOMBIE("zombie-messages"),
    SLIME("slime-messages"),
    GHAST("ghast-messages"),
    ZOMBIFIED_PIGLIN("zombified-piglin-messages"),
    ENDERMAN("enderman-messages"),
    CAVE_SPIDER("cavespider-messages"),
    SILVERFISH("silverfish-messages"),
    BLAZE("blaze-messages"),
    MAGMA_CUBE("magmacube-messages"),
    ENDER_DRAGON("dragon-messages"),
    WITCH("witch-messages"),
    ENDERMITE("endermite-messages"),
    GUARDIAN("guardian-messages"),
    SHULKER("shulker-messages"),
    WOLF("wolf-messages"),
    IRON_GOLEM("golem-messages"),
    POLAR_BEAR("polar-bear-messages"),
    LLAMA("llama-messages"),
    LLAMA_SPIT("llama-messages"),
    ENDER_CRYSTAL("end-crystal-messages"),
    PHANTOM("phantom-messages"),
    TRIDENT("drowned-messages"),
    PUFFERFISH("pufferfish-messages"),
    DROWNED("drowned-messages"),
    DOLPHIN("dolphin-messages"),
    PANDA("panda-messages"),
    PILLAGER("pillager-messages"),
    RAVAGER("ravager-messages"),
    FOX("fox-messages"),
    BEE("bee-messages"),
    HOGLIN("hoglin-messages"),
    PIGLIN("piglin-messages"),
    PIG_ZOMBIE("pigman-messages"),
    ZOGLIN("zoglin-messages"),
    PIGLIN_BRUTE("piglin-messages"),
    GOAT("goat-messages"),
    BOGGED("skeleton-messages"),
    BREEZE("breeze-messages"),
    WARDEN("warden-messages"),
    MELEE_DEATH("melee-death-messages");

    private final String path;
    private static final Map<String, DeathCause> BY_NAME = Arrays.stream(values())
            .collect(Collectors.toMap(deathCause -> deathCause.name().toUpperCase(Locale.ROOT), deathCause -> deathCause));
    private static final Map<String, DeathCause> BY_ALIAS = Map.ofEntries(
            Map.entry("TNT", PRIMED_TNT),
            Map.entry("TNT_MINECART", PRIMED_TNT),
            Map.entry("MINECART_TNT", PRIMED_TNT),
            Map.entry("FIREWORK_ROCKET", FIREWORK),
            Map.entry("END_CRYSTAL", ENDER_CRYSTAL),
            Map.entry("LIGHTNING_BOLT", LIGHTNING)
    );
    private static final Map<String, DeathCause> BY_PATH = Arrays.stream(values())
            .collect(Collectors.toMap(deathCause -> deathCause.getPath().toLowerCase(Locale.ROOT), deathCause -> deathCause, (first, second) -> first));
    private static final Map<String, Set<DeathCause>> BY_PATH_SET = Arrays.stream(values())
            .collect(Collectors.groupingBy(deathCause -> deathCause.getPath().toLowerCase(Locale.ROOT), Collectors.toSet()));
    public static final Set<String> PATH_SET = Arrays.stream(DeathCause.values()).map(DeathCause::getPath).collect(Collectors.toSet());

    DeathCause(String path) {
        this.path = path;
    }

    public String getPath() {
        return path;
    }

    public static <E extends Enum<E>> DeathCause from(Enum<E> namedEnum, CustomDeathMessages plugin) {
        String name = namedEnum.name().toUpperCase(Locale.ROOT);
        DeathCause deathCause = BY_NAME.get(name);

        if (deathCause == null) {
            deathCause = BY_ALIAS.get(name);
        }

        if (deathCause != null) {
            return deathCause;
        }

        plugin.getLogger().warning("Unknown entity or damage cause '" + namedEnum.name() + "'. If you would like this message to be added, please leave a message on the plugin discussion.");
        return DeathCause.UNKNOWN;
    }

    public static Set<DeathCause> deathCauseWithPath(DeathCause cause) {
        return BY_PATH_SET.get(cause.getPath().toLowerCase(Locale.ROOT));
    }

    public static DeathCause fromPathSingle(String path) {
        return BY_PATH.get(path.toLowerCase(Locale.ROOT));
    }
}
