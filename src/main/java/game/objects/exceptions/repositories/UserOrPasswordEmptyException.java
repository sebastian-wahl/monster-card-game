package game.objects.exceptions.repositories;

public class UserOrPasswordEmptyException extends RuntimeException {
    public UserOrPasswordEmptyException(String message) {
        super(message);
    }
}
