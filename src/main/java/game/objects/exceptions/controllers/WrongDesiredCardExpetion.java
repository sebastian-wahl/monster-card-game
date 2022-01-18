package game.objects.exceptions.controllers;

public class WrongDesiredCardExpetion extends Exception {
    public WrongDesiredCardExpetion(String cardName) {
        super("The desired card: \"" + cardName + "\" is not a card in this game!");
    }
}
