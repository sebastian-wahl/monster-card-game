package game.objects;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class Deck {

    private List<CardBase> deck;

    // Deck init size = max 4 cards
    public static final int INIT_MAX_DECK_SIZE = 4;

    public Deck() {
        this(Collections.emptyList());
    }

    public Deck(List<CardBase> cards) {
        this.setDeck(cards);
    }

    public void setDeck(List<CardBase> cards) {
        if (cards.size() > INIT_MAX_DECK_SIZE) {
            deck = new ArrayList<>(cards.subList(0, 4));
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

    public List<CardBase> getCards() {
        return this.deck;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        sb.append("{");
        if (!this.deck.isEmpty()) {
            sb.append("\"Deck\": ");
            sb.append(this.deck.size() > 1 ? "[" : "");
            for (int i = 0; i < this.deck.size(); i++) {
                sb.append(this.deck.get(i).toString());
                if (i < this.deck.size() - 1) {
                    sb.append(", ");
                }
            }
            sb.append(this.deck.size() > 1 ? "]" : "");
        } else {
            sb.append("\"Deck\": \"Empty\"");
        }
        sb.append("}");
        return sb.toString();
    }
}
