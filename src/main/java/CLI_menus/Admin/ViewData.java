package CLI_menus.Admin;

import back_end_classes.*;
import java.util.ArrayList;

public class ViewData {

    public static void printAllGuests(Staff staff) {
        System.out.println("\n--- Registered Guests ---");
        ArrayList<Guest> guests = staff.viewAllGuests();

        if (guests.isEmpty()) {
            System.out.println("No guests registered yet.");
        } else {
            for (int i = 0; i < guests.size(); i++) {
                Guest g = guests.get(i);
                System.out.println("- Username: " + g.getUsername() + " | Wallet Balance: $" + g.getBalance());
            }
        }
        System.out.println("-------------------------");
    }

    public static void printAllAmenities(Admin admin) {
        System.out.println("\n--- Available Amenities ---");
        ArrayList<Amenity> amenities = admin.viewAllAmenities();

        if (amenities.isEmpty()) {
            System.out.println("No amenities found in database.");
        } else {
            for (int i = 0; i < amenities.size(); i++) {
                Amenity a = amenities.get(i);
                System.out.println("- ID: " + a.getAmenityId() + " | Name: " + a.getName() + " | Price: $" + a.getPricePerDay());
            }
        }
        System.out.println("-------------------------");
    }

    public static void printAllReservations(Staff staff) {
        System.out.println("\n--- System Reservations ---");
        ArrayList<Reservation> reservations = staff.viewAllReservations();

        if (reservations.isEmpty()) {
            System.out.println("No reservations found in database.");
        } else {
            for (int i = 0; i < reservations.size(); i++) {
                Reservation r = reservations.get(i);
                System.out.println("- Res ID: " + r.getReservationId() + " | Room: " + r.getRoom().getRoomNumber() +
                        " | Guest: " + r.getGuest().getUsername() + " | Status: " + r.getStatus());
            }
        }
        System.out.println("-------------------------");
    }

    public static void printAllRooms(Staff staff) {
        System.out.println("\n--- Hotel Rooms ---");
        ArrayList<Room> rooms = staff.viewAllRooms();

        if (rooms.isEmpty()) {
            System.out.println("No rooms found in database.");
        } else {
            for (int i = 0; i < rooms.size(); i++) {
                Room r = rooms.get(i);
                System.out.println("- Room " + r.getRoomNumber() + " | Type: " + r.getRoomType().getName() +
                        " | Available: " + r.isAvailable());
            }
        }
        System.out.println("-------------------------");
    }

    public static void printAllRoomTypes(Admin admin) {
        System.out.println("\nAvailable Room Types:");
        ArrayList<RoomType> roomTypes = admin.viewAllRoomTypes();

        if (roomTypes.isEmpty()) {
            System.out.println("No roomTypes found in database.");
        } else {
            for (int i = 0; i < roomTypes.size(); i++) {
                RoomType rt = roomTypes.get(i);
                System.out.println("- ID: " + rt.getTypeId() + " | Name: " + rt.getName());
            }
        }
        System.out.println("-------------------------");
    }
}