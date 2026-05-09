package back_end_classes;

import java.util.ArrayList;

public class HotelDatabase {

    //private static dataField
    private static HotelDatabase instance;

    //private final arrayLists
    private final ArrayList<Guest> guests;
    private final ArrayList<Room> rooms;
    private final ArrayList<RoomType> roomTypes;
    private final ArrayList<Amenity> amenities;
    private final ArrayList<Invoice> invoices;
    private final ArrayList<Reservation> reservations;
    private final ArrayList<Staff> staffMembers;

    //
    private boolean isLoading = false;
    //Private Constructor
    private HotelDatabase() {
        guests = new ArrayList<>();
        rooms = new ArrayList<>();
        roomTypes = new ArrayList<>();
        amenities = new ArrayList<>();
        invoices = new ArrayList<>();
        reservations = new ArrayList<>();
        staffMembers = new ArrayList<>();
    }

    //instance getter method
    public static HotelDatabase getInstance() {
        if (instance == null) {
            instance = new HotelDatabase();
        }
        return instance;
    }
    //class methods


    public void setLoading(boolean loading) {
        this.isLoading = loading;
    }

    //add or remove methods to edit the database
    public void addInvoice(Invoice inv) {
        if (inv != null) {
            invoices.add(inv);
            if (!isLoading) {
                DatabaseHelper.insertInvoice(inv);
            }
        }
    }
    public void addReservation(Reservation res) {
        if (res != null) {
            reservations.add(res);
            if (!isLoading) {
                DatabaseHelper.insertReservation(res);
            }
        }
    }

    public void addRoom(Room r) {
        if (r != null) {
            rooms.add(r);
            if (!isLoading) {
                DatabaseHelper.insertRoom(r);
            }
        }
    }
    public void removeRoom(Room r) {
        if (r != null) {
            rooms.remove(r);
        DatabaseHelper.deleteRoom(r.getRoomNumber());
    }
    }

    public void addRoomType(RoomType rt) {
        if (rt != null) {
            roomTypes.add(rt);
            if (!isLoading) {
                DatabaseHelper.insertRoomType(rt);
            }
        }
    }
    public void removeRoomType(RoomType rt) {
        if (rt != null) {
            roomTypes.remove(rt);
        DatabaseHelper.deleteRoomType(rt.getTypeId());
        }
    }

    public void addAmenity(Amenity a) {
        if (a != null) {
            amenities.add(a);
            if (!isLoading) {
                DatabaseHelper.insertAmenity(a);
            }
        }
    }
    public void removeAmenity(Amenity a) {
        if (a != null) {
            amenities.remove(a);
            DatabaseHelper.deleteAmenity(a.getAmenityId());
        }
    }

    public void addStaffMember(Staff s) {
        if (s != null) {
            staffMembers.add(s);
            if (!isLoading) {
                DatabaseHelper.insertStaff(s); // Only saves if NOT loading (prevents error during starting the application)
            }
        }
    }
    public void addGuest(Guest g) {
        if (g != null) {
            guests.add(g);
            if (!isLoading) {
                DatabaseHelper.insertGuest(g);
            }
        }
    }


    //getters (gets a copy of database current state)
    /**
     * retrieves a shallow copy of the requested database list.
     * returns a new ArrayList containing references to the original.
     * modifying the arrayLists using the ArrayList library's methods will not affect the database, it affects these shallow copies.
     * it is still allowed to modify the objects themselves in the arrayLists using setters from other classes.
     **/
    public ArrayList<Room> getRooms() { return new ArrayList<>(rooms); }
    public ArrayList<Guest> getGuests() { return new ArrayList<>(guests); }
    public ArrayList<RoomType> getRoomTypes() { return new ArrayList<>(roomTypes); }
    public ArrayList<Amenity> getAmenities() { return new ArrayList<>(amenities); }
    public ArrayList<Invoice> getInvoices() { return new ArrayList<>(invoices); }
    public ArrayList<Reservation> getReservations() { return new ArrayList<>(reservations); }
    public ArrayList<Staff> getStaffMembers() { return new ArrayList<>(staffMembers); }
}