package game.objects.exceptions;

import game.objects.enums.CardsEnum;

public class CardNotFoundException extends RuntimeException {
    public CardNotFoundException(CardsEnum cardsEnum) {
        super("No card to the Enum " + cardsEnum + " was found!");
    }
}
