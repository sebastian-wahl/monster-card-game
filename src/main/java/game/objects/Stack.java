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
}
