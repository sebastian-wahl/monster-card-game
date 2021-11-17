package game.objects.monstercards;

import game.objects.CardBase;
import game.objects.enums.ElementType;
import game.objects.enums.FightOutcome;
import game.objects.spellcards.SpellCard;

public class Kraken extends MonsterCard {
    public Kraken() {
        super("Kraken", 30, ElementType.WATER);
    }

    @Override
    /*
     * The Kraken is immune against spells
     */
    public FightOutcome attack(CardBase competitor) {
        if (competitor instanceof SpellCard) {
            return FightOutcome.ATTACKER;
        } else {
            return super.attack(competitor);
        }
    }
}
