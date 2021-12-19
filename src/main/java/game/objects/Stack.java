package game.objects;

import java.util.ArrayList;
import java.util.List;

public class Stack {
    private List<CardBase> stack;

    public Stack() {
        stack = new ArrayList<>();
    }

    public void addPackageToStack(Package p) {
        this.stack.addAll(p.cards);
    }

    public void addCardsToStack(List<CardBase> cards) {
        this.stack.addAll(cards);
    }

    public void removeCardsFromStack(List<CardBase> cards) {
        this.stack.removeAll(cards);
    }

    public void removeCardForTrade() {

    }
}
