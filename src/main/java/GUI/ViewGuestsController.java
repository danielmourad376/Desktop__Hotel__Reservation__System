package GUI;

import back_end_classes.Guest;
import back_end_classes.HotelDatabase;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.util.Callback;

public class ViewGuestsController {

    @FXML private TableView<Guest> guestsTable;
    @FXML private TableColumn<Guest, String> colUsername;
    @FXML private TableColumn<Guest, String> colDOB;
    @FXML private TableColumn<Guest, String> colGender;
    @FXML private TableColumn<Guest, String> colAddress;
    @FXML private TableColumn<Guest, String> colBalance;

    @FXML
    public void initialize() {
        colUsername.setCellValueFactory(new PropertyValueFactory<>("username"));
        colDOB.setCellValueFactory(new PropertyValueFactory<>("dateOfBirth"));
        colGender.setCellValueFactory(new PropertyValueFactory<>("gender"));
        colAddress.setCellValueFactory(new PropertyValueFactory<>("address"));

        colBalance.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Guest, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TableColumn.CellDataFeatures<Guest, String> param) {
                return new SimpleStringProperty(String.format("$%.2f", param.getValue().getBalance()));
            }
        });

        ObservableList<Guest> data = FXCollections.observableArrayList(HotelDatabase.getInstance().getGuests());
        guestsTable.setItems(data);
    }
}