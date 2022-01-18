package game.objects.monstercards;

import game.objects.CardBase;
import game.objects.enums.CardsEnum;
import game.objects.enums.ElementType;
import game.objects.enums.FightOutcome;
import game.objects.spellcards.SpellCard;

public class Kraken extends MonsterCard {
    public Kraken() {
        super("Kraken", 23, ElementType.WATER, CardsEnum.KRAKEN.getRarity(),
                "The kraken is immune against spells.");
    }

    @Override
    /*
     * The Kraken is immune against spells
     */
    public FightOutcome attack(CardBase competitor) {
        if (competitor instanceof SpellCard) {
            return FightOutcome.ATTACKER;
        }
        return super.attack(competitor);
    }
}
