package back_end_classes;

import java.time.LocalDate;
import java.util.ArrayList;
import exception.*;

public class Guest extends User{
    //data Fields
    private Double balance;
    private Gender gender;
    private String address;
    private String roomPreferences;

    //constructors
    public Guest(String username, String password, LocalDate dOb, Gender gender, double balance, String address){
        this(username, password, dOb, gender, balance, address, "None");
    }
    public Guest(String username, String password, LocalDate dOb, Gender gender, double balance, String address, String roomPreferences) {
        super(username, password, dOb);

        setGender(gender);
        setBalance(balance);
        setAddress(address);
        setRoomPreferences(roomPreferences);
    }

    //methods
    public void register(){
        if (HotelDatabaseSearch.findUserByUsername(this.getUsername()) != null) {
            throw new IllegalArgumentException("Username '" + this.getUsername() + "' is already taken.");
        }

        HotelDatabase.getInstance().addGuest(this);
        System.out.println("Guest " + this.getUsername() + " registered successfully.");
    }

    public void reserveRoom(int roomNumber, LocalDate checkIn, LocalDate checkOut)  throws RoomNotFoundException, RoomNotAvailableException  {
        Room room = HotelDatabaseSearch.findRoomByNumber(roomNumber);
        if (room == null) {
            throw new RoomNotFoundException(roomNumber);
        }

        if (!HotelDatabaseSearch.isRoomAvailableOnDates(room, checkIn, checkOut)) {
            throw new RoomNotAvailableException(roomNumber);
        }

            Reservation newRes = new Reservation(this, room, checkIn, checkOut);
            HotelDatabase.getInstance().addReservation(newRes);
            System.out.println("Reservation successful");

    }

    public void cancelReservation(int reservationId) throws UnauthorizedActionException, InvalidReservationStateException {
        Reservation res = HotelDatabaseSearch.findReservationById(reservationId);

        if (res == null) {
            throw new IllegalArgumentException("Reservation #" + reservationId + " not found.");
        }
        if (!res.getGuest().equals(this)) {
            throw new UnauthorizedActionException("You do not have permission to cancel Reservation #" + reservationId);
        }
        res.cancel();
    }

    public void addBalance(double amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("Validation Error: Cannot add a negative or zero amount.");
        }
            this.balance += amount;
            System.out.println("Successfully added $" + amount + " to wallet. New balance: $" + this.balance);

    }

    public void deductBalance(double amount) throws InvalidPaymentException{
        if (amount <= 0) {
            throw new IllegalArgumentException("Validation Error: Invalid charge amount.");
        }
        if (this.balance < amount) {
            throw new InvalidPaymentException(this.balance, amount);
        }
        this.balance -= amount;
    }

    public void viewAvailableRooms() {
        ArrayList<Room> available = HotelDatabaseSearch.findAvailableRooms();
        System.out.println("=== Available Rooms ===");
        if (available.isEmpty()) {
            System.out.println("No rooms currently available.");
        } else {
            for (int i = 0; i < available.size(); i++) {
                Room r = available.get(i);
                System.out.println("Room " + r.getRoomNumber() + " | Type: " + r.getRoomType().getName() + " | Price/Night: $" + r.calculatePrice());

                System.out.println("   Included Amenities: " + r.getAmenities());
                System.out.println("--------------------------------------------------");
            }
        }
    }

    public void viewMyReservations() {
        ArrayList<Reservation> allRes = HotelDatabase.getInstance().getReservations();
        System.out.println("Reservations: ");
        boolean found = false;

        for (int i = 0; i < allRes.size(); i++) {
            Reservation res = allRes.get(i);
            if (res.getGuest().equals(this)) {
                System.out.println(res.toString());
                found = true;
            }
        }

        if (!found) {
            System.out.println("You have no active reservations.");
        }
    }

    //online payment method so user doesn't have to go to receptionist's desk to check out (can't use cash payment method)
    public void checkoutAndPay(int resId, PaymentMethod method) throws
            UnauthorizedActionException, InvalidPaymentException, InvalidCheckOutException {

        Reservation targetRes = HotelDatabaseSearch.findReservationById(resId);

        if (method == PaymentMethod.CASH) {
            throw new InvalidCheckOutException("Invalid payment method");
        }

        if (targetRes == null) {
            throw new InvalidCheckOutException("Reservation #" + resId + " not found.");
        }

        if (!targetRes.getGuest().equals(this)) {
            throw new UnauthorizedActionException("Guest " + this.getUsername() + " cannot checkout Reservation #" + resId);
        }

        Invoice inv = targetRes.processCheckout(method);
        System.out.println("Checkout complete. Safe travels! Your Invoice ID is #" + inv.getInvoiceId());

        inv.generateInvoice();

    }

    //getters and setters
    public double getBalance() { return balance; }
    public String getRoomPreferences(){return roomPreferences;}
    public Gender getGender(){return gender;}
    public String getAddress(){return address;}



    public void setBalance(double balance) {
        if (balance <= 0)
            throw new IllegalArgumentException("Balance must be positive.");
        this.balance = balance;
    }
    public void setGender(Gender gender){
        if (gender == null)
            throw new IllegalArgumentException("Gender cannot be null.");
        this.gender = gender;
    }

    public void setAddress(String address) {
        if (address == null || address.trim().isEmpty())
            throw new IllegalArgumentException("Address cannot be empty.");
        this.address = address.trim();
    }

    public void setRoomPreferences(String roomPreferences) {
        if (roomPreferences == null)
            throw new IllegalArgumentException("Room preferences cannot be null.");
        this.roomPreferences = roomPreferences;
    }
}
