package back_end_classes;

import java.time.LocalDate;
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

    // 3. Private Constructor
    private HotelDatabase() {
        guests = new ArrayList<>();
        rooms = new ArrayList<>();
        roomTypes = new ArrayList<>();
        amenities = new ArrayList<>();
        invoices = new ArrayList<>();
        reservations = new ArrayList<>();
        staffMembers = new ArrayList<>();
        loadDummyData();
    }

    //instance getter method
    public static HotelDatabase getInstance() {
        if (instance == null) {
            instance = new HotelDatabase();
        }
        return instance;
    }
    //class methods
    private void loadDummyData() {
        //create room types and add them to the arrayList of roomTypes
        RoomType single = new RoomType( "Single", 100.0, "Cozy single room", 1);
        RoomType doubleRoom = new RoomType( "Double", 150.0, "Spacious double room", 2);
        RoomType suite = new RoomType("Suite", 300.0, "Luxury suite", 4);
        roomTypes.add(single);
        roomTypes.add(doubleRoom);
        roomTypes.add(suite);

        //create amenities and add them to the arrayList of amenities
        Amenity wifi = new Amenity("WIFI", 10, "Hotel");
        Amenity tv = new Amenity("TV", 5, "Room");
        Amenity miniFridge = new Amenity("Mini Fridge", 15, "Room");
        amenities.add(wifi);
        amenities.add(tv);
        amenities.add(miniFridge);


        //create rooms and add them to the arrayList of rooms
        Room room101 = new Room(101, single);
        Room room102 = new Room( 102, single);
        Room room201 = new Room( 201, doubleRoom);

        //calls to add amenities to the rooms just created
        room101.addAmenity(wifi);
        room102.addAmenity(wifi);

        room201.addAmenity(wifi);
        room201.addAmenity(tv);
        room201.addAmenity(miniFridge);

        rooms.add(room101);
        rooms.add(room102);
        rooms.add(room201);


        //create staff members and add them to the arrayList of staffMembers
        Admin masterAdmin = new Admin("admin1", "Admin123" ,LocalDate.of(1990,1, 1), 40);
        Receptionist masterReceptionist = new Receptionist("reception1", "Recp123", LocalDate.of(1995, 1, 1), 35);
        staffMembers.add(masterAdmin);
        staffMembers.add(masterReceptionist);

        //create guests and add them to the arrayList of guests
        Guest guest1 = new Guest("Alice", "pass123", LocalDate.of(1990, 5, 10), Gender.FEMALE, 500.0, "123 Main St");
        Guest guest2 = new Guest("Bob", "pass456", LocalDate.of(1988, 3, 15), Gender.MALE, 200.65, "456 Elm St");
        guests.add(guest1);
        guests.add(guest2);

        //Alice has a PENDING reservation starting next month and ending in 5 days
        Reservation res1 = new Reservation(guest1, room101, LocalDate.now().plusMonths(1).plusDays(1), LocalDate.now().plusMonths(1).plusDays(5));

        //Bob has a CONFIRMED reservation starting next month and ending in 3 days
        Reservation res2 = new Reservation(guest2, room201, LocalDate.now().plusMonths(1), LocalDate.now().plusMonths(1).plusDays(3));
        res2.setStatus(ReservationStatus.CONFIRMED);
        reservations.add(res1);
        reservations.add(res2);
    }


    //add or remove methods to edit the database
    public void addInvoice(Invoice inv) {if (inv != null) invoices.add(inv);}
    public void addReservation(Reservation res) {if (res != null) reservations.add(res);}
    public void addRoom(Room r) { if (r != null) rooms.add(r); }
    public void removeRoom(Room r) { if (r != null) rooms.remove(r); }

    public void addRoomType(RoomType rt) { if (rt != null) roomTypes.add(rt); }
    public void removeRoomType(RoomType rt) { if (rt != null) roomTypes.remove(rt); }

    public void addAmenity(Amenity a) { if (a != null) amenities.add(a); }
    public void removeAmenity(Amenity a) { if (a != null) amenities.remove(a); }

    public void addStaffMember(Staff s) { if (s != null) staffMembers.add(s); }
    public void addGuest(Guest g) { if (g != null) guests.add(g); }


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