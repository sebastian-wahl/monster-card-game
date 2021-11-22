package game.objects.exceptions;

public class DeckNotFoundException extends Exception {
    public DeckNotFoundException(String username) {
        super("Deck for User: " + username + " not found!");
    }
}
