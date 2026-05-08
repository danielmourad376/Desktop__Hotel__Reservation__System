package GUI;

import back_end_classes.Guest;
import back_end_classes.HotelDatabaseSearch;
import back_end_classes.Room;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.scene.input.KeyCode;
import javafx.util.Callback;

import java.io.IOException;
import java.util.ArrayList;

public class AvailableRoomsController {

    private Guest currentGuest;

    @FXML private TableView<Room> roomsTable;
    @FXML private TableColumn<Room, String> colRoomNumber;
    @FXML private TableColumn<Room, String> colRoomType;
    @FXML private TableColumn<Room, String> colPrice;
    @FXML private TableColumn<Room, String> colAmenities;

    //used by GuestController
    public void setGuestSession(Guest guest) {
        this.currentGuest = guest;
    }

    @FXML
    public void initialize() {

        colRoomNumber.setCellValueFactory(new PropertyValueFactory<>("roomNumber"));

        colRoomType.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Room, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TableColumn.CellDataFeatures<Room, String> param) {
                return new SimpleStringProperty(param.getValue().getRoomType().getName());
            }
        });

        colPrice.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Room, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TableColumn.CellDataFeatures<Room, String> param) {
                return new SimpleStringProperty(String.format("$%.2f", param.getValue().calculatePrice()));
            }
        });


        colAmenities.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Room, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TableColumn.CellDataFeatures<Room, String> param) {
                ArrayList<back_end_classes.Amenity> amList = param.getValue().getAmenities();
                if (amList.isEmpty()) return new SimpleStringProperty("None");

                StringBuilder sb = new StringBuilder();
                for (int i = 0; i < amList.size(); i++) {
                    back_end_classes.Amenity a = amList.get(i);

                    sb.append("• ").append(a.getName()).append(" (").append(a.getType()).append(") - $").append(a.getPricePerDay()).append("/day");
                    if (i < amList.size() - 1) sb.append("\n"); // Add line break
                }
                return new SimpleStringProperty(sb.toString());
            }
        });


        colAmenities.setCellFactory(tc -> {
            return new TableCell<>() {
                private final javafx.scene.text.Text text = new javafx.scene.text.Text();

                @Override
                protected void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty || item == null) {
                        setGraphic(null);
                    } else {
                        text.setText(item);
                        // Bind text width to column width (minus a little padding)
                        text.wrappingWidthProperty().bind(tc.widthProperty().subtract(15));
                        text.setStyle("-fx-fill: " + (isSelected() ? "white" : "black") + ";");
                        setGraphic(text);
                    }
                }
            };
        });

        // Enter key listener
        roomsTable.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                Room selectedRoom = roomsTable.getSelectionModel().getSelectedItem();

                if (selectedRoom != null) {
                    openReservationConfirmation(selectedRoom);
                }
            }
        });
        //double-click listener
        roomsTable.setRowFactory(tv -> {
            TableRow<Room> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && !row.isEmpty()) {
                    Room selectedRoom = row.getItem();
                    openReservationConfirmation(selectedRoom);
                }
            });
            return row;
        });
        //both listeners open reserveRoom.fxml

        loadAvailableRooms();
    }

    private void openReservationConfirmation(Room room) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("ReserveRoom.fxml"));
            Parent root = loader.load();

            // Pass the selected room to the reservation confirm controller
            ReserveConfirmController controller = loader.getController();
            controller.setRoom(room);
            controller.setGuestSession(currentGuest);

            // Show as a popup window
            Stage confirmStage = new Stage();
            confirmStage.setTitle("Reserve Room");
            confirmStage.setScene(new Scene(root));
            confirmStage.initModality(Modality.APPLICATION_MODAL); // blocks main window
            confirmStage.showAndWait();

        } catch (IOException e) {
            System.out.println("Could not load reservation confirmation.");
        }
    }

    private void loadAvailableRooms() {
        //fetch data from utility class and send to the TableView
        ArrayList<Room> available = HotelDatabaseSearch.findAvailableRooms();
        ObservableList<Room> roomData = FXCollections.observableArrayList(available);
        roomsTable.setItems(roomData);
    }
}