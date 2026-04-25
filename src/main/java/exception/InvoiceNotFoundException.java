package exception;

public class InvoiceNotFoundException extends Exception {
    public InvoiceNotFoundException(int invoiceId) {
        super("Invoice #" + invoiceId + " does not exist in the system.");
    }
}
