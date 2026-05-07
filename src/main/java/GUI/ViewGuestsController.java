package GUI;

import back_end_classes.Guest;
import back_end_classes.HotelDatabase;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

public class ViewGuestsController {

    @FXML private TableView<Guest> guestsTable;
    @FXML private TableColumn<Guest, String> colUsername;
    @FXML private TableColumn<Guest, String> colDOB;
    @FXML private TableColumn<Guest, String> colGender;
    @FXML private TableColumn<Guest, String> colAddress;
    @FXML private TableColumn<Guest, String> colBalance;

    @FXML
    public void initialize() {
        colUsername.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getUsername()));
        colDOB.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getDateOfBirth().toString()));
        colGender.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getGender().toString()));
        colAddress.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getAddress()));
        colBalance.setCellValueFactory(cellData -> new SimpleStringProperty(String.format("$%.2f", cellData.getValue().getBalance())));

        ObservableList<Guest> data = FXCollections.observableArrayList(HotelDatabase.getInstance().getGuests());
        guestsTable.setItems(data);
    }
}