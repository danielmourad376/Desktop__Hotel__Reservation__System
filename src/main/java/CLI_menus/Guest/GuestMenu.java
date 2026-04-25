package CLI_menus.Guest;

import back_end_classes.*;
import java.util.Scanner;
import java.time.LocalDate;

public class GuestMenu {
    public static void displayGuest(Guest guest, Scanner input){
        boolean loggedIn = true;
        while(loggedIn){
            System.out.println("\n=== GUEST MENU ===");
            System.out.println("Welcome, " + guest.getUsername() + " | Balance: $" + guest.getBalance());
            System.out.println("1. View Available Rooms");
            System.out.println("2. Make a Reservation");
            System.out.println("3. View My Reservations");
            System.out.println("4. Cancel a Reservation");
            System.out.println("5. Checkout and Pay (Online)");
            System.out.println("6. Manage Wallet (Add Funds)");
            System.out.println("7. View / Edit Profile");
            System.out.println("8. Logout");
            System.out.print("Select an option: ");

            String choice = input.nextLine();
            try {
                switch (choice) {
                    case "1":
                        guest.viewAvailableRooms();
                        break;

                    case "2":
                        System.out.println("Enter room number: ");
                        int roomNum = Integer.parseInt(input.nextLine());
                        LocalDate checkIn = null;
                        LocalDate checkOut = null;
                        while (checkIn == null) {
                            System.out.print("Enter Check-in Date (YYYY-MM-DD): ");
                            try {
                                checkIn = LocalDate.parse(input.nextLine());
                            } catch (Exception e) {
                                System.out.println("Invalid format. Please use YYYY-MM-DD.");
                            }
                        }
                        while (checkOut == null) {
                            System.out.print("Enter Check-out Date (YYYY-MM-DD): ");
                            try {
                                checkOut = LocalDate.parse(input.nextLine());
                            } catch (Exception e) {
                                System.out.println("Invalid format. Please use YYYY-MM-DD.");
                            }
                        }

                        guest.reserveRoom(roomNum, checkIn, checkOut);
                        System.out.println("Reservation successfully created!");
                        break;

                    case "3":
                        guest.viewMyReservations();
                        break;

                    case "4":
                        guest.viewMyReservations();
                        System.out.print("Enter Reservation ID to cancel: ");
                        int cancelId = Integer.parseInt(input.nextLine());
                        guest.cancelReservation(cancelId);
                        System.out.println("Reservation cancelled.");
                        break;

                    case "5":
                        guest.viewMyReservations();
                        System.out.print("Enter Reservation ID to checkout: ");
                        int resId = Integer.parseInt(input.nextLine());

                        System.out.print("Select your payment method: ");
                        System.out.println("1. Credit Card");
                        System.out.println("2. ONLINE");
                        System.out.print("Choice: ");

                        String payment = input.nextLine();
                        PaymentMethod method;
                        if (payment.equals("1")) {
                            method = PaymentMethod.CREDIT_CARD;
                        } else if (payment.equals("2")) {
                            method = PaymentMethod.ONLINE;
                        } else {
                            throw new IllegalArgumentException("Invalid payment method.");
                        }

                        guest.checkoutAndPay(resId, method);
                        break;

                    case "6":
                        System.out.println("Enter amount to add to your ONLINE wallet: ");
                        double amount = Double.parseDouble(input.nextLine());
                        guest.addBalance(amount);
                        break;

                    case "7":
                        System.out.println("\n--- MY PROFILE ---");
                        System.out.println("Username: " + guest.getUsername());
                        System.out.println("Gender: " + guest.getGender());
                        System.out.println("Address: " + guest.getAddress());
                        System.out.println("Current Room Preferences: " + guest.getRoomPreferences());
                        System.out.print("Would you like to update your profile? (y/n)? ");
                        if (input.nextLine().trim().equalsIgnoreCase("y")) {
                            System.out.println("Select an attribute to update:");
                            System.out.println("1. update Username");
                            System.out.println("2. update Address");
                            System.out.println("3. update Current Room Preferences");
                            System.out.println("Choice: ");
                            String update = input.nextLine();
                            switch (update) {
                                case "1":

                                System.out.print("Enter a username: ");
                                guest.setUsername(input.nextLine());
                                System.out.println("Username updated successfully!");
                                break;

                                case "2":
                                System.out.println("enter a address: ");
                                guest.setAddress(input.nextLine());
                                System.out.println("Address updated successfully!");
                                break;

                                case "3":
                                    System.out.println("enter your room preferences: ");
                                    guest.setRoomPreferences(input.nextLine());
                                    System.out.println("Room preferences updated successfully!");
                                    break;
                                default:
                                    System.out.println("Invalid choice. Profile update cancelled.");
                            }
                        }
                        break;
                    case "8":
                        loggedIn = false;
                        System.out.println("Logging out...");
                        break;
                    default:
                        System.out.println("Invalid choice. Please try again.");
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
