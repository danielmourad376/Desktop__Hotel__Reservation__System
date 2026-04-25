package CLI_menus.Admin;
import back_end_classes.*;
import java.util.Scanner;

public class AdminViewMenu {

    public static void viewDatabase(Admin admin, Scanner cin) {
        boolean back = false;

        while (!back) {
            System.out.println("\n=== DATABASE REPORTS (READ-ONLY) ===");
            System.out.println("1. View All Guests");
            System.out.println("2. View All Amenities");
            System.out.println("3. View All Reservations");
            System.out.println("4. View All Rooms");
            System.out.println("5. Go Back to Admin Dashboard");
            System.out.print("Select a report to view: ");

            String choice = cin.nextLine();

            switch (choice) {
                case "1":
                    ViewData.printAllGuests(admin);
                    break;
                case "2":
                    ViewData.printAllAmenities(admin);
                    break;
                case "3":
                    ViewData.printAllReservations(admin);
                    break;
                case "4":
                    ViewData.printAllRooms(admin);
                    break;
                case "5":
                    back = true;
                    break;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }
    }
}
