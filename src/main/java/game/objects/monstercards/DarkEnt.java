package game.objects.monstercards;

import game.objects.CardBase;
import game.objects.enums.CardsEnum;
import game.objects.enums.ElementType;
import game.objects.enums.FightOutcome;

public class DarkEnt extends MonsterCard {
    public DarkEnt() {
        super("Dark Ent", 16, ElementType.NORMAL, CardsEnum.DARK_ENT.getRarity());
    }

    @Override
    /*
     * The Dark Ents have such an anger against the Orks and Knights that they immediately smash them in a fight
     */
    public FightOutcome attack(CardBase competitor) {
        if (competitor instanceof Ork || competitor instanceof Knight) {
            return FightOutcome.ATTACKER;
        }
        return super.attack(competitor);
    }
}
