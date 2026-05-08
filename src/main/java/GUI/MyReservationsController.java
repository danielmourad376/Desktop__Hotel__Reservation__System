package GUI;

import back_end_classes.Guest;
import back_end_classes.HotelDatabaseSearch;
import back_end_classes.Reservation;
import back_end_classes.ReservationStatus;
import back_end_classes.PaymentMethod;
import exception.InvalidReservationStateException;
import exception.UnauthorizedActionException;
import exception.InvalidCheckOutException;
import exception.InvalidPaymentException;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.ChoiceDialog;
import javafx.scene.control.TableRow;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyCode;
import javafx.util.Callback;

import java.util.ArrayList;
import static GUI.RegisterController.showAlert;

public class MyReservationsController {

    private Guest currentGuest;

    @FXML private TableView<Reservation> reservationsTable;
    @FXML private TableColumn<Reservation, String> colResId;
    @FXML private TableColumn<Reservation, String> colRoomNum;
    @FXML private TableColumn<Reservation, String> colCheckIn;
    @FXML private TableColumn<Reservation, String> colCheckOut;
    @FXML private TableColumn<Reservation, String> colTotal;
    @FXML private TableColumn<Reservation, String> colStatus;

    @FXML private Button cancelButton;
    private GuestController parentController;

    public void setGuestSession(Guest guest, GuestController parent) {
        this.currentGuest = guest;
        this.parentController = parent;
        loadReservations(); // Load data once the session is set
    }

    @FXML
    public void initialize() {
        // Map backend Reservation data to Table columns
        colResId.setCellValueFactory(new PropertyValueFactory<>("reservationId"));

        colRoomNum.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Reservation, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TableColumn.CellDataFeatures<Reservation, String> param) {
                return new SimpleStringProperty(String.valueOf(param.getValue().getRoom().getRoomNumber()));
            }
        });

        colCheckIn.setCellValueFactory(new PropertyValueFactory<>("checkInDate"));

        colCheckOut.setCellValueFactory(new PropertyValueFactory<>("checkOutDate"));

        colTotal.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Reservation, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TableColumn.CellDataFeatures<Reservation, String> param) {
                return new SimpleStringProperty(String.format("$%.2f", param.getValue().calculateTotal()));
            }
        });

        colStatus.setCellValueFactory(new PropertyValueFactory<>("status"));

        // Disable cancel button if nothing is selected
        cancelButton.setDisable(true);
        reservationsTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                // Only enable cancellation if the status allows it
                boolean isCancellable = newSelection.getStatus() == ReservationStatus.PENDING ||
                        newSelection.getStatus() == ReservationStatus.CONFIRMED;
                cancelButton.setDisable(!isCancellable);
            } else {
                cancelButton.setDisable(true);
            }
        });

        //double-click and enter key listener for table
        reservationsTable.setRowFactory(tableView -> {
            TableRow<Reservation> row = new TableRow<>();

            //mouse Double-Click Listener
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && !row.isEmpty()) {
                    triggerCheckoutLogic(row.getItem());
                }
            });

            // Enter key listener
            reservationsTable.setOnKeyPressed(event -> {
                if (event.getCode() == KeyCode.ENTER) {
                    Reservation selected = reservationsTable.getSelectionModel().getSelectedItem();
                    if (selected != null) {
                        triggerCheckoutLogic(selected);
                    }
                }
            });
            return row;
        });
    }

    private void triggerCheckoutLogic(Reservation selected) {
        if (selected.getStatus() == ReservationStatus.CONFIRMED) {
            processCheckoutViaDialog(selected);
        } else {
            showAlert(Alert.AlertType.WARNING, "Invalid Action",
                    "Only CONFIRMED reservations can be checked out.\nCurrent status: " + selected.getStatus());
        }
    }
    private void loadReservations() {
        if (currentGuest != null) {
            ArrayList<Reservation> history = HotelDatabaseSearch.findReservationsByGuestUsername(currentGuest.getUsername());

            //filter the list to only show active reservations
            ArrayList<Reservation> activeReservations = new ArrayList<>();
            for (int i = 0; i < history.size(); i++) {
                Reservation res = history.get(i);
                if (res.getStatus() == ReservationStatus.PENDING || res.getStatus() == ReservationStatus.CONFIRMED) {
                    activeReservations.add(res);
                }
            }

            ObservableList<Reservation> resData = FXCollections.observableArrayList(activeReservations);
            reservationsTable.setItems(resData);
        }
    }

    @FXML
    private void handleCancelReservation(ActionEvent event) {
        Reservation selected = reservationsTable.getSelectionModel().getSelectedItem();

        if (selected != null) {
            try {
                //call the backend cancellation logic
                currentGuest.cancelReservation(selected.getReservationId());

                //refresh the table to show the updated "CANCELLED" status
                reservationsTable.refresh();
                cancelButton.setDisable(true); // Disable button after cancel

                showAlert(Alert.AlertType.INFORMATION, "Success", "Reservation #" + selected.getReservationId() + " has been cancelled.");

            } catch (UnauthorizedActionException | InvalidReservationStateException | IllegalArgumentException e) {
                showAlert(Alert.AlertType.ERROR, "Cancellation Failed", e.getMessage());
            } catch (Exception e) {
                showAlert(Alert.AlertType.ERROR, "System Error", "An unexpected error occurred.");
            }
        }
    }
    private void processCheckoutViaDialog(Reservation selected) {
        //create a ChoiceDialog with only the valid online payment methods
        ChoiceDialog<PaymentMethod> dialog = new ChoiceDialog<>(PaymentMethod.CREDIT_CARD, PaymentMethod.CREDIT_CARD, PaymentMethod.ONLINE);
        dialog.setTitle("Checkout & Pay");
        dialog.setHeaderText("Checkout for Reservation #" + selected.getReservationId() + "\nTotal Due: $" + String.format("%.2f", selected.calculateTotal()));
        dialog.setContentText("Select your payment method:");

        //show the dialog and wait for the user to hit "OK"
        dialog.showAndWait().ifPresent(method -> {
            try {
                //call backend logic
                currentGuest.checkoutAndPay(selected.getReservationId(), method);

                if (parentController != null) {
                    parentController.refreshTopBar();
                }

                String invoiceText = String.format(
                        "--- DIGITAL RECEIPT ---\n" +
                                "Reservation #: %d\n" +
                                "Total Paid: $%.2f via %s\n\n" +
                                "Thank you for choosing PrimeStay Hotel. Safe travels!",
                        selected.getReservationId(), selected.calculateTotal(), method
                );

                showAlert(Alert.AlertType.INFORMATION, "Checkout Successful", invoiceText);
                //update the GUI to reflect the COMPLETED status
                reservationsTable.refresh();
                cancelButton.setDisable(true); // Reset cancel button state

                showAlert(Alert.AlertType.INFORMATION, "Checkout Successful",
                        "Successfully paid via " + method + ".\nSafe travels!");

            } catch (InvalidCheckOutException | InvalidPaymentException | UnauthorizedActionException e) {
                // catch custom exceptions
                showAlert(Alert.AlertType.ERROR, "Checkout Failed", e.getMessage());
            } catch (Exception e) {
                showAlert(Alert.AlertType.ERROR, "System Error", "An unexpected error occurred: " + e.getMessage());
            }
        });
    }

}