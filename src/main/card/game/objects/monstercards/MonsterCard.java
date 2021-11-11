package card.game.objects.monstercards;

import card.game.objects.CardBase;
import card.game.objects.ElementType;
import card.game.objects.FightOutcome;

public abstract class MonsterCard extends CardBase {

    public MonsterCard(String name, int damage, ElementType elementType) {
        super(name, damage, elementType);
    }

    @Override
    public FightOutcome attack(CardBase competitor) {
        if (competitor instanceof MonsterCard) {
            if (getDamage() == competitor.getDamage())
                return FightOutcome.TIE;
            else
                return getDamage() > competitor.getDamage() ? FightOutcome.ATTACKER : FightOutcome.DEFENDER;
        } else {
            return attackSpell(competitor);
        }
    }
}
