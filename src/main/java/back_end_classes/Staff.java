package back_end_classes;

import java.time.LocalDate;
import java.util.ArrayList;


public abstract class Staff extends User{
    //dataFields
    private final Role role;
    private int workingHours;

    //Constructor
    public Staff(String username, String password, LocalDate dob, Role role, int hours ){
        super(username, password, dob);
        this.role = role;
        setWorkingHours(hours);

    }
    //abstract method for Milestone 2
    public abstract void performDuties();

    //viewing Methods
    public ArrayList<Guest> viewAllGuests(){
        return HotelDatabase.getInstance().getGuests();
    }

    public ArrayList<Amenity> viewAllAmenities(){return HotelDatabase.getInstance().getAmenities();}

    public ArrayList<Reservation> viewAllReservations(){
        return HotelDatabase.getInstance().getReservations();
    }

    public ArrayList<Room> viewAllRooms() {
        return HotelDatabase.getInstance().getRooms();
    }

    public ArrayList<RoomType> viewAllRoomTypes() { return HotelDatabase.getInstance().getRoomTypes(); }

    //getters & setters
    public Role getRole(){return role;}

    public int getWorkingHours(){return workingHours;}
    public void setWorkingHours(int workingHours) {
        if (workingHours <= 0){
            throw new IllegalArgumentException("Working hours must be greater than zero.");}
        if (workingHours > 60){
            throw new IllegalArgumentException("Working hours cannot exceed 60 (hours in a week).");}
        this.workingHours = workingHours;
    }
}
