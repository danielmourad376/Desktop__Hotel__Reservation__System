package exception;

public class InvalidPaymentException extends Exception{
    public InvalidPaymentException(double balance, double amountDue) {
        super("Insufficient funds. Balance: $" + balance + " | Amount due: $" + amountDue);
    }

    public InvalidPaymentException(String message) {
        super(message);
    }
}
