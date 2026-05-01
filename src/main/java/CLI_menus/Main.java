package CLI_menus;

import CLI_menus.Admin.AdminMenu;
import CLI_menus.Guest.GuestMenu;
import CLI_menus.Receptionist.ReceptionistMenu;
import back_end_classes.*;

import java.util.Scanner;
import java.time.LocalDate;

public class Main {
    public static void main(String[] args) {
        HotelDatabase.getInstance(); //load the dummy Data

        Scanner input = new Scanner(System.in);
        boolean programRunning = true;

        System.out.println("----------------------------------------");
        System.out.println(" Welcome to the Hotel Management System ");
        System.out.println("----------------------------------------");

        while (programRunning) {
            System.out.println("\nPlease select an option:");
            System.out.println("1. Login");
            System.out.println("2. Register (new Guest)");
            System.out.println("3. Terminate program");
            System.out.println("Select an option: ");

            String choice = input.nextLine();
            /// nextLine() is used instead of nextInt() here to prevent a leftover newline
            ///Scanner bug, and to prevent the program from crashing if the user types a letter.

            switch (choice) {
                case "1":
                    System.out.print("Username: ");
                    String uname = input.nextLine();
                    System.out.print("Password: ");
                    String pass = input.nextLine();

                    User loggedInUser = User.login(uname, pass);

                    if (loggedInUser == null) {
                        System.out.println("Invalid Username or Password. Please try again.");
                    } else {
                        System.out.println("\n Login successful. Welcome, " + loggedInUser.getUsername() + "!");

                        /*if statements to send the user to their respective menu
                         based on their User type(Admin, Receptionist, Guest)
                         by checking if the loggedInUser object is an object of the respective user classes*/
                        if (loggedInUser instanceof Admin) {
                            AdminMenu.displayAdmin((Admin) loggedInUser, input);
                        } else if (loggedInUser instanceof Receptionist) {
                            ReceptionistMenu.displayReceptionist((Receptionist) loggedInUser, input);
                        } else if (loggedInUser instanceof Guest) {
                            GuestMenu.displayGuest((Guest) loggedInUser, input);
                        }
                    }
                    break;
                case "2":
                    try {
                        System.out.println("\n--- GUEST REGISTRATION ---");
                        System.out.print("Enter Username: ");
                        String newUname = input.nextLine();
                        System.out.print("Enter Password: ");
                        String newPass = input.nextLine();
                        System.out.print("Enter Date of Birth (YYYY-MM-DD): ");
                        LocalDate dob = LocalDate.parse(input.nextLine());
                        System.out.print("Enter Gender (MALE/FEMALE): ");
                        Gender gender = Gender.valueOf(input.nextLine().toUpperCase());
                        System.out.print("Enter Starting Wallet Balance: $");
                        double balance = Double.parseDouble(input.nextLine());
                        System.out.print("Enter Home Address: ");
                        String address = input.nextLine();

                        // Try to create and register the guest
                        Guest newGuest = new Guest(newUname, newPass, dob, gender, balance, address);
                        newGuest.register();

                    } catch (Exception e) {
                        // Catches bad dates, negative balances, empty strings, and bad Enum spelling
                        System.out.println("REGISTRATION FAILED: " + e.getMessage());
                    }
                    break;
                case "3":
                    programRunning = false;
                    System.out.println("Thank you for using the Hotel Management System. Goodbye!");
                    break;
                default:
                    System.out.println("Invalid choice. Please enter 1, 2, or 3.");
            }
        }

    }
}