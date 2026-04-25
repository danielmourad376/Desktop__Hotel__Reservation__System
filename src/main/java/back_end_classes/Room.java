package back_end_classes;

import java.util.ArrayList;

public class Room {
    //private data fields

    private int roomNumber;
    private RoomType roomType;
    private ArrayList<Amenity> amenities;
    private boolean isAvailable;



    //constructor
    public Room( int number, RoomType type) {
        setRoomNumber(number);
        setRoomType(type);
        this.amenities = new ArrayList<>();
        this.isAvailable = true;
    }
    //methods

    public void addAmenity(Amenity amenity) {
        if (amenity == null) {throw new IllegalArgumentException("Amenity cannot be null.");}
        this.amenities.add(amenity);
    }

    public double calculatePrice() {
        double total = roomType.getBasePrice();
        for (int i = 0; i < amenities.size(); i++) {
            total += amenities.get(i).getPricePerDay();
        }
        return total;
    }

    //getters and setters

    public int getRoomNumber() { return roomNumber; }
    public void setRoomNumber(int roomNumber) {
        if (roomNumber <= 0)
            throw new IllegalArgumentException("Room number must be a positive integer.");
        this.roomNumber = roomNumber;
    }

    public RoomType getRoomType() { return roomType; }
    public void setRoomType(RoomType roomType) {
        if (roomType == null)
            throw new IllegalArgumentException("Room type cannot be null.");
        this.roomType = roomType;
    }

    public void setAvailable(boolean available) { this.isAvailable = available; }
    public boolean isAvailable(){return isAvailable;}

    public ArrayList<Amenity> getAmenities() { return amenities; }
    public void setAmenities(ArrayList<Amenity> amenities) {
        if (amenities == null)
            throw new IllegalArgumentException("Amenities list cannot be null.");
        this.amenities = amenities;
    }


}
