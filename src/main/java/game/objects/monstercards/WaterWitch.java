package game.objects.monstercards;

import game.objects.CardBase;
import game.objects.enums.CardsEnum;
import game.objects.enums.ElementType;
import game.objects.enums.FightOutcome;
import game.objects.spellcards.SpellCard;

public class WaterWitch extends MonsterCard {
    public WaterWitch() {
        super("Water Witch", 15, ElementType.WATER, CardsEnum.WATER_WITCH.getRarity(),
                "The water witch is immune against all spells. " +
                        "Since the water witch knows which runes the fire wizard uses, she can dodge all of his attacks.");
    }

    @Override
    /*
     * The water witch is immune against all spells.
     * Since the water witch knows which runes the fire wizard uses, she can dodge all of his attacks.
     */
    public FightOutcome attack(CardBase competitor) {
        if (competitor instanceof SpellCard || competitor instanceof FireWizard) {
            return FightOutcome.ATTACKER;
        }
        return super.attack(competitor);
    }
}
