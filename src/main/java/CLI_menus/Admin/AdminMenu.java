package CLI_menus.Admin;

import back_end_classes.*;
import java.util.Scanner;

public class AdminMenu {

    public static void displayAdmin(Admin admin, Scanner input){
        boolean loggedIn = true;

        while(loggedIn){
            System.out.println("======ADMIN MENU======");
            System.out.println("Welcome " + admin.getUsername());
            System.out.println("1. view Database");
            System.out.println("2. Manage Rooms");
            System.out.println("3. Logout");
            System.out.print("Select a category: ");

            String choice = input.nextLine();

            try {
                switch (choice) {
                    case "1":
                        AdminViewMenu.viewDatabase(admin, input);
                        break;
                    case "2":
                        AdminRoomMenu.editRooms(admin, input);
                        break;
                    case "3":
                        loggedIn = false;
                        System.out.println("Logging out...");
                        break;
                    default:
                        System.out.println("Invalid choice. Please try again");
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
