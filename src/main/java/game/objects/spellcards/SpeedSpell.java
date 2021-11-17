package game.objects.spellcards;

import game.objects.enums.CardsEnum;
import game.objects.enums.ElementType;

public class SpeedSpell extends SpellCard {
    public SpeedSpell() {
        super("Speed Spell", 30, ElementType.NORMAL, CardsEnum.SPEED_SPELL.getRarity());
    }
}
