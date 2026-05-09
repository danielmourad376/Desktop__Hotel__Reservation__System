package GUI;

import back_end_classes.Admin;
import back_end_classes.HotelDatabase;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;

import java.io.IOException;

public class AdminController {

    private Admin currentAdmin;

    @FXML private Label welcomeLabel;
    @FXML private AnchorPane contentArea;

    //Status of database
    @FXML private Label statRooms;
    @FXML private Label statGuests;
    @FXML private Label statReservations;

    //sidebar buttons
    @FXML private Button manageRoomsBtn;
    @FXML private Button manageRoomTypesBtn;
    @FXML private Button manageAmenitiesBtn;
    @FXML private Button manageStaffBtn;
    @FXML private Button viewGuestsBtn;
    @FXML private Button viewReservationsBtn;

    public void setSession(Admin admin) {
        this.currentAdmin = admin;
        if (currentAdmin != null) {
            welcomeLabel.setText("System Admin: " + currentAdmin.getUsername());
            updateStats();
        } else {
            welcomeLabel.setText("System Admin: Unknown");
        }
    }

    //bring data from backend to populate the stat labels above the menu
    public void updateStats() {
        statRooms.setText("Rooms: " + HotelDatabase.getInstance().getRooms().size());
        statGuests.setText("Guests: " + HotelDatabase.getInstance().getGuests().size());
        statReservations.setText("Reservations: " + HotelDatabase.getInstance().getReservations().size());
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
            if (controller instanceof ManageStaffController) {
                ((ManageStaffController) controller).setSession(currentAdmin, this);
            }else if (controller instanceof ManageRoomTypesController) {
                ((ManageRoomTypesController) controller).setSession(currentAdmin, this);
            } else if (controller instanceof ManageAmenitiesController) {
                ((ManageAmenitiesController) controller).setSession(currentAdmin, this);
            }else if (controller instanceof ManageRoomsController) {
                ((ManageRoomsController) controller).setSession(currentAdmin, this);
            }else if (controller instanceof ManageReservationsController) {
                ((ManageReservationsController) controller).setSession(currentAdmin, this);
            }

        } catch (IOException e) {
            RegisterController.showAlert(Alert.AlertType.ERROR, "Navigation Error", "Could not load the requested screen: " + fxmlFile);
        } catch (Exception e) {
            RegisterController.showAlert(Alert.AlertType.ERROR, "System Error", "An unexpected error occurred while loading the view.");
        }
    }

    @FXML
    private void handleManageRooms(ActionEvent event) {
        loadSubView("ManageRooms.fxml", manageRoomsBtn);
    }

    @FXML
    private void handleManageRoomTypes(ActionEvent event) {
        loadSubView("ManageRoomTypes.fxml", manageRoomTypesBtn);
    }

    @FXML
    private void handleManageAmenities(ActionEvent event) {
        loadSubView("ManageAmenities.fxml", manageAmenitiesBtn);
    }

    @FXML
    private void handleManageStaff(ActionEvent event) {
        loadSubView("ManageStaff.fxml", manageStaffBtn);
    }

    @FXML
    private void handleViewGuests(ActionEvent event) {
        loadSubView("ViewGuests.fxml", viewGuestsBtn);
    }

    @FXML
    private void handleViewReservations(ActionEvent event) {
        loadSubView("ManageReservations.fxml", viewReservationsBtn);
    }

    @FXML
    private void handleLogout(ActionEvent event) {
        currentAdmin = null;
        SceneSwitcher.goTo(event, "Launch.fxml");
    }
}