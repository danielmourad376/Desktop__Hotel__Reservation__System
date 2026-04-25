package back_end_classes;
import exception.*;

public interface Payable {
    double getTotalAmount();
    void pay(double amount, PaymentMethod method) throws InvalidPaymentException;
    boolean isPaid();
}
