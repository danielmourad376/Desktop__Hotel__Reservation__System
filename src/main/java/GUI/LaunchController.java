package GUI;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;

public class LaunchController{

    @FXML
    private void handleLoginClick(ActionEvent event) {
        SceneSwitcher.goTo(event, "Login.fxml");
    }

    @FXML
    private void handleRegisterClick(ActionEvent event) {
        SceneSwitcher.goTo(event, "Register.fxml");
    }


}