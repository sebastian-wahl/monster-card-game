package game.objects.monstercards;

import game.objects.CardBase;
import game.objects.enums.ElementType;
import game.objects.enums.FightOutcome;

public class FireWizard extends MonsterCard {
    public FireWizard() {
        super("Fire Wizard", 25, ElementType.FIRE);
    }

    @Override
    /*
     * Wizard can control Orks, so they are not able to damage them.
     */
    public FightOutcome attack(CardBase competitor) {
        if (competitor instanceof Ork) {
            return FightOutcome.ATTACKER;
        } else {
            return super.attack(competitor);
        }
    }
}
