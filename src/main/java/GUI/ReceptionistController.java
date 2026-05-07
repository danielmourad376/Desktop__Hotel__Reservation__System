package GUI;

import back_end_classes.Receptionist;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import java.io.IOException;



public class ReceptionistController {

    private Receptionist currentReceptionist;

    @FXML private Label welcomeLabel;
    @FXML private AnchorPane contentArea;

    @FXML private Button reservationsBtn;
    @FXML private Button roomsBtn;
    @FXML private Button guestsBtn;

    public void setSession(Receptionist receptionist) {
        this.currentReceptionist = receptionist;
        if (currentReceptionist != null) {
            welcomeLabel.setText("Desk: " + currentReceptionist.getUsername());
        } else {
            welcomeLabel.setText("Desk: Unknown Staff");
        }
    }

    private void loadSubView(String fxmlFile, Button selectedButton) {

        if (selectedButton != null) {
            selectedButton.requestFocus();
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlFile));
            Node view = loader.load();

            contentArea.getChildren().setAll(view);
            AnchorPane.setTopAnchor(view, 0.0);
            AnchorPane.setBottomAnchor(view, 0.0);
            AnchorPane.setLeftAnchor(view, 0.0);
            AnchorPane.setRightAnchor(view, 0.0);

            Object controller = loader.getController();

            if (controller instanceof ManageReservationsController) {
                ((ManageReservationsController) controller).setSession(currentReceptionist);
            }


        } catch (IOException e) {
            RegisterController.showAlert(Alert.AlertType.ERROR, "Navigation Error", "Could not load the requested screen: " + fxmlFile);
        } catch (Exception e) {
            RegisterController.showAlert(Alert.AlertType.ERROR, "System Error", "An unexpected error occurred while loading the view.");
        }
    }

    @FXML
    private void handleManageReservations(ActionEvent event) {
        loadSubView("ManageReservations.fxml", reservationsBtn);
    }

    @FXML
    private void handleViewRooms(ActionEvent event) {
        loadSubView("ViewRooms.fxml", roomsBtn);
    }

    @FXML
    private void handleViewGuests(ActionEvent event) {
        loadSubView("ViewGuests.fxml", guestsBtn);
    }

    @FXML
    private void handleLogout(ActionEvent event) {
        currentReceptionist = null;
        SceneSwitcher.goTo(event, "Launch.fxml");
    }
}