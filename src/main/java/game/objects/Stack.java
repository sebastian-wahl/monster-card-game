package game.objects;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Stack {
    private List<CardBase> stack;

    public Stack() {
        stack = new ArrayList<>();
    }

    public Stack(List<CardBase> stack) {
        this.stack = stack;
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

    public boolean contains(CardBase card) {
        return this.stack.contains(card);
    }

    public boolean containsAll(List<CardBase> cards) {
        return this.stack.containsAll(cards);
    }

    public boolean containsAllStringId(List<String> cardIds) {
        return this.stack.stream().map(cardBase -> cardBase.getId().toString()).collect(Collectors.toList()).containsAll(cardIds);
    }

    public void removeAllCardsThatAreInvolvedInTrade() {
        this.stack = this.stack.stream().filter(cardBase -> !cardBase.isInTradeInvolved()).collect(Collectors.toList());
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        sb.append("{");
        if (!this.stack.isEmpty()) {
            sb.append("\"Stack\": ");
            sb.append(this.stack.size() > 1 ? "[" : "");
            for (int i = 0; i < this.stack.size(); i++) {
                sb.append(this.stack.get(i).toString());
                if (i < this.stack.size() - 1) {
                    sb.append(", ");
                }
            }
            sb.append(this.stack.size() > 1 ? "]" : "");
        } else {
            sb.append("\"Stack\": \"Empty\"");
        }
        sb.append("}");
        return sb.toString();
    }
}
