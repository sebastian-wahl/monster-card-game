package game.objects.card.factory;

import game.objects.CardBase;
import game.objects.enums.CardsEnum;
import game.objects.exceptions.CardNotFoundException;
import game.objects.monstercards.*;
import game.objects.spellcards.*;

import java.util.Arrays;
import java.util.List;

/**
 * Class for Card creation
 */
public class CardFactory {
    // hide constructor
    private CardFactory() {
    }

    public static final List<CardsEnum> CARD_LIST = Arrays.asList(
            // Monstercards
            CardsEnum.DRAGON,
            CardsEnum.FIRE_ELF,
            CardsEnum.FIRE_WIZARD,
            CardsEnum.GREY_GOBLIN,
            CardsEnum.KNIGHT,
            CardsEnum.KRAKEN,
            CardsEnum.ORK,
            CardsEnum.DARK_BAT,
            CardsEnum.DARK_ENT,
            // Spellcards
            CardsEnum.DARK_SPELL,
            CardsEnum.FIRE_SPELL,
            CardsEnum.WATER_SPELL,
            CardsEnum.SLOWNESS_SPELL,
            CardsEnum.SPEED_SPELL
    );

    public static CardBase createCard(CardsEnum toCreate) {
        // Monstercards
        if (toCreate == CardsEnum.DRAGON) {
            return new Dragon();
        }
        if (toCreate == CardsEnum.FIRE_ELF) {
            return new FireElf();
        }
        if (toCreate == CardsEnum.FIRE_WIZARD) {
            return new FireWizard();
        }
        if (toCreate == CardsEnum.GREY_GOBLIN) {
            return new GreyGoblin();
        }
        if (toCreate == CardsEnum.KNIGHT) {
            return new Knight();
        }
        if (toCreate == CardsEnum.KRAKEN) {
            return new Knight();
        }
        if (toCreate == CardsEnum.ORK) {
            return new Ork();
        }
        if (toCreate == CardsEnum.DARK_BAT) {
            return new DarkBat();
        }
        if (toCreate == CardsEnum.DARK_ENT) {
            return new DarkEnt();
        }
        if (toCreate == CardsEnum.WATER_WITCH) {
            return new WaterWitch();
        }
        // Spellcards
        if (toCreate == CardsEnum.DARK_SPELL) {
            return new DarkSpell();
        }
        if (toCreate == CardsEnum.FIRE_SPELL) {
            return new FireSpell();
        }
        if (toCreate == CardsEnum.WATER_SPELL) {
            return new WaterSpell();
        }
        if (toCreate == CardsEnum.SPEED_SPELL) {
            return new SpeedSpell();
        }
        if (toCreate == CardsEnum.SLOWNESS_SPELL) {
            return new SlownessSpell();
        }

        // Exception does not need to be checked since this should only happen when a card was forgotten to add to this list
        throw new CardNotFoundException(toCreate);
    }

}
