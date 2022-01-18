package game.objects.monstercards;

import game.objects.CardBase;
import game.objects.enums.CardsEnum;
import game.objects.enums.ElementType;
import game.objects.enums.FightOutcome;

public class DarkEnt extends MonsterCard {
    public DarkEnt() {
        super("Dark Ent", 16, ElementType.NORMAL, CardsEnum.DARK_ENT.getRarity(),
                "The dark ents are so enraged by the orcs and knights that they immediately destroy them in a fight.");
    }

    @Override
    /*
     * The dark ents are so enraged by the orcs and knights that they immediately destroy them in a fight.
     */
    public FightOutcome attack(CardBase competitor) {
        if (competitor instanceof Ork || competitor instanceof Knight) {
            return FightOutcome.ATTACKER;
        }
        return super.attack(competitor);
    }
}
