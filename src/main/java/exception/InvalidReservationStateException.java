package exception;

public class InvalidReservationStateException extends Exception {
    public InvalidReservationStateException(String message) {
        super(message);
    }
}
