package game.objects.spellcards;

import game.objects.CardBase;
import game.objects.enums.ElementType;
import game.objects.enums.FightOutcome;

public abstract class SpellCard extends CardBase {
    public SpellCard(String name, int damage, ElementType elementType) {
        super(name, damage, elementType);
    }

    @Override
    public FightOutcome attack(CardBase competitor) {
        return attackSpell(competitor);
    }

}
