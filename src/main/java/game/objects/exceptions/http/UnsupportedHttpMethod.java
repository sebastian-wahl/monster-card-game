package game.objects.exceptions.http;

public class UnsupportedHttpMethod extends RuntimeException {
    public UnsupportedHttpMethod(String methode) {
        super("Die Http Methode " + methode + " wird nicht unterstützt!");
    }
}
