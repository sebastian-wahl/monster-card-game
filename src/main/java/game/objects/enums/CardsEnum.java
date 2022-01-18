package game.objects.enums;

/**
 * Enum for all game cards combined with a rarity
 * Rarity:
 * ~ 0.0 - 0.5999: common
 * ~ 0.6 - 0.7999: slightly rare
 * ~ 0.8 - 0.8999: moderately rare
 * ~ 0.9 - 0.9555: rare
 * ~ 0.9556 - 0.999: very rare
 * ~ 1: ultimately rare
 */
public enum CardsEnum {
    // Monstercards
    DRAGON(RarityEnum.MODERATELY_RARE, "Dragon"),
    FIRE_ELF(RarityEnum.COMMON, "Fire Elf"),
    FIRE_WIZARD(RarityEnum.SLIGHTLY_RARE, "Fire Wizard"),
    GREY_GOBLIN(RarityEnum.COMMON, "Grey Goblin"),
    KNIGHT(RarityEnum.SLIGHTLY_RARE, "Knight"),
    KRAKEN(RarityEnum.MODERATELY_RARE, "Kraken"),
    ORC(RarityEnum.COMMON, "Orc"),
    DARK_BAT(RarityEnum.COMMON, "Dark Bat"),
    DARK_ENT(RarityEnum.COMMON, "Dark Ent"),
    WATER_WITCH(RarityEnum.VERY_RARE, "Water Witch"),
    // Spellcards
    DARK_SPELL(RarityEnum.MODERATELY_RARE, "Dark Spell"),
    FIRE_SPELL(RarityEnum.VERY_RARE, "Fire Spell"),
    WATER_SPELL(RarityEnum.RARE, "Water Spell"),
    SLOWNESS_SPELL(RarityEnum.COMMON, "Slowness Spell"),
    SPEED_SPELL(RarityEnum.SLIGHTLY_RARE, "Speed Spell");

    private final RarityEnum rarity;
    private final String name;

    CardsEnum(RarityEnum rarity, String name) {
        this.rarity = rarity;
        this.name = name;
    }

    public RarityEnum getRarity() {
        return rarity;
    }

    public String getName() {
        return this.name;
    }
}
