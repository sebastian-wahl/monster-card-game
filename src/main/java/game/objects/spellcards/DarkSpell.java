package game.objects.spellcards;

import game.objects.enums.CardsEnum;
import game.objects.enums.ElementType;

public class DarkSpell extends SpellCard {
    public DarkSpell() {
        super("Dark Spell", 35, ElementType.NORMAL, CardsEnum.DARK_SPELL.getRarity());
    }
}
