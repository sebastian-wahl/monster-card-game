package game.http.exceptions;

public class UnsupportedHttpMethod extends RuntimeException {
    public UnsupportedHttpMethod(String methode) {
        super("Die Http Methode " + methode + " wird nicht unterstützt!");
    }
}
