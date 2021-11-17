package game.objects.monstercards;

import game.objects.CardBase;
import game.objects.enums.ElementType;
import game.objects.enums.FightOutcome;
import game.objects.spellcards.WaterSpell;

public class Knight extends MonsterCard {
    public Knight() {
        super("Knight", 15, ElementType.NORMAL);
    }

    @Override
    /*
     * The armor of Knights is so heavy that WaterSpells make them drown them instantly.
     */
    public FightOutcome attack(CardBase competitor) {
        if (competitor instanceof WaterSpell) {
            return FightOutcome.DEFENDER;
        } else {
            return super.attack(competitor);
        }
    }
}
