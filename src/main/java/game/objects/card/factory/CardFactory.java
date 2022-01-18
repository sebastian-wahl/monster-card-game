package game.objects.card.factory;

import game.objects.CardBase;
import game.objects.enums.CardsEnum;
import game.objects.exceptions.CardNotFoundException;
import game.objects.monstercards.*;
import game.objects.spellcards.*;

/**
 * Class for Card creation
 */
public class CardFactory {
    // hide constructor
    private CardFactory() {
    }

    public static CardBase createCard(CardsEnum toCreate, String id) {
        CardBase toReturn = null;
        // Monstercards
        toReturn = createMonstercard(toCreate);
        // Spellcards
        if (toReturn == null) {
            toReturn = createSpellcard(toCreate);
        }
        if (toReturn != null) {
            if (id != null) {
                toReturn.setId(id);
            }
            return toReturn;
        } else {
            // Exception does not need to be checked since this should only happen when a card was forgotten to add to this list
            throw new CardNotFoundException(toCreate);
        }
    }

    private static CardBase createSpellcard(CardsEnum toCreate) {
        CardBase toReturn = null;
        if (toCreate == CardsEnum.DARK_SPELL) {
            toReturn = new DarkSpell();
        }
        if (toCreate == CardsEnum.FIRE_SPELL) {
            toReturn = new FireSpell();
        }
        if (toCreate == CardsEnum.WATER_SPELL) {
            toReturn = new WaterSpell();
        }
        if (toCreate == CardsEnum.SPEED_SPELL) {
            toReturn = new SpeedSpell();
        }
        if (toCreate == CardsEnum.SLOWNESS_SPELL) {
            toReturn = new SlownessSpell();
        }
        return toReturn;
    }

    private static CardBase createMonstercard(CardsEnum toCreate) {
        CardBase toReturn = null;
        if (toCreate == CardsEnum.DRAGON) {
            toReturn = new Dragon();
        }
        if (toCreate == CardsEnum.FIRE_ELF) {
            toReturn = new FireElf();
        }
        if (toCreate == CardsEnum.FIRE_WIZARD) {
            toReturn = new FireWizard();
        }
        if (toCreate == CardsEnum.GREY_GOBLIN) {
            toReturn = new GreyGoblin();
        }
        if (toCreate == CardsEnum.KNIGHT) {
            toReturn = new Knight();
        }
        if (toCreate == CardsEnum.KRAKEN) {
            toReturn = new Knight();
        }
        if (toCreate == CardsEnum.ORC) {
            toReturn = new Orc();
        }
        if (toCreate == CardsEnum.DARK_BAT) {
            toReturn = new DarkBat();
        }
        if (toCreate == CardsEnum.DARK_ENT) {
            toReturn = new DarkEnt();
        }
        if (toCreate == CardsEnum.WATER_WITCH) {
            toReturn = new WaterWitch();
        }
        return toReturn;
    }

}
