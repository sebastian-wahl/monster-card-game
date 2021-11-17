package game.objects.monstercards;

import game.objects.CardBase;
import game.objects.enums.CardsEnum;
import game.objects.enums.ElementType;
import game.objects.enums.FightOutcome;
import game.objects.spellcards.WaterSpell;

public class Knight extends MonsterCard {
    public Knight() {
        super("Knight", 22, ElementType.NORMAL, CardsEnum.KNIGHT.getRarity());
    }

    @Override
    /*
     * The armor of Knights is so heavy that WaterSpells make them drown them instantly.
     * The DarkEnts have such an anger against the Orks and Knights that they immediately smash them in a fight
     */
    public FightOutcome attack(CardBase competitor) {
        if (competitor instanceof WaterSpell || competitor instanceof DarkEnt) {
            return FightOutcome.DEFENDER;
        }
        return super.attack(competitor);
    }
}
