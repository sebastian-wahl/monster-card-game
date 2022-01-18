package game.objects;

import java.util.*;

public class Deck {

    private List<CardBase> deckList;

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
            deckList = new ArrayList<>(cards.subList(0, 4));
        } else {
            deckList = cards;
        }
    }

    public CardBase getRandomCard() {
        Random rand = new Random();
        return deckList.get(rand.nextInt(deckList.size()));
    }

    public void removeCard(CardBase card) {
        this.deckList.remove(card);
    }

    public void addCard(CardBase card) {
        this.deckList.add(card);
    }

    public int getDeckSize() {
        return this.deckList.size();
    }

    public boolean isDeckEmpty() {
        return this.deckList.isEmpty();
    }

    public List<CardBase> getCards() {
        return this.deckList;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Deck)) return false;
        Deck deck1 = (Deck) o;
        return Objects.equals(deckList, deck1.deckList);
    }

    @Override
    public int hashCode() {
        return Objects.hash(deckList);
    }

    public String toPlainString() {
        StringBuilder sb = new StringBuilder();

        sb.append("{");
        if (!this.deckList.isEmpty()) {
            sb.append("\"Deck\": ");
            sb.append(this.deckList.size() > 1 ? "[" : "");
            for (int i = 0; i < this.deckList.size(); i++) {
                sb.append(this.deckList.get(i).toPlainString());
                if (i < this.deckList.size() - 1) {
                    sb.append(", ");
                }
            }
            sb.append(this.deckList.size() > 1 ? "]" : "");
        } else {
            sb.append("\"Deck\": \"Empty\"");
        }
        sb.append("}");

        return sb.toString();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        sb.append("{");
        if (!this.deckList.isEmpty()) {
            sb.append("\"Deck\": ");
            sb.append(this.deckList.size() > 1 ? "[" : "");
            for (int i = 0; i < this.deckList.size(); i++) {
                sb.append(this.deckList.get(i).toString());
                if (i < this.deckList.size() - 1) {
                    sb.append(", ");
                }
            }
            sb.append(this.deckList.size() > 1 ? "]" : "");
        } else {
            sb.append("\"Deck\": \"Empty\"");
        }
        sb.append("}");
        return sb.toString();
    }
}
