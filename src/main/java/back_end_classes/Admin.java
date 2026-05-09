package back_end_classes;

import java.time.LocalDate;
import java.util.ArrayList;

public class Admin extends Staff{

    //constructor
    public Admin(String username, String password, LocalDate hireDate, int hours ){
        super(username, password, hireDate, Role.ADMIN, hours);
    }

    @Override
    public void performDuties() {} //to be used in milestone 2

    //adding more admins/receptionist to database
    public void hireEmployee(String uname, String pass, LocalDate dob, Role jobTitle, int hours){
        if (HotelDatabaseSearch.findUserByUsername(uname) != null) {
            throw new IllegalArgumentException("Username '" + uname + "' is already taken.");
        }
            Staff newHire;
            switch (jobTitle) {
                case ADMIN:
                    newHire = new Admin(uname, pass, dob, hours);
                    break;
                case RECEPTIONIST:
                    newHire = new Receptionist(uname, pass, dob, hours);
                    break;
                default:
                    throw new IllegalArgumentException("Invalid role.");
            }
            HotelDatabase.getInstance().addStaffMember(newHire);
            System.out.println(jobTitle + " " + uname + " was hired.");
    }

    //crud
    //add methods
    public void addRoom(int num, RoomType roomType){
        if (HotelDatabaseSearch.roomNumExists(num)) {
            throw new IllegalArgumentException("Room number " + num + " already exists.");
        }
            Room newRoom = new Room(num, roomType);
            HotelDatabase.getInstance().addRoom(newRoom);
            System.out.println("Room no: " + newRoom.getRoomNumber() + " added to database.");
    }
    public void addRoomType(String name, double price, String desc, int occupancy){
            RoomType newType = new RoomType(name, price, desc, occupancy);
            HotelDatabase.getInstance().addRoomType(newType);
            System.out.println("Added new Room Type: " + newType.getName() + " to database.");
    }
    public void addAmenity(String name, double price, String type){
            Amenity newAmenity = new Amenity(name, price, type);
            HotelDatabase.getInstance().addAmenity(newAmenity);
            System.out.println("Added new Amenity: " + newAmenity.getName() + " to database.");
    }

    //console method; won't be used for GUI
    public void addAmenityToRoom(int targetRoomNumber, String targetAmenityId) {
        Room addToRoom = HotelDatabaseSearch.findRoomByNumber(targetRoomNumber);
        if (addToRoom == null) {
            throw new IllegalArgumentException("Room " + targetRoomNumber + " not found.");
        }

        Amenity a = HotelDatabaseSearch.findAmenityById(targetAmenityId); //find requested amenity by its id and set it to a refVariable named a
        if (a == null) {
            throw new IllegalArgumentException("Amenity " + targetAmenityId + " does not exist.");
        }

        addToRoom.addAmenity(a);
        System.out.println("Successfully added " + a.getName() + " to Room " + targetRoomNumber);
    }

    //update methods

    public void updateRoom(int targetRoomNumber, RoomType newRoomType, ArrayList<Amenity> newAmenities, boolean newIsAvailable) {
        Room roomToUpdate = HotelDatabaseSearch.findRoomByNumber(targetRoomNumber);
        if (roomToUpdate == null) {
            throw new IllegalArgumentException("Update Failed: Room " + targetRoomNumber + " not found.");
        }
        /*most null checks are now inside the setters of other classes
        (assume this for all CRUD methods of this admin class)*/

        roomToUpdate.setRoomType(newRoomType);
        roomToUpdate.setAmenities(newAmenities);
        roomToUpdate.setAvailable(newIsAvailable);
        DatabaseHelper.updateRoom(roomToUpdate);
        System.out.println("Room no: " + targetRoomNumber + " fully updated.");
    }
    public void updateRoomType(String typeId, String name, double basePrice, String description, int maxOccupancy){
        RoomType typeToUpdate = HotelDatabaseSearch.findRoomTypeById(typeId);

        if (typeToUpdate == null) {
            throw new IllegalArgumentException("Update Failed: Room Type " + typeId + " not found.");
        }

            typeToUpdate.setName(name);
            typeToUpdate.setBasePrice(basePrice);
            typeToUpdate.setDescription(description);
            typeToUpdate.setMaxOccupancy(maxOccupancy);
            DatabaseHelper.updateRoomType(typeToUpdate);
            System.out.println("Room Type " + typeId + " fully updated.");
    }
    public void updateAmenity(String targetId, String name, double price, String type){
        Amenity a = HotelDatabaseSearch.findAmenityById(targetId);

        if (a == null) {
            throw new IllegalArgumentException("Update Failed: Amenity not found.");
        }

            a.setName(name);
            a.setPricePerDay(price);
            a.setType(type);
            DatabaseHelper.updateAmenity(a);
            System.out.println("Amenity " + targetId + " fully updated.");
    }

    //delete methods
    public void deleteRoom(int roomNumber) {
        Room r = HotelDatabaseSearch.findRoomByNumber(roomNumber);

        if (r == null) {
            throw new IllegalArgumentException("Delete Failed: Room " + roomNumber + " not found.");
        }

            HotelDatabase.getInstance().removeRoom(r);
            System.out.println("Room " + roomNumber + " deleted from database.");
    }

    public void deleteRoomType(String typeId) {
        RoomType rt = HotelDatabaseSearch.findRoomTypeById(typeId);

        if (rt == null) {
            throw new IllegalArgumentException("Delete Failed: Room Type " + typeId + " not found.");
        }

            HotelDatabase.getInstance().removeRoomType(rt);
            System.out.println("Room Type " + typeId + " deleted from database.");
    }

    public void deleteAmenity(String amenityId) {
        Amenity a = HotelDatabaseSearch.findAmenityById(amenityId);

        if (a == null) {
            throw new IllegalArgumentException("Delete Failed: Amenity " + amenityId + " not found.");
        }

            HotelDatabase.getInstance().removeAmenity(a);
            System.out.println("Amenity " + amenityId + " deleted from database.");

    }

}
