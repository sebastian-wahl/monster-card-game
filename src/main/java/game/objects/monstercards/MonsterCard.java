package game.objects.monstercards;

import game.objects.CardBase;
import game.objects.enums.ElementType;
import game.objects.enums.FightOutcome;

public abstract class MonsterCard extends CardBase {

    protected MonsterCard(String name, double damage, ElementType elementType) {
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
