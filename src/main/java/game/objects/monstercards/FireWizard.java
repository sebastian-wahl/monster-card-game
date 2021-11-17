package game.objects.monstercards;

import game.objects.CardBase;
import game.objects.enums.CardsEnum;
import game.objects.enums.ElementType;
import game.objects.enums.FightOutcome;

public class FireWizard extends MonsterCard {
    public FireWizard() {
        super("Fire Wizard", 22, ElementType.FIRE, CardsEnum.FIRE_WIZARD.getRarity());
    }

    @Override
    /*
     * Wizard can control Orks, so they are not able to damage them.
     */
    public FightOutcome attack(CardBase competitor) {
        if (competitor instanceof Ork) {
            return FightOutcome.ATTACKER;
        }
        if (competitor instanceof WaterWitch) {
            return FightOutcome.DEFENDER;
        }
        return super.attack(competitor);
    }
}
