package game.objects;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Deck {

    private List<CardBase> deck;

    // Deck init size = max 4 cards
    public static final int INIT_MAX_DECK_SIZE = 4;

    public Deck(List<CardBase> cards) {
        if (cards.size() > INIT_MAX_DECK_SIZE) {
            deck = new ArrayList<>(cards.subList(0, 3));
        } else {
            deck = cards;
        }
    }

    public CardBase getRandomCard() {
        Random rand = new Random();
        return deck.get(rand.nextInt(deck.size()));
    }

    public void removeCard(CardBase card) {
        this.deck.remove(card);
    }

    public void addCard(CardBase card) {
        this.deck.add(card);
    }

    public int getDeckSize() {
        return this.deck.size();
    }

    public boolean isDeckEmpty() {
        return this.deck.isEmpty();
    }
}
