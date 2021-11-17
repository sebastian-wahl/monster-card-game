package game.http.URL;

public enum PathEnum {
    BATTLE("battle"),
    USER("user"),
    // ToDo add other paths
    // no match
    NOMATCH(null);

    private final String text;

    PathEnum(final String text) {
        this.text = text;
    }

    @Override
    public String toString() {
        return text;
    }

    public static PathEnum getEnumFromString(String text) {
        switch (text) {
            case "battle":
                return BATTLE;
            // ToDo add path conversion
            default:
                return NOMATCH;
        }
    }
}
