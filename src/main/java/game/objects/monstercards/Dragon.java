package game.objects.monstercards;

import game.objects.CardBase;
import game.objects.enums.CardsEnum;
import game.objects.enums.ElementType;
import game.objects.enums.FightOutcome;

public class Dragon extends MonsterCard {
    public Dragon() {
        super("Dragon", 27, ElementType.FIRE, CardsEnum.DRAGON.getRarity(),
                "Goblins are too afraid of dragons to attack. " +
                        "Fire elves have known dragons since they were little and can dodge their attacks.");
    }

    @Override
    /*
     * Goblins are too afraid of dragons to attack.
     * Fire elves have known dragons since they were little and can dodge their attacks.
     */
    public FightOutcome attack(CardBase competitor) {
        if (competitor instanceof GreyGoblin) {
            return FightOutcome.ATTACKER;
        } else if (competitor instanceof FireElf) {
            return FightOutcome.DEFENDER;
        }
        return super.attack(competitor);
    }
}
