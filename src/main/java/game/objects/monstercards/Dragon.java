package game.objects.monstercards;

import game.objects.CardBase;
import game.objects.enums.ElementType;
import game.objects.enums.FightOutcome;

public class Dragon extends MonsterCard {
    public Dragon() {
        super("Dragon", 22, ElementType.FIRE);
    }

    @Override
    /*
     * Goblins are too afraid of Dragons to attack.
     * The FireElves know Dragons since they were little and can evade their attacks.
     */
    public FightOutcome attack(CardBase competitor) {
        if (competitor instanceof Dragon) {
            return FightOutcome.ATTACKER;
        } else if (competitor instanceof FireElf) {
            return FightOutcome.DEFENDER;
        } else {
            return super.attack(competitor);
        }
    }
}