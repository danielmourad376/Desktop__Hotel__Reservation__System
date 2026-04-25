package back_end_classes;

import java.time.LocalDate;
import back_end_classes.exception.*;

public class Receptionist extends Staff {

    //Constructor
    public Receptionist(String username, String password, LocalDate dob, int hours ){
        super(username, password, dob, Role.RECEPTIONIST, hours);
    }
    @Override
    public void performDuties() {} //to be used in milestone 2


    //methods:
    public void checkIn(int reservationId) throws InvalidCheckInException {
        Reservation res = HotelDatabaseSearch.findReservationById(reservationId);

            if (res == null) {
                throw new InvalidCheckInException("Reservation #" + reservationId + " not found.");

            }
            if (res.getStatus() != ReservationStatus.CONFIRMED) {
                throw new InvalidCheckInException("Reservation #" + reservationId
                        + " cannot be checked in. Current status: " + res.getStatus()
                        + ". Only CONFIRMED reservations are eligible.");
            }

            if (!res.getRoom().isAvailable()) {
                throw new InvalidCheckInException("Room " + res.getRoom().getRoomNumber()
                        + " is already occupied.");
            }

        res.getRoom().setAvailable(false);
        System.out.println("Check-in successful. Guest " + res.getGuest().getUsername()
                + " is now in Room " + res.getRoom().getRoomNumber() + ".");
    }

    //front desk payment method
    public void checkOut(int reservationId, PaymentMethod method) throws InvalidCheckOutException, InvalidPaymentException{
        Reservation res = HotelDatabaseSearch.findReservationById(reservationId);
        if(res == null){
            throw new InvalidCheckOutException("Reservation #" + reservationId + " not found.");
        }
        //receptionist generates invoice at desk
        Invoice finalInvoice = res.processCheckout(method);

        System.out.println("Front Desk Check-out successful. Guest " + res.getGuest().getUsername()
                + " has left Room " + res.getRoom().getRoomNumber() + ".");
    }



    public void manageReservation(){//milestone 2
    }

}

