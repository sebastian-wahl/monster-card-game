package dbobjects.spellcards;

import dbobjects.CardBase;
import dbobjects.CardType;
import dbobjects.ElementType;

public class WaterSpell extends CardBase {
    public WaterSpell() {
        super("Water Spell", 20, ElementType.WATER, CardType.SPELL);
    }
}
