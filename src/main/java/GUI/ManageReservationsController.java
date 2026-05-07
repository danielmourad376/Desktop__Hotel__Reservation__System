package GUI;

import back_end_classes.*;
import exception.InvalidCheckInException;
import exception.InvalidCheckOutException;
import exception.InvalidPaymentException;
import exception.InvalidReservationStateException;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.util.ArrayList;

public class ManageReservationsController {

    @FXML private TableView<Reservation> allReservationsTable;
    @FXML private TableColumn<Reservation, String> colResId;
    @FXML private TableColumn<Reservation, String> colGuest;
    @FXML private TableColumn<Reservation, String> colRoomNum;
    @FXML private TableColumn<Reservation, String> colCheckIn;
    @FXML private TableColumn<Reservation, String> colCheckOut;
    @FXML private TableColumn<Reservation, String> colStatus;

    @FXML private Button confirmBtn;
    @FXML private Button checkInBtn;
    @FXML private Button checkOutBtn;

    private Receptionist currentReceptionist;

    // Receives the session data from the ReceptionistController
    public void setSession(Receptionist receptionist) {
        this.currentReceptionist = receptionist;
    }

    @FXML
    public void initialize() {
        colResId.setCellValueFactory(cellData -> new SimpleStringProperty(String.valueOf(cellData.getValue().getReservationId())));
        colGuest.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getGuest().getUsername()));
        colRoomNum.setCellValueFactory(cellData -> new SimpleStringProperty(String.valueOf(cellData.getValue().getRoom().getRoomNumber())));
        colCheckIn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getCheckInDate().toString()));
        colCheckOut.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getCheckOutDate().toString()));
        colStatus.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getStatus().toString()));

        confirmBtn.setDisable(true);
        checkInBtn.setDisable(true);
        checkOutBtn.setDisable(true);

        allReservationsTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSel, newSel) -> {
            if (newSel != null) {
                // Only allow confirming if it is PENDING
                confirmBtn.setDisable(newSel.getStatus() != ReservationStatus.PENDING);

                checkInBtn.setDisable(newSel.getStatus() != ReservationStatus.CONFIRMED);
                checkOutBtn.setDisable(newSel.getStatus() != ReservationStatus.CONFIRMED);
            } else {
                confirmBtn.setDisable(true);
                checkInBtn.setDisable(true);
                checkOutBtn.setDisable(true);
            }
        });

        loadTableData();
    }

    private void loadTableData() {
        ArrayList<Reservation> allRes = HotelDatabase.getInstance().getReservations();
        ArrayList<Reservation> activeReservations = new ArrayList<>();

        for (Reservation res : allRes) {
            if (res.getStatus() == ReservationStatus.PENDING || res.getStatus() == ReservationStatus.CONFIRMED) {
                activeReservations.add(res);
            }
        }

        ObservableList<Reservation> data = FXCollections.observableArrayList(activeReservations);
        allReservationsTable.setItems(data);
    }

    @FXML
    private void handleConfirm(ActionEvent event) {
        Reservation selected = allReservationsTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            try {
                selected.confirmReservation();
                allReservationsTable.refresh();

                confirmBtn.setDisable(true);
                checkInBtn.setDisable(false);
                checkOutBtn.setDisable(false);

                RegisterController.showAlert(Alert.AlertType.INFORMATION, "Success", "Reservation #" + selected.getReservationId() + " has been successfully confirmed.");
            } catch (InvalidReservationStateException e) {
                RegisterController.showAlert(Alert.AlertType.WARNING, "Action Failed", e.getMessage());
            }
        }
    }

    @FXML
    private void handleCheckIn(ActionEvent event) {
        Reservation selected = allReservationsTable.getSelectionModel().getSelectedItem();

        if (selected != null && currentReceptionist != null) {
            try {
                currentReceptionist.checkIn(selected.getReservationId());
                allReservationsTable.refresh();

                RegisterController.showAlert(Alert.AlertType.INFORMATION, "Check-In Successful",
                        "Guest " + selected.getGuest().getUsername() + " has been checked into Room " + selected.getRoom().getRoomNumber() + ".");
            } catch (InvalidCheckInException e) {
                RegisterController.showAlert(Alert.AlertType.WARNING, "Check-In Blocked", e.getMessage());
            } catch (Exception e) {
                RegisterController.showAlert(Alert.AlertType.ERROR, "System Error", "An unexpected error occurred during Check-In.");
            }
        }
    }

    @FXML
    private void handleCheckOut(ActionEvent event) {
        Reservation selected = allReservationsTable.getSelectionModel().getSelectedItem();

        if (selected != null && currentReceptionist != null) {

            ChoiceDialog<PaymentMethod> dialog = new ChoiceDialog<>(PaymentMethod.CREDIT_CARD, PaymentMethod.CREDIT_CARD, PaymentMethod.CASH);
            dialog.setTitle("Front Desk Check-Out");
            dialog.setHeaderText("Checking out Reservation #" + selected.getReservationId() + "\nTotal Due: $" + String.format("%.2f", selected.calculateTotal()));
            dialog.setContentText("Select payment method received from guest:");

            dialog.showAndWait().ifPresent(method -> {
                try {
                    currentReceptionist.checkOut(selected.getReservationId(), method);
                    allReservationsTable.refresh();

                    String invoiceText = String.format(
                            "--- FINAL INVOICE ---\n" +
                                    "Reservation #: %d\n" +
                                    "Guest: %s\n" +
                                    "Room: %d\n" +
                                    "Check-In: %s\n" +
                                    "Check-Out: %s\n" +
                                    "Payment Method: %s\n" +
                                    "---------------------\n" +
                                    "Total Paid: $%.2f",
                            selected.getReservationId(), selected.getGuest().getUsername(),
                            selected.getRoom().getRoomNumber(), selected.getCheckInDate(),
                            selected.getCheckOutDate(), method, selected.calculateTotal()
                    );

                    RegisterController.showAlert(Alert.AlertType.INFORMATION, "Checkout Successful - Receipt", invoiceText);
                    loadTableData();
                } catch (InvalidCheckOutException | InvalidPaymentException e) {
                    RegisterController.showAlert(Alert.AlertType.WARNING, "Check-Out Blocked", e.getMessage());
                } catch (Exception e) {
                    RegisterController.showAlert(Alert.AlertType.ERROR, "System Error", "An unexpected error occurred during Check-Out.");
                }
            });
        }
    }
}