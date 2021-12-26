package game.objects.monstercards;

import game.objects.CardBase;
import game.objects.enums.CardsEnum;
import game.objects.enums.ElementType;
import game.objects.enums.FightOutcome;

public class Ork extends MonsterCard {
    public Ork() {
        super("Ork", 18, ElementType.NORMAL, CardsEnum.ORK.getRarity());
    }

    @Override
    /*
     * Wizard can control Orks, so they are not able to damage them.
     * DarkEnts have such an anger against the Orks and Knights that they immediately smash them in a fight
     */
    public FightOutcome attack(CardBase competitor) {
        if (competitor instanceof FireWizard || competitor instanceof DarkEnt) {
            return FightOutcome.DEFENDER;
        }
        return super.attack(competitor);
    }

}
