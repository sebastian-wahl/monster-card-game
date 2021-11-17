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
    DRAGON(RarityEnum.MODERATELY_RARE),
    FIRE_ELF(RarityEnum.COMMON),
    FIRE_WIZARD(RarityEnum.SLIGHTLY_RARE),
    GREY_GOBLIN(RarityEnum.COMMON),
    KNIGHT(RarityEnum.SLIGHTLY_RARE),
    KRAKEN(RarityEnum.MODERATELY_RARE),
    ORK(RarityEnum.COMMON),
    DARK_BAT(RarityEnum.COMMON),
    DARK_ENT(RarityEnum.COMMON),
    WATER_WITCH(RarityEnum.VERY_RARE),
    // Spellcards
    DARK_SPELL(RarityEnum.MODERATELY_RARE),
    FIRE_SPELL(RarityEnum.VERY_RARE),
    WATER_SPELL(RarityEnum.RARE),
    SLOWNESS_SPELL(RarityEnum.COMMON),
    SPEED_SPELL(RarityEnum.SLIGHTLY_RARE);


    private final RarityEnum rarity;

    CardsEnum(RarityEnum rarity) {
        this.rarity = rarity;
    }

    public RarityEnum getRarity() {
        return rarity;
    }

}
