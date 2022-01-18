package game.objects.spellcards;

import game.objects.CardBase;
import game.objects.enums.CardsEnum;
import game.objects.enums.ElementType;
import game.objects.enums.FightOutcome;
import game.objects.monstercards.DarkBat;

public class SlownessSpell extends SpellCard {
    public SlownessSpell() {
        super("Slowness Spell", 20, ElementType.NORMAL, CardsEnum.SLOWNESS_SPELL.getRarity(),
                "Dark Bats are so fast that the Slowness Spell does not work on them");
    }

    @Override
    /*
     * Dark Bats are so fast that the Slowness Spell does not work on them
     */
    public FightOutcome attack(CardBase competitor) {
        if (competitor instanceof DarkBat) {
            return FightOutcome.DEFENDER;
        }
        return super.attack(competitor);
    }
}
