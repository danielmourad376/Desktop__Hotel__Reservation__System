package CLI_menus.Admin;

import back_end_classes.*;

import java.util.ArrayList;
import java.util.Scanner;

public class AdminRoomMenu {
    public static void editRooms(Admin admin, Scanner cin) {
        boolean back = false;

        while (!back) {
            System.out.println("\n--- MANAGE ROOMS ---");
            System.out.println("1. Add a New Room");
            System.out.println("2. Add an Amenity to a Room");
            System.out.println("3. Update a Room");
            System.out.println("4. Remove a Room");
            System.out.println("5. Go Back to Admin Dashboard");
            System.out.print("Select an option: ");

            String choice = cin.nextLine();

            try {
                switch (choice) {
                    case "1":
                        adminAddRoom(admin, cin);
                        break;
                    case "2":
                        adminAddAmenityToRoom(admin, cin);
                        break;
                    case "3":
                        adminUpdateRoom(admin, cin);
                        break;
                    case "4":
                        adminRemoveRoom(admin, cin);
                        break;
                    case "5":
                        back = true;
                        break;
                    default:
                        System.out.println("Invalid choice. Please try again.");
                }
            } catch (NumberFormatException e) {
                System.out.println("INPUT ERROR: Please enter a valid number.");
            } catch (Exception e) {
                System.out.println("An unexpected error occurred: " + e.getMessage());
            }
        }
    }

    private static void adminAddRoom(Admin admin, Scanner cin) {
        ViewData.printAllRooms(admin);

        System.out.print("Enter New Room Number: ");
        int roomNumber = Integer.parseInt(cin.nextLine());

        ViewData.printAllRoomTypes(admin);
        System.out.print("Enter Room Type ID from the list above: ");
        String typeId = cin.nextLine();

        try {
            RoomType selectedType = HotelDatabaseSearch.findRoomTypeById(typeId);

            if (selectedType == null) {
                System.out.println(" Error: Room Type '" + typeId + "' not found.");
            } else {
                admin.addRoom(roomNumber, selectedType);
                System.out.println(" Room " + roomNumber + " added successfully.");
            }

        } catch (IllegalArgumentException e) {
            System.out.println(" Error adding room: " + e.getMessage());
        }
    }

    private static void adminAddAmenityToRoom(Admin admin, Scanner cin) {
        System.out.print("Enter Room Number: ");
        int roomNumber = Integer.parseInt(cin.nextLine());

        ViewData.printAllAmenities(admin);

        System.out.print("Enter Amenity ID to add: ");
        String amenityId = cin.nextLine();

        try {
            admin.addAmenityToRoom(roomNumber, amenityId);
            System.out.println("Amenity added to room " + roomNumber + " successfully.");
        } catch (IllegalArgumentException e) {
            System.out.println("Error adding amenity: " + e.getMessage());
        }
    }

    private static void adminRemoveRoom(Admin admin, Scanner cin) {
        ViewData.printAllRooms(admin);

        System.out.print("Enter Room Number to Remove: ");
        int roomNumber = Integer.parseInt(cin.nextLine());

        try {
            admin.deleteRoom(roomNumber);
            System.out.println("Room " + roomNumber + " removed successfully.");
        } catch (IllegalArgumentException e) {
            System.out.println("Error removing room: " + e.getMessage());
        }
    }

    private static void adminUpdateRoom(Admin admin, Scanner cin) {
        ViewData.printAllRooms(admin);

        System.out.print("Enter Room Number to Update: ");
        int targetRoomNumber = Integer.parseInt(cin.nextLine());

        Room roomToUpdate = HotelDatabaseSearch.findRoomByNumber(targetRoomNumber);
        if (roomToUpdate == null) {
            System.out.println("Error: Room " + targetRoomNumber + " not found.");
            return;
        }

        System.out.println("\n--- 1. Update Room Type ---");
        System.out.println("Current Type: " + roomToUpdate.getRoomType().getName());

        ViewData.printAllRoomTypes(admin);

        System.out.print("Enter NEW Room Type ID (or press Enter to keep current): ");
        String typeId = cin.nextLine();

        RoomType newRoomType = roomToUpdate.getRoomType(); // Default to current
        if (!typeId.trim().isEmpty()) {
            RoomType foundType = HotelDatabaseSearch.findRoomTypeById(typeId);
            if (foundType == null) {
                System.out.println("Error: Room Type '" + typeId + "' not found. Update cancelled.");
                return;
            }
            newRoomType = foundType;
        }

        System.out.println("\n--- 2. Update Amenities ---");
        ArrayList<Amenity> newAmenities = new ArrayList<>(roomToUpdate.getAmenities());

        System.out.print("Current amenities: ");
        if (newAmenities.isEmpty()) {
            System.out.println("None");
        } else {
            for (int i = 0; i < newAmenities.size(); i++) {
                System.out.print(newAmenities.get(i).getName() + " ");
            }
            System.out.println();
        }

        System.out.print("Do you want to completely replace this amenity list? (y/n): ");
        if (cin.nextLine().trim().equalsIgnoreCase("y")) {
            newAmenities.clear();

            ViewData.printAllAmenities(admin);

            System.out.println("Type an Amenity ID to add it, or type 'done' to finish.");
            while (true) {
                System.out.print("Add Amenity ID (or 'done'): ");
                String input = cin.nextLine().trim();

                if (input.equalsIgnoreCase("done")) {
                    break;
                }

                Amenity foundAmenity = HotelDatabaseSearch.findAmenityById(input);

                if (foundAmenity != null) {
                    newAmenities.add(foundAmenity);
                    System.out.println("Added: " + foundAmenity.getName());
                } else {
                    System.out.println("Invalid ID.");
                }
            }
        }

        System.out.println("\n--- 3. Update Availability ---");
        boolean newIsAvailable = roomToUpdate.isAvailable();
        System.out.print("Current Availability: " + newIsAvailable + ". Enter 'true' or 'false' (or press Enter to keep current): ");
        String availInput = cin.nextLine().trim();
        if (!availInput.isEmpty()) {
            newIsAvailable = Boolean.parseBoolean(availInput);
        }

        try {
            admin.updateRoom(targetRoomNumber, newRoomType, newAmenities, newIsAvailable);
            System.out.println("\nRoom " + targetRoomNumber + " successfully updated!");
        } catch (IllegalArgumentException e) {
            System.out.println("\nError updating room: " + e.getMessage());
        }
    }
}