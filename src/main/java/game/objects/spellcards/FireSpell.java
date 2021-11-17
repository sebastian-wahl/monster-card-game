package game.objects.spellcards;

import game.objects.enums.CardsEnum;
import game.objects.enums.ElementType;

public class FireSpell extends SpellCard {
    public FireSpell() {
        super("Fire Spell", 55, ElementType.FIRE, CardsEnum.FIRE_SPELL.getRarity());
    }

}
