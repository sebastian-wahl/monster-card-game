package game.http;

public enum HttpReady {
    // header
    AUTHORIZATION_KEY("Authorization"),
    CONTENT_LENGTH_KEY("content-length"),
    CONTENT_TYPE_KEY("content-type"),
    SERVER_NAME_KEY("server"),
    CONTENT_TYPE_APPLICATION_JSON("application/json"),
    CONTENT_TYPE_TEXT_PLAIN("text/plain");

    private String text;

    HttpReady(String text) {
        this.text = text;
    }

    @Override
    public String toString() {
        return this.text;
    }
}
