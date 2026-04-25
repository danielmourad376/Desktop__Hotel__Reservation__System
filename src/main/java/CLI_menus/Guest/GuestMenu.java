package CLI_menus.Guest;

import back_end_classes.*;
import java.util.Scanner;
import java.util.ArrayList;
import java.time.LocalDate;

public class GuestMenu {
    public static void displayGuest(Guest guest, Scanner input){
        boolean loggedIn = true;
        while(loggedIn){
            System.out.println("\n=== GUEST MENU ===");
            System.out.println("Welcome, " + guest.getUsername() + "!");
            System.out.println("1. View Available Rooms");
            System.out.println("2. Make a Reservation");
            System.out.println("3. View My Reservations");
            System.out.println("4. Cancel a Reservation");
            System.out.println("5. View Profile & Wallet");
            System.out.println("6. Logout");
            System.out.print("Select an option: ");

            String choice = input.nextLine();
            try {
                switch (choice) {
                    case "1":

                        break;

                    case "2":

                        break;

                    case "3":

                        break;

                    case "4":

                        break;

                    case "5":

                        break;
                    case "6":
                        loggedIn = false;
                        System.out.println("Logging out...");
                        break;

                }
            }catch (NumberFormatException e){
                System.out.println("INPUT ERROR: Please enter a valid number.");
            }
            catch (Exception e) {
                System.out.println("ACTION FAILED: " + e.getMessage());
            }
        }
    }
}
