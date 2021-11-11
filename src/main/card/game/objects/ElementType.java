package card.game.objects;

public enum ElementType {
    FIRE("Fire"),
    WATER("Water"),
    NORMAL("Normal");

    private final String text;

    ElementType(final String text) {
        this.text = text;
    }

    @Override
    public String toString() {
        return text;
    }

}
