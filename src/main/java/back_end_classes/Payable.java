package back_end_classes;
import back_end_classes.exception.*;

public interface Payable {
    double getTotalAmount();
    void pay(double amount, PaymentMethod method) throws InvalidPaymentException;
    boolean isPaid();
}
