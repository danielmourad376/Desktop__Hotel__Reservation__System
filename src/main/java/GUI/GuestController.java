package GUI;

import back_end_classes.Guest;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;

public class GuestController{

    // The backend session data
    private Guest currentGuest;

    //UI elements
    @FXML private Label welcomeLabel;
    @FXML private Label balanceLabel;


    @FXML private AnchorPane contentArea;

    /**
     * Called by Login/Register controllers right before switching to this screen.
     */
    public void setSession(Guest guest) {
        this.currentGuest = guest;

        // auto update the top navigation bar
        welcomeLabel.setText("Welcome, " + currentGuest.getUsername());
        balanceLabel.setText(String.format("Balance: $%,.2f", currentGuest.getBalance()));
    }
    public void refreshTopBar() {
        if (currentGuest != null) {
            welcomeLabel.setText("Welcome, " + currentGuest.getUsername());
            balanceLabel.setText(String.format("Balance: $%,.2f", currentGuest.getBalance()));
        }
    }

    //loads the scenes that appear after user clicks the buttons in their menu
    private void loadSubView(String fxmlFile) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlFile));
            javafx.scene.Node view = loader.load();

            //gets the controller obj of the fxml file from the ref variable "loader" then stores it in the generic object called controller
            Object controller = loader.getController();
            if (controller instanceof AvailableRoomsController) { //checks if the generic obj "controller" is of said controller class
                ((AvailableRoomsController) controller).setGuestSession(currentGuest);
            } else if (controller instanceof MyReservationsController) {
                ((MyReservationsController) controller).setGuestSession(currentGuest, this);
            } else if (controller instanceof ManageWalletController) {
                //pass the guest AND the GuestController to refresh the top bar if balance changes
                ((ManageWalletController) controller).setGuestSession(currentGuest, this);
            }else if (controller instanceof EditProfileController) {
                //pass session and parent so we can refresh the top bar if username changes
                ((EditProfileController) controller).setGuestSession(currentGuest, this);
            }
            // Clear the existing welcome text and set the new view to fill the pane
            contentArea.getChildren().setAll(view);
            AnchorPane.setTopAnchor(view, 0.0);
            AnchorPane.setBottomAnchor(view, 0.0);
            AnchorPane.setLeftAnchor(view, 0.0);
            AnchorPane.setRightAnchor(view, 0.0);

        } catch (Exception e) {
            System.out.println("Error loading view: " + fxmlFile);
        }
    }

    @FXML
    private void handleViewRooms(ActionEvent event) {
        loadSubView("AvailableRooms.fxml");
    }

    @FXML
    private void handleViewReservations(ActionEvent event) { loadSubView("MyReservations.fxml");}

    @FXML
    private void handleManageWallet(ActionEvent event) { loadSubView("ManageWallet.fxml");}

    @FXML
    private void handleEditProfile(ActionEvent event) {
        loadSubView("EditProfile.fxml");
    }

    @FXML
    private void handleLogout(ActionEvent event) {
        //remove the session data (prevents different users from having the same session as other users)
        currentGuest = null;

        //go back to start menu
        SceneSwitcher.goTo(event, "Launch.fxml");
    }
}