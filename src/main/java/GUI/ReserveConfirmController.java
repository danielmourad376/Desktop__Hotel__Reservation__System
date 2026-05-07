package GUI;

import back_end_classes.Guest;
import back_end_classes.Room;
import exception.RoomNotAvailableException;
import exception.RoomNotFoundException;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.stage.Stage;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

import static GUI.RegisterController.showAlert;

public class ReserveConfirmController {

    private Room selectedRoom;
    private Guest currentGuest;

    @FXML private Label roomInfoLabel;
    @FXML private DatePicker checkInDate;
    @FXML private DatePicker checkOutDate;
    @FXML private Label totalCostLabel;

    // Receives the selected room from AvailableRoomsController
    public void setRoom(Room room) {
        this.selectedRoom = room;
        roomInfoLabel.setText(String.format("Room #%d — %s — $%.2f/night",
                room.getRoomNumber(),
                room.getRoomType().getName(),
                room.calculatePrice()));
    }

    // Receives the active user session
    public void setGuestSession(Guest guest) {
        this.currentGuest = guest;
    }

    @FXML
    public void initialize() {
        // Add listeners to the DatePickers to calculate the price in real-time
        checkInDate.valueProperty().addListener((obs, oldVal, newVal) -> updateTotalCost());
        checkOutDate.valueProperty().addListener((obs, oldVal, newVal) -> updateTotalCost());
    }

    private void updateTotalCost() {
        LocalDate in = checkInDate.getValue();
        LocalDate out = checkOutDate.getValue();

        // Only calculate if both dates are selected and valid chronologically
        if (in != null && out != null && !in.isAfter(out)) {
            long nights = ChronoUnit.DAYS.between(in, out);
            if (nights == 0) nights = 1; // Minimum 1-night charge per backend logic

            double total = nights * selectedRoom.calculatePrice();
            totalCostLabel.setText(String.format("Total: $%.2f", total));
        } else {
            totalCostLabel.setText("Total: $0.00");
        }
    }

    @FXML
    private void handleConfirm(ActionEvent event) {
        LocalDate in = checkInDate.getValue();
        LocalDate out = checkOutDate.getValue();

        //Frontend Validation
        if (in == null || out == null) {
            showAlert(Alert.AlertType.WARNING, "Missing Dates", "Please select both check-in and check-out dates.");
            return;
        }
        if (in.isBefore(LocalDate.now())) {
            showAlert(Alert.AlertType.WARNING, "Invalid Dates", "Check-in date cannot be in the past.");
            return;
        }
        if (in.isAfter(out)) {
            showAlert(Alert.AlertType.WARNING, "Invalid Dates", "Check-out date cannot be before the check-in date.");
            return;
        }

        //Backend Execution
        try {
            // Attempt to reserve through the guest class
            currentGuest.reserveRoom(selectedRoom.getRoomNumber(), in, out);

            showAlert(Alert.AlertType.INFORMATION, "Success", "Reservation confirmed for Room " + selectedRoom.getRoomNumber() + "!");
            closeWindow(event);

        } catch (RoomNotAvailableException | RoomNotFoundException | IllegalArgumentException e) {
            // Catch custom exceptions and display them to the user
            showAlert(Alert.AlertType.ERROR, "Reservation Failed", e.getMessage());
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "System Error", "An unexpected error occurred: " + e.getMessage());
        }
    }

    @FXML
    private void handleCancel(ActionEvent event) {
        closeWindow(event);
    }

    // Helper to close the popup stage
    private void closeWindow(ActionEvent event) {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.close();
    }


}