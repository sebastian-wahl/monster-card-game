package game.objects.enums;

/**
 * Enum for all game cards combined with a rarity:
 * ~ 0 - 60: common
 * ~ 61 - 79: slightly rare
 * ~ 80 - 89: moderately rare
 * ~ 90 - 0.95: rare
 * ~ 96 - 99: very rare
 * ~ 100: ultimately rare
 */
public enum RarityEnum {
    COMMON(0, 60, "Common"),
    SLIGHTLY_RARE(61, 79, "Slightly Rare"),
    MODERATELY_RARE(80, 89, "Moderately Rare"),
    RARE(90, 95, "Rare"),
    VERY_RARE(96, 100, "Very Rare"),
    ULTIMATELY_RARE(100, 100, "Ultimately Rare");

    public static final int MAX_RARITY = 100;


    private final int rarityStart;
    private final int rarityEnd;
    private final String rarityString;

    RarityEnum(int rarityStart, int rarityEnd, String rarityString) {
        this.rarityStart = rarityStart;
        this.rarityEnd = rarityEnd;
        this.rarityString = rarityString;
    }

    public int getRarityStart() {
        return rarityStart;
    }

    public int getRarityEnd() {
        return rarityEnd;
    }

    @Override
    public String toString() {
        return this.rarityString;
    }
}
