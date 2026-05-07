package GUI;

import back_end_classes.Guest;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

public class ManageWalletController {

    private Guest currentGuest;
    private GuestController parentController; //reference to the main window

    @FXML private Label currentBalanceLabel;
    @FXML private TextField amountField;

    //receives both the session data and the parent controller
    public void setGuestSession(Guest guest, GuestController parent) {
        this.currentGuest = guest;
        this.parentController = parent;
        updateBalanceDisplay();
    }

    private void updateBalanceDisplay() {
        if (currentGuest != null) {
            currentBalanceLabel.setText(String.format("$%,.2f", currentGuest.getBalance()));
        }
    }

    @FXML
    private void handleAddFunds(ActionEvent event) {
        String input = amountField.getText().trim();

        if (input.isEmpty()) {
            RegisterController.showAlert(Alert.AlertType.WARNING, "Missing Data", "Please enter an amount to add.");
            return;
        }

        try {
            double amount = Double.parseDouble(input);

            //call backend method
            currentGuest.addBalance(amount);

            //update the local UI
            updateBalanceDisplay();
            amountField.clear();

            //parent window will update the top navigation bar
            if (parentController != null) {
                parentController.refreshTopBar();
            }

            RegisterController.showAlert(Alert.AlertType.INFORMATION, "Transaction Successful", String.format("Successfully added $%,.2f to your wallet.", amount));

        } catch (NumberFormatException e) {
            RegisterController.showAlert(Alert.AlertType.ERROR, "Invalid Input", "Please enter a valid numeric amount.");
        } catch (IllegalArgumentException e) {
            RegisterController.showAlert(Alert.AlertType.WARNING, "Transaction Failed", e.getMessage());
        } catch (Exception e) {
            RegisterController.showAlert(Alert.AlertType.ERROR, "System Error", "An unexpected error occurred.");
        }
    }
}