package dbobjects.monstercards;

import dbobjects.CardBase;
import dbobjects.CardType;
import dbobjects.ElementType;

/**
 * Immune against spells
 */
public class Kraken extends CardBase {
    public Kraken() {
        super("Kraken", 30, ElementType.WATER, CardType.MONSTER, true);
    }
}
