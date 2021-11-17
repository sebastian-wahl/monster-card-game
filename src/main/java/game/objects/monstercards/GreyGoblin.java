package game.objects.monstercards;

import game.objects.CardBase;
import game.objects.enums.ElementType;
import game.objects.enums.FightOutcome;

public class GreyGoblin extends MonsterCard {

    public GreyGoblin() {
        super("Grey Goblin", 10, ElementType.NORMAL);
    }

    @Override
    /*
     * Goblins are too afraid of Dragons to attack.
     */
    public FightOutcome attack(CardBase competitor) {
        if (competitor instanceof Dragon) {
            return FightOutcome.DEFENDER;
        } else {
            return super.attack(competitor);
        }
    }
}
