package game.objects.spellcards;

import game.objects.CardBase;
import game.objects.enums.ElementType;
import game.objects.enums.FightOutcome;
import game.objects.enums.RarityEnum;
import game.objects.monstercards.Kraken;
import game.objects.monstercards.WaterWitch;

public abstract class SpellCard extends CardBase {
    protected SpellCard(String name, int damage, ElementType elementType, RarityEnum cardRarity) {
        super(name, damage, elementType, cardRarity);
    }

    protected SpellCard(String name, int damage, ElementType elementType, RarityEnum cardRarity, String desc) {
        super(name, damage, elementType, cardRarity, desc);
    }

    @Override
    public FightOutcome attack(CardBase competitor) {
        if (competitor instanceof Kraken || competitor instanceof WaterWitch) {
            return FightOutcome.DEFENDER;
        }
        return attackSpell(competitor);
    }

}