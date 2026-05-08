package GUI;

import back_end_classes.*;
import exception.InvalidCheckInException;
import exception.InvalidCheckOutException;
import exception.InvalidPaymentException;
import exception.InvalidReservationStateException;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.util.Callback;
import javafx.application.Platform;

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

    private Staff currentStaff;

    // Receives the session data from the ReceptionistController
    public void setSession(Staff staff) {
        this.currentStaff = staff;
    }

    @FXML
    public void initialize() {
        colResId.setCellValueFactory(new PropertyValueFactory<>("reservationId"));

        colGuest.setCellValueFactory(new Callback<>() {
            @Override
            public ObservableValue<String> call(TableColumn.CellDataFeatures<Reservation, String> param) {
                return new SimpleStringProperty(param.getValue().getGuest().getUsername());
            }
        });

        colRoomNum.setCellValueFactory(new Callback<>() {
            @Override
            public ObservableValue<String> call(TableColumn.CellDataFeatures<Reservation, String> param) {
                return new SimpleStringProperty(String.valueOf(param.getValue().getRoom().getRoomNumber()));
            }
        });

        colCheckIn.setCellValueFactory(new PropertyValueFactory<>("checkInDate"));
        colCheckOut.setCellValueFactory(new PropertyValueFactory<>("checkOutDate"));
        colStatus.setCellValueFactory(new PropertyValueFactory<>("status"));

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
        class Task implements Runnable {
            @Override
            public void run() {
                ArrayList<Reservation> allRes = HotelDatabase.getInstance().getReservations();
                ArrayList<Reservation> activeReservations = new ArrayList<>();

                for (int i = 0; i < allRes.size(); i++) {
                    Reservation res = allRes.get(i);
                    if (res.getStatus() == ReservationStatus.PENDING || res.getStatus() == ReservationStatus.CONFIRMED) {
                        activeReservations.add(res);
                    }
                }

                ObservableList<Reservation> data = FXCollections.observableArrayList(activeReservations);

                class UIUpdateTask implements Runnable {
                    @Override
                    public void run() {
                        allReservationsTable.setItems(data);
                    }
                }

                UIUpdateTask uiTask = new UIUpdateTask();
                Platform.runLater(uiTask);
            }
        }

        Task task = new Task();
        Thread t = new Thread(task);
        t.start();
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

        if (selected != null && currentStaff != null) {
            try {
                if (currentStaff instanceof Receptionist) {
                    //downcast the staff object to a receptionist object
                    ((Receptionist) currentStaff).checkIn(selected.getReservationId());
                } else {
                    //admin sets the room availability to false directly
                    selected.getRoom().setAvailable(false);
                }
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

        if (selected != null && currentStaff != null) {

            ChoiceDialog<PaymentMethod> dialog = new ChoiceDialog<>(PaymentMethod.CREDIT_CARD, PaymentMethod.CREDIT_CARD, PaymentMethod.CASH);
            dialog.setTitle("Front Desk Check-Out");
            dialog.setHeaderText("Checking out Reservation #" + selected.getReservationId() + "\nTotal Due: $" + String.format("%.2f", selected.calculateTotal()));
            dialog.setContentText("Select payment method received from guest:");

            dialog.showAndWait().ifPresent(method -> {
                try {
                    if (currentStaff instanceof Receptionist) {
                        //downcast the staff object to a receptionist object
                        ((Receptionist) currentStaff).checkOut(selected.getReservationId(), method);
                    } else {
                        // Admin handles checkout directly through the reservation object
                        selected.processCheckout(method);
                    }
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