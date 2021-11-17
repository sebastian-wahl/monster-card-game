package game.objects.spellcards;

import game.objects.CardBase;
import game.objects.enums.ElementType;
import game.objects.enums.FightOutcome;
import game.objects.monstercards.Knight;

public class WaterSpell extends SpellCard {
    public WaterSpell() {
        super("Water Spell", 20, ElementType.WATER);
    }

    @Override
    /*
     * The armor of Knights is so heavy that WaterSpells make them drown them instantly.
     */
    public FightOutcome attack(CardBase competitor) {
        if (competitor instanceof Knight) {
            return FightOutcome.ATTACKER;
        } else {
            return super.attack(competitor);
        }
    }

}
