package dbobjects;

import lombok.Data;

@Data
public abstract class CardBase {
    private String name;
    private final int damage;
    private ElementType elementType;
    private CardType cardType;

    private boolean isSpellImmune = false;

    public CardBase(String name, int damage, ElementType elementType, CardType cardType, boolean isSpellImmune) {
        this(name, damage, elementType, cardType);
        this.isSpellImmune = isSpellImmune;
    }

    public CardBase(String name, int damage, ElementType elementType, CardType cardType) {
        this.name = name;
        this.damage = damage;
        this.elementType = elementType;
        this.cardType = cardType;
    }
}
