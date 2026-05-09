package GUI;

import back_end_classes.*;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public class EditProfileController {

    private Guest currentGuest;
    private GuestController parentController;

    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private DatePicker dobPicker;
    @FXML private ComboBox<Gender> genderComboBox;
    @FXML private TextField addressField;
    @FXML private TextField preferencesField;

    @FXML
    public void initialize() {
        genderComboBox.getItems().addAll(Gender.values());
    }

    public void setGuestSession(Guest guest, GuestController parent) {
        this.currentGuest = guest;
        this.parentController = parent;
        populateFields();
    }

    // Fills the form with the user's current data
    private void populateFields() {
        if (currentGuest != null) {
            usernameField.setText(currentGuest.getUsername());
            passwordField.setText(currentGuest.getPassword());
            dobPicker.setValue(currentGuest.getDateOfBirth());
            genderComboBox.setValue(currentGuest.getGender());
            addressField.setText(currentGuest.getAddress());
            preferencesField.setText(currentGuest.getRoomPreferences());
        }
    }

    @FXML
    private void handleSave(ActionEvent event) {
        try {
            String newUsername = usernameField.getText().trim();

            //check database for duplicate usernames
            User existingUser = HotelDatabaseSearch.findUserByUsername(newUsername);
            if (existingUser != null && existingUser != currentGuest) {
                RegisterController.showAlert(Alert.AlertType.WARNING, "Username Taken", "Username '" + newUsername + "' is already taken.");
                populateFields(); // Reset the form
                return; // Stop saving
            }
            //update all fields using backend from current guest
            currentGuest.setUsername(usernameField.getText().trim());
            currentGuest.setPassword(passwordField.getText().trim());
            currentGuest.setDateOfBirth(dobPicker.getValue());
            currentGuest.setGender(genderComboBox.getValue());
            currentGuest.setAddress(addressField.getText().trim());
            currentGuest.setRoomPreferences(preferencesField.getText().trim());

            //if username changed, tell the main window to update the "Welcome" text
            if (parentController != null) {
                parentController.refreshTopBar();
            }

            DatabaseHelper.updateGuest(currentGuest);
            RegisterController.showAlert(Alert.AlertType.INFORMATION, "Success", "Profile updated successfully!");

        } catch (IllegalArgumentException e) {
            //catches any validation errors (like empty username, spaces in password, future DOB)
            RegisterController.showAlert(Alert.AlertType.WARNING, "Update Failed", e.getMessage());
            //re-populate fields to erase the invalid attempt
            populateFields();
        } catch (Exception e) {
            RegisterController.showAlert(Alert.AlertType.ERROR, "System Error", "An unexpected error occurred.");
        }
    }

    @FXML
    private void handleReset(ActionEvent event) {
        //discard changes by reloading the original data into the text fields
        populateFields();
    }
}