package exception;

public class UnauthorizedActionException extends Exception{
    public UnauthorizedActionException(String action) {
        super("Unauthorized action: " + action);
    }
}
