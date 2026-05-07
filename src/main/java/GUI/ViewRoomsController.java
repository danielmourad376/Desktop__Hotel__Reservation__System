package GUI;

import back_end_classes.HotelDatabase;
import back_end_classes.Room;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

public class ViewRoomsController {

    @FXML private TableView<Room> roomsTable;
    @FXML private TableColumn<Room, String> colRoomNum;
    @FXML private TableColumn<Room, String> colType;
    @FXML private TableColumn<Room, String> colPrice;
    @FXML private TableColumn<Room, String> colMaxOcc;
    @FXML private TableColumn<Room, String> colStatus;

    @FXML
    public void initialize() {
        colRoomNum.setCellValueFactory(cellData -> new SimpleStringProperty(String.valueOf(cellData.getValue().getRoomNumber())));
        colType.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getRoomType().getName()));
        colPrice.setCellValueFactory(cellData -> new SimpleStringProperty(String.format("$%.2f", cellData.getValue().getRoomType().getBasePrice())));
        colMaxOcc.setCellValueFactory(cellData -> new SimpleStringProperty(String.valueOf(cellData.getValue().getRoomType().getMaxOccupancy())));

        // Convert boolean to readable text
        colStatus.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().isAvailable() ? "Available" : "Occupied"));

        ObservableList<Room> data = FXCollections.observableArrayList(HotelDatabase.getInstance().getRooms());
        roomsTable.setItems(data);
    }
}