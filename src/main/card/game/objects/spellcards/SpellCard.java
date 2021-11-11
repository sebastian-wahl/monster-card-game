package card.game.objects.spellcards;

import card.game.controller.ImmuneCardController;
import card.game.objects.CardBase;
import card.game.objects.ElementType;
import card.game.objects.FightOutcome;

public abstract class SpellCard extends CardBase {
    public SpellCard(String name, int damage, ElementType elementType) {
        super(name, damage, elementType);
    }

    @Override
    public FightOutcome attack(CardBase competitor) {
        if (ImmuneCardController.isCardImmuneAgainstSpells(competitor))
            return FightOutcome.DEFENDER;
        else
            return attackSpell(competitor);
    }

}
