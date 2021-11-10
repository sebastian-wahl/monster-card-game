package dbobjects.spellcards;

import dbobjects.CardBase;
import dbobjects.CardType;
import dbobjects.ElementType;

public class FireSpell extends CardBase {
    public FireSpell() {
        super("Fire Spell", 90, ElementType.FIRE, CardType.SPELL);
    }
}
