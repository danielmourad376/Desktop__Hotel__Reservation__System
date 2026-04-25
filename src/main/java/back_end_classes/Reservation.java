package back_end_classes;

import exception.*;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;


public class Reservation {

    // Attributes
    private static int idCounter = 1000;
    private final int reservationId;
    private final Guest guest;
    private final Room room;
    private LocalDate checkInDate;
    private LocalDate checkOutDate;
    private ReservationStatus status;

    // Constructor
    public Reservation(Guest guest, Room room, LocalDate checkInDate, LocalDate checkOutDate) {
        if (!checkInDate.isBefore(checkOutDate)) {
            throw new IllegalArgumentException("Check-in date must be strictly before check-out date.");
        }
        if (guest == null) {
            throw new IllegalArgumentException("Guest cannot be null.");
        }
        if (room == null) {
            throw new IllegalArgumentException("Room cannot be null.");
        }

        this.reservationId = ++idCounter;
        this.guest = guest;
        this.room = room;
        setCheckInDate(checkInDate);
        setCheckOutDate(checkOutDate);
        setStatus(ReservationStatus.PENDING);
    }


    public void confirmReservation() throws InvalidReservationStateException {

        if (this.getStatus() != ReservationStatus.PENDING) {
            throw new InvalidReservationStateException("Only PENDING reservations can be approved. Current status: "
                + this.getStatus());
        }
        this.setStatus(ReservationStatus.CONFIRMED);
        System.out.println("Reservation #" + reservationId + " confirmed.");
    }

    public void cancel() throws InvalidReservationStateException {
        if (this.status != ReservationStatus.PENDING && this.status != ReservationStatus.CONFIRMED) {
            throw new InvalidReservationStateException("Cannot cancel a reservation that is already " + this.status);
        }
        if (!room.isAvailable()) {
            throw new InvalidReservationStateException("Cannot cancel after check-in. You must formally checkout to pay your balance.");
        }

        room.setAvailable(true);
        this.status = ReservationStatus.CANCELLED;
        System.out.println("Reservation cancelled.");
    }

    public double calculateTotal() {
        long nights = ChronoUnit.DAYS.between(checkInDate, checkOutDate);
        if (nights == 0) nights = 1;
        /*if the guest checked in and out on same day
         then nights would equal 0 giving them a free stay
          so we set nights to 1 for the minimum days of a stay*/
        return nights * room.calculatePrice();
    }

    public Invoice processCheckout(PaymentMethod method) throws InvalidCheckOutException, InvalidPaymentException {

        if (this.status != ReservationStatus.CONFIRMED) {
            throw new InvalidCheckOutException("Reservation #" + reservationId
                    + " cannot be checked out. Current status: " + this.status);
        }
        if (this.room.isAvailable()) {
            throw new InvalidCheckOutException("Room " + room.getRoomNumber()
                    + " is not currently occupied. Guest may not have checked in yet.");
        }

        //invoice generation
        Invoice finalInvoice = new Invoice(this);
        HotelDatabase.getInstance().addInvoice(finalInvoice);
        double amountDue = finalInvoice.getTotalAmount();

        /*Deduct Balance if guest has enough
        to cover amount due if they pay ONLINE*/
        if (method == PaymentMethod.ONLINE) {
            this.guest.deductBalance(amountDue);
        }

        //Finalize the Transaction
        finalInvoice.pay(amountDue, method);
        this.room.setAvailable(true);
        this.status = ReservationStatus.COMPLETED;

        return finalInvoice; // return the invoice obj
    }


    //getters for final data fields
    public Guest getGuest() { return guest; }
    public int getReservationId() { return reservationId; }
    public Room getRoom() { return room; }

    //getters and setters (non final)
    public LocalDate getCheckInDate() { return checkInDate; }
    public void setCheckInDate(LocalDate checkInDate) {
        if (checkInDate == null)
            throw new IllegalArgumentException("Check-in date cannot be null.");
        // only validate order if checkOutDate is already set
        if (this.checkOutDate != null && !checkInDate.isBefore(this.checkOutDate))
            throw new IllegalArgumentException("Check-in date must be before check-out date.");
        this.checkInDate = checkInDate;
    }

    public LocalDate getCheckOutDate() { return checkOutDate; }
    public void setCheckOutDate(LocalDate checkOutDate) {
        if (checkOutDate == null)
            throw new IllegalArgumentException("Check-out date cannot be null.");
        // only validate order if checkInDate is already set
        if (this.checkInDate != null && !checkOutDate.isAfter(this.checkInDate))
            throw new IllegalArgumentException("Check-out date must be after check-in date.");
        this.checkOutDate = checkOutDate;
    }

    public ReservationStatus getStatus() { return status; }
    public void setStatus(ReservationStatus status) {
        if (status == null)
            throw new IllegalArgumentException("Reservation status cannot be null.");
        this.status = status;
    }

    @Override
    public String toString() {
        return "Reservation #" + reservationId +
                " | Guest: " + guest.getUsername() +
                " | Room: " + room.getRoomNumber() +
                " | Check-in: " + checkInDate +
                " | Check-out: " + checkOutDate +
                " | Total: $" + calculateTotal() +
                " | Status: " + status;
    }
}