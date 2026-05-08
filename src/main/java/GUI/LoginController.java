package GUI;

import back_end_classes.*;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public class LoginController {

    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;

    @FXML
    public void handleLoginButton(ActionEvent event) {
        String username = usernameField.getText().trim();
        String password = passwordField.getText();

        if (username.isEmpty() || password.isEmpty()) {
            RegisterController.showAlert(Alert.AlertType.WARNING, "Missing Data", "Please enter both your username and password.");
            return;
        }

        try {
            User foundUser = HotelDatabaseSearch.findUserByUsername(username);

            if (foundUser == null || !foundUser.getPassword().equals(password)) {
                RegisterController.showAlert(Alert.AlertType.ERROR, "Login Failed", "Incorrect username or password.");
                return;
            }

            FXMLLoader loader;
            Parent root;

            if (foundUser instanceof Guest) {
                loader = new FXMLLoader(getClass().getResource("GuestMenu.fxml"));
                root = loader.load();
                GuestController controller = loader.getController();
                controller.setSession((Guest) foundUser);

            } else if (foundUser instanceof Admin) {
                loader = new FXMLLoader(getClass().getResource("AdminMenu.fxml"));
                root = loader.load();
                AdminController controller = loader.getController();
                controller.setSession((Admin) foundUser);
            } else if (foundUser instanceof Receptionist) {
                loader = new FXMLLoader(getClass().getResource("ReceptionistMenu.fxml"));
                root = loader.load();

                ReceptionistController controller = loader.getController();
                controller.setSession((Receptionist) foundUser);
            } else {
                RegisterController.showAlert(Alert.AlertType.ERROR, "System Error", "Unknown user type.");
                return;
            }

            Scene currentScene = ((Node) event.getSource()).getScene();
            currentScene.setRoot(root);

        } catch (Exception e) {
            RegisterController.showAlert(Alert.AlertType.ERROR, "System Error", "An unexpected error occurred during login.");
        }
    }

    @FXML
    private void handleCancel(ActionEvent event) {
        SceneSwitcher.goTo(event, "Launch.fxml");
    }
}