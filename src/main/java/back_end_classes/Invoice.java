package back_end_classes;

import java.time.LocalDate;
import exception.*;

public class Invoice implements Payable {
    // Attributes
    private static int invoiceCounter = 5000;
    private final int invoiceId;
    private final Reservation reservation;
    private final double totalAmount;
    private boolean paid;
    private final LocalDate issueDate;
    private PaymentMethod paymentMethod;
    private LocalDate paymentDate;

    // Constructor
    public Invoice(Reservation reservation) {
        this.invoiceId = ++invoiceCounter;
        this.reservation = reservation;
        this.issueDate = LocalDate.now();
        this.paid = false;
        this.totalAmount = reservation.calculateTotal();
    }

    // Methods
    public void generateInvoice() {
        System.out.println(this.toString());
    }

    @Override
    public void pay(double amount, PaymentMethod method) throws InvalidPaymentException {
        ReservationStatus currentStatus = this.reservation.getStatus();

        if (currentStatus != ReservationStatus.CONFIRMED) {
            throw new InvalidPaymentException("Error: You can only pay for Confirmed reservations.");
        }

        if (this.paid) {
            System.out.println("Invoice #" + invoiceId + " is already paid.");
            return;
        }

        if (amount >= totalAmount) {
            this.paid = true;
            this.paymentMethod = method;
            this.paymentDate = LocalDate.now();
            System.out.println("Invoice #" + invoiceId + " paid in full. Amount: $" + totalAmount + " via " + method);


            if (amount > totalAmount) {
                System.out.println("Change due: $" + (amount - totalAmount));
            }
        } else {
            System.out.println("Insufficient amount. Total due: $" + totalAmount);
        }
    }

    // Getters
    @Override
    public double getTotalAmount() { return totalAmount; }

    public Reservation getReservation() { return reservation; }
    public PaymentMethod getPaymentMethod() { return paymentMethod; }
    public LocalDate getPaymentDate() { return paymentDate; }
    public int getInvoiceId() { return invoiceId; }

    @Override
    public boolean isPaid() { return paid; }

    public LocalDate getIssueDate() { return issueDate; }

    //setter
    public static void setIdCounter(int newCount) {
        invoiceCounter = newCount;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("========== INVOICE #").append(getInvoiceId()).append(" ==========\n");
        sb.append("Issue Date : ").append(getIssueDate()).append("\n");
        sb.append("-------------------------------------------\n");

        sb.append("  ").append(getReservation().toString()).append("\n");
        sb.append("-------------------------------------------\n");
        sb.append("TOTAL      : $").append(getTotalAmount()).append("\n");
        sb.append("STATUS     : ").append(isPaid() ? "PAID" : "UNPAID").append("\n");
        if (paid) {
            sb.append("PAID VIA   : ").append(getPaymentMethod()).append(" on ").append(getPaymentDate()).append("\n");
        }
        sb.append("===========================================");

        return sb.toString();
    }
}
