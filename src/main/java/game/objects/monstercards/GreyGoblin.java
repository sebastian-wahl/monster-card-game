package game.objects.monstercards;

import game.objects.CardBase;
import game.objects.enums.CardsEnum;
import game.objects.enums.ElementType;
import game.objects.enums.FightOutcome;

public class GreyGoblin extends MonsterCard {

    public GreyGoblin() {
        super("Grey Goblin", 14, ElementType.NORMAL, CardsEnum.GREY_GOBLIN.getRarity());
    }

    @Override
    /*
     * Goblins are too afraid of Dragons to attack.
     */
    public FightOutcome attack(CardBase competitor) {
        if (competitor instanceof Dragon) {
            return FightOutcome.DEFENDER;
        }
        return super.attack(competitor);
    }
}
