package GUI;

import back_end_classes.Gender;
import back_end_classes.Guest;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

import java.time.LocalDate;

public class RegisterController {

    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private DatePicker dobPicker;
    @FXML private ComboBox<Gender> genderComboBox;
    @FXML private TextField balanceField;
    @FXML private TextField addressField;

    @FXML
    public void initialize() {
        // populate the gender combo box with gender enum
        genderComboBox.getItems().addAll(Gender.values());
    }
    @FXML
    public void registerGuest(ActionEvent event) {
        try {
            String uname = usernameField.getText().trim();
            String pass = passwordField.getText().trim();
            LocalDate dob = dobPicker.getValue();
            Gender selectedGender = genderComboBox.getValue();
            String address = addressField.getText().trim();
            String balanceText = balanceField.getText().trim();

            /// if the String value of balance (balanceText) is empty, return null,
            ///  otherwise convert to Double wrapper since double cannot accept null data
            Double balance = balanceText.isEmpty() ? null : Double.valueOf(balanceText);

            Guest newGuest = new Guest(uname, pass, dob, selectedGender, balance, address);
            newGuest.register();


            FXMLLoader loader = new FXMLLoader(getClass().getResource("GuestMenu.fxml"));
            Parent root = loader.load();

            GuestController controller = loader.getController();
            controller.setSession(newGuest);

            Scene currentScene = ((Node) event.getSource()).getScene();
            currentScene.setRoot(root);

        } catch (NumberFormatException e) {
            //catches wrong data type for balance
            showAlert(Alert.AlertType.ERROR, "Invalid Input", "Please enter a valid numeric amount for your starting balance.");
        } catch (IllegalArgumentException e) {
            //catches exceptions thrown by backend
            showAlert(Alert.AlertType.WARNING, "Validation Error", e.getMessage());
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "System Error", "An unexpected error occurred: " + e.getMessage());
        }
    }
    @FXML
    private void cancelReg(ActionEvent event) {
        SceneSwitcher.goTo(event, "Launch.fxml");
    }
    public static void showAlert(Alert.AlertType type, String title, String content) {
        //creates popup in the gui
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
