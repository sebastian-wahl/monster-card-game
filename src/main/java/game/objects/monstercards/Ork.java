package game.objects.monstercards;

import game.objects.CardBase;
import game.objects.enums.ElementType;
import game.objects.enums.FightOutcome;

public class Ork extends MonsterCard {
    public Ork() {
        super("Big Ork", 18, ElementType.NORMAL);
    }

    @Override
    /*
     * Wizard can control Orks, so they are not able to damage them.
     */
    public FightOutcome attack(CardBase competitor) {
        if (competitor instanceof FireWizard) {
            return FightOutcome.DEFENDER;
        } else {
            return super.attack(competitor);
        }
    }

}
