package GUI;

import back_end_classes.HotelDatabase;
import back_end_classes.Room;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.util.Callback;

public class ViewRoomsController {

    @FXML private TableView<Room> roomsTable;
    @FXML private TableColumn<Room, String> colRoomNum;
    @FXML private TableColumn<Room, String> colType;
    @FXML private TableColumn<Room, String> colPrice;
    @FXML private TableColumn<Room, String> colMaxOcc;
    @FXML private TableColumn<Room, String> colStatus;

    @FXML
    public void initialize() {
        colRoomNum.setCellValueFactory(new PropertyValueFactory<>("roomNumber"));

        colType.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Room, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TableColumn.CellDataFeatures<Room, String> param) {
                return new SimpleStringProperty(param.getValue().getRoomType().getName());
            }
        });

        colPrice.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Room, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TableColumn.CellDataFeatures<Room, String> param) {
                return new SimpleStringProperty(String.format("$%.2f", param.getValue().getRoomType().getBasePrice()));
            }
        });

        colMaxOcc.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Room, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TableColumn.CellDataFeatures<Room, String> param) {
                return new SimpleStringProperty(String.valueOf(param.getValue().getRoomType().getMaxOccupancy()));
            }
        });

        // Convert boolean to readable text
        colStatus.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Room, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TableColumn.CellDataFeatures<Room, String> param) {
                return new SimpleStringProperty(param.getValue().isAvailable() ? "Available" : "Occupied");
            }
        });

        ObservableList<Room> data = FXCollections.observableArrayList(HotelDatabase.getInstance().getRooms());
        roomsTable.setItems(data);
    }
}