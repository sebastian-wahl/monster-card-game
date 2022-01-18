package game.objects.monstercards;

import game.objects.CardBase;
import game.objects.enums.CardsEnum;
import game.objects.enums.ElementType;
import game.objects.enums.FightOutcome;
import game.objects.spellcards.WaterSpell;

public class Knight extends MonsterCard {
    public Knight() {
        super("Knight", 22, ElementType.NORMAL, CardsEnum.KNIGHT.getRarity(),
                "The armor of a knight is so heavy that the water spells make them drown them instantly." +
                        "The dark ents are so enraged by the orcs and knights that they immediately destroy them in a fight.");
    }

    @Override
    /*
     * The armor of a knight is so heavy that the water spells make them drown them instantly.
     * The dark ents are so enraged by the orcs and knights that they immediately destroy them in a fight.
     */
    public FightOutcome attack(CardBase competitor) {
        if (competitor instanceof WaterSpell || competitor instanceof DarkEnt) {
            return FightOutcome.DEFENDER;
        }
        return super.attack(competitor);
    }
}
