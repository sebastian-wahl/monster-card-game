package game.objects.monstercards;

import game.objects.CardBase;
import game.objects.enums.CardsEnum;
import game.objects.enums.ElementType;
import game.objects.enums.FightOutcome;
import game.objects.spellcards.SlownessSpell;

public class DarkBat extends MonsterCard {
    public DarkBat() {
        super("Dark Bat", 10, ElementType.NORMAL, CardsEnum.DARK_BAT.getRarity());
    }

    @Override
    /*
     * Dark Bats are so fast that the Slowness Spell does not work on them
     */
    public FightOutcome attack(CardBase competitor) {
        if (competitor instanceof SlownessSpell) {
            return FightOutcome.ATTACKER;
        }
        return super.attack(competitor);
    }
}
