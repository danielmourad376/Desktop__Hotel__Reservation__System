package CLI_menus.Receptionist;
import back_end_classes.*;
import CLI_menus.Admin.ViewData;

import java.util.Scanner;

public class ReceptionistMenu {
    public static void displayReceptionist(Receptionist receptionist, Scanner input){
        boolean loggedIn = true;
        while (loggedIn){
            System.out.println("\n=== RECEPTIONIST MENU ===");
            System.out.println("Welcome, " + receptionist.getUsername() + "!");
            System.out.println("1. View All Reservations");
            System.out.println("2. View All Rooms");
            System.out.println("3. View All Guests");
            System.out.println("4. Check-In Guest");
            System.out.println("5. Check-Out Guest");
            System.out.println("6. Confirm a Pending Reservation");
            System.out.println("7. Logout");
            System.out.print("Select an option: ");

            String choice = input.nextLine();
            try {
                switch (choice) {
                    case "1":
                        ViewData.printAllReservations(receptionist);
                    break;

                    case "2":
                        ViewData.printAllRooms(receptionist);
                    break;

                    case "3":
                        ViewData.printAllGuests(receptionist);
                    break;

                    case "4":
                        ViewData.printAllReservations(receptionist);

                        System.out.println("please enter the guest's reservation ID: ");
                        int resId = Integer.parseInt(input.nextLine());

                        Reservation res = HotelDatabaseSearch.findReservationById(resId);
                        if (res == null) {
                            throw new IllegalArgumentException("Reservation #" + resId + " not found.");
                        }

                        receptionist.checkIn(resId);
                        System.out.println("Guest " + res.getGuest().getUsername() + " checked in successfully.");
                    break;

                    case "5":
                        ViewData.printAllReservations(receptionist);

                        System.out.println("please enter the guest's reservation ID: ");
                        int finalRes = Integer.parseInt(input.nextLine());

                        Reservation r = HotelDatabaseSearch.findReservationById(finalRes);
                        if (r == null) {
                            throw new IllegalArgumentException("Reservation #" + finalRes + " not found.");
                        }

                        System.out.println("Select Payment Method:");
                        System.out.println("1. Cash");
                        System.out.println("2. Credit Card");
                        System.out.println("3. Online");
                        System.out.print("Choice: ");

                        String paymentChoice = input.nextLine();
                        PaymentMethod selectedMethod;

                        switch (paymentChoice) {
                            case "1":
                                selectedMethod = PaymentMethod.CASH;
                                break;
                            case "2":
                                selectedMethod = PaymentMethod.CREDIT_CARD;
                                break;
                            case "3":
                                selectedMethod = PaymentMethod.ONLINE;
                                break;
                            default:
                                throw new IllegalArgumentException("Invalid payment method selected.");
                        }

                        receptionist.checkOut(finalRes, selectedMethod );
                        System.out.println("Guest " + r.getGuest().getUsername() + " checked out successfully.");

                    break;

                    case "6":
                        ViewData.printAllReservations(receptionist);

                        System.out.println("please enter the pending reservation ID to confirm: ");
                        int pendingReservation = Integer.parseInt(input.nextLine());
                        Reservation pendingRes = HotelDatabaseSearch.findReservationById(pendingReservation);

                        if (pendingRes == null) {
                            throw new IllegalArgumentException("Reservation #" + pendingReservation + " not found.");
                        }

                        pendingRes.confirmReservation();
                        break;

                    case "7":
                        loggedIn = false;
                        System.out.println("Logging out...");
                    break;
                    default:
                        System.out.println("Invalid option. Please try again.");
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
