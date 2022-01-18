package game.objects.spellcards;

import game.objects.CardBase;
import game.objects.enums.CardsEnum;
import game.objects.enums.ElementType;
import game.objects.enums.FightOutcome;
import game.objects.monstercards.Knight;

public class WaterSpell extends SpellCard {
    public WaterSpell() {
        super("Water Spell", 25, ElementType.WATER, CardsEnum.WATER_SPELL.getRarity(),
                "The armor of a knight is so heavy that water spells make them drown them instantly.");
    }

    @Override
    /*
     * The armor of Knights is so heavy that WaterSpells make them drown them instantly.
     */
    public FightOutcome attack(CardBase competitor) {
        if (competitor instanceof Knight) {
            return FightOutcome.ATTACKER;
        }
        return super.attack(competitor);
    }

}
