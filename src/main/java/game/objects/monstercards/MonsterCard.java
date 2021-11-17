package game.objects.monstercards;

import game.objects.CardBase;
import game.objects.enums.ElementType;
import game.objects.enums.FightOutcome;
import game.objects.enums.RarityEnum;

public abstract class MonsterCard extends CardBase {

    protected MonsterCard(String name, double damage, ElementType elementType, RarityEnum cardRarity) {
        super(name, damage, elementType, cardRarity);
    }

    @Override
    public FightOutcome attack(CardBase competitor) {
        if (competitor instanceof MonsterCard) {
            if (getDamage() == competitor.getDamage())
                return FightOutcome.TIE;
            else
                return getDamage() > competitor.getDamage() ? FightOutcome.ATTACKER : FightOutcome.DEFENDER;
        }
        return attackSpell(competitor);
    }
}
