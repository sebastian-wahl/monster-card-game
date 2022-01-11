package game.http;

public enum HttpReady {
    // header
    AUTHORIZATION_KEY("Authorization"),
    CONTENT_LENGTH_KEY("Content-Length"),
    CONTENT_TYPE_KEY("Content-Type"),
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
