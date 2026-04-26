package back_end_classes;

import java.time.LocalDate;
import java.util.ArrayList;

public final class HotelDatabaseSearch {

    private HotelDatabaseSearch() {
    }

    public static Room findRoomByNumber(int number) {
        ArrayList<Room> rooms = HotelDatabase.getInstance().getRooms();

        for (int i = 0; i < rooms.size(); i++) {
            if (rooms.get(i).getRoomNumber() == number) {
                return rooms.get(i);
            }
        }
        return null; // Room not found
    }


    public static User findUserByUsername(String username) {
        ArrayList<Staff> staffMembers = HotelDatabase.getInstance().getStaffMembers();
        ArrayList<Guest> guests = HotelDatabase.getInstance().getGuests();
        for (int i = 0; i < staffMembers.size(); i++) {
            Staff s = staffMembers.get(i);
            if (s.getUsername().equals(username)) {
                return s;
            }
        }

        for (int i = 0; i < guests.size(); i++) {
            Guest g = guests.get(i);
            if (g.getUsername().equals(username)) {
                return g;
            }
        }
        return null; // Guest not found
    }

    public static ArrayList<Room> findAvailableRooms() {
        ArrayList<Room> allRooms = HotelDatabase.getInstance().getRooms();
        ArrayList<Room> availableRooms = new ArrayList<>();

        for (int i = 0; i < allRooms.size(); i++) {
            Room r = allRooms.get(i);
            if (r.isAvailable()) {
                availableRooms.add(r);
            }
        }
        return availableRooms;
    }

    public static RoomType findRoomTypeById(String id) {
        ArrayList<RoomType> types = HotelDatabase.getInstance().getRoomTypes();
        for (int i = 0; i < types.size(); i++) {
            if (types.get(i).getTypeId().equals(id)) {
                return types.get(i);
            }
        }
        return null;
    }

    public static Amenity findAmenityById(String id) {
        ArrayList<Amenity> amenities = HotelDatabase.getInstance().getAmenities();
        for (int i = 0; i < amenities.size(); i++) {
            if (amenities.get(i).getAmenityId().equals(id)) {
                return amenities.get(i);
            }
        }
        return null;
    }

    public static boolean roomNumExists(int n) {
        ArrayList<Room> rooms = HotelDatabase.getInstance().getRooms();
        for (int i = 0; i < rooms.size(); i++) {
            Room currentRoom = rooms.get(i);

            if (currentRoom.getRoomNumber() == n) {
                return true;
            }

        }
        return false;
    }

    public static Reservation findReservationById(int id) {
        ArrayList<Reservation> reservations = HotelDatabase.getInstance().getReservations();
        for (int i = 0; i < reservations.size(); i++) {
            if (reservations.get(i).getReservationId() == id) {
                return reservations.get(i);
            }
        }
        return null;
    }

    public static Invoice findInvoiceById(int id) {
        ArrayList<Invoice> invoices = HotelDatabase.getInstance().getInvoices();
        for (int i = 0; i < invoices.size(); i++) {
            if (invoices.get(i).getInvoiceId() == id) {
                return invoices.get(i);
            }
        }
        return null;
    }

    public static boolean isRoomAvailableOnDates(Room room, LocalDate reqIn, LocalDate reqOut) {
        ArrayList<Reservation> allRes = HotelDatabase.getInstance().getReservations();

        for (int i = 0; i < allRes.size(); i++) {
            Reservation res = allRes.get(i);

            if (res.getRoom().getRoomNumber() == room.getRoomNumber() && res.getStatus() != ReservationStatus.CANCELLED) {

                boolean overlaps = reqIn.isBefore(res.getCheckOutDate()) && reqOut.isAfter(res.getCheckInDate());
                if (overlaps) {
                    return false;
                }
            }
        }
        return true;
    }

    public static ArrayList<Reservation> findReservationsByGuestUsername(String username) {
        ArrayList<Reservation> allRes = HotelDatabase.getInstance().getReservations();
        ArrayList<Reservation> matches = new ArrayList<>();

        for (int i = 0; i < allRes.size(); i++) {
            Reservation res = allRes.get(i);
            // equalsIgnore for giving the user of this method an arraylist of all guests with same letter usernames
            //design choice for allowing the receptionist to find the reservation of a guest through multiple methods.
            if (res.getGuest().getUsername().equalsIgnoreCase(username)) {
                matches.add(res);
            }
        }

        return matches;
    }

}
