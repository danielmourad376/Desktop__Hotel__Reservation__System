package GUI;

import back_end_classes.*;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.util.Callback;

import java.util.ArrayList;

public class ManageRoomsController {

    private Admin currentAdmin;

    @FXML private TableView<Room> roomsTable;
    @FXML private TableColumn<Room, String> colRoomNum;
    @FXML private TableColumn<Room, String> colType;
    @FXML private TableColumn<Room, String> colTotalCost;
    @FXML private TableColumn<Room, String> colStatus;
    @FXML private TableColumn<Room, String> colAmenities;

    @FXML private TextField roomNumField;
    @FXML private ComboBox<RoomType> typeBox;
    @FXML private CheckBox availableCheck;
    @FXML private ListView<Amenity> amenitiesListView;

    @FXML private Button updateBtn;
    @FXML private Button deleteBtn;

    public void setSession(Admin admin) {
        this.currentAdmin = admin;
    }

    @FXML
    public void initialize() {
        //allow selecting multiple amenities in the ListView
        amenitiesListView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        //populate dropdowns and lists from the Database
        typeBox.setItems(FXCollections.observableArrayList(HotelDatabase.getInstance().getRoomTypes()));
        amenitiesListView.setItems(FXCollections.observableArrayList(HotelDatabase.getInstance().getAmenities()));

        //table Columns setup
        colRoomNum.setCellValueFactory(new PropertyValueFactory<>("roomNumber"));

        colType.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Room, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TableColumn.CellDataFeatures<Room, String> param) {
                return new SimpleStringProperty(param.getValue().getRoomType().getName());
            }
        });

        colTotalCost.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Room, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TableColumn.CellDataFeatures<Room, String> param) {
                return new SimpleStringProperty(String.format("$%.2f", param.getValue().calculatePrice()));
            }
        });

        colStatus.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Room, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TableColumn.CellDataFeatures<Room, String> param) {
                return new SimpleStringProperty(param.getValue().isAvailable() ? "Available" : "Occupied");
            }
        });

        colAmenities.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Room, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TableColumn.CellDataFeatures<Room, String> param) {
                ArrayList<Amenity> amenities = param.getValue().getAmenities();
                if (amenities.isEmpty()) return new SimpleStringProperty("None");

                StringBuilder amenityString = new StringBuilder();
                for (int i = 0; i < amenities.size(); i++) {
                    amenityString.append(amenities.get(i).getName());

                    if (i < amenities.size() - 1) {
                        amenityString.append(", ");
                    }
                }
                return new SimpleStringProperty(amenityString.toString());
            }
        });

        roomsTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSel, newSel) -> {
            if (newSel != null) {
                roomNumField.setText(String.valueOf(newSel.getRoomNumber()));
                roomNumField.setDisable(true); //prevent editing the room number of an existing room

                typeBox.setValue(newSel.getRoomType());
                availableCheck.setSelected(newSel.isAvailable());

                amenitiesListView.getSelectionModel().clearSelection();

                //select amenities
                ArrayList<Amenity> roomAmenities = newSel.getAmenities();
                for (int i = 0; i < roomAmenities.size(); i++) {
                    amenitiesListView.getSelectionModel().select(roomAmenities.get(i));
                }

                updateBtn.setDisable(false);
                deleteBtn.setDisable(false);
            } else {
                roomNumField.clear();
                roomNumField.setDisable(false);
                typeBox.setValue(null);
                availableCheck.setSelected(true);
                amenitiesListView.getSelectionModel().clearSelection();
                updateBtn.setDisable(true);
                deleteBtn.setDisable(true);
            }
        });

        loadTableData();
    }

    private void loadTableData() {
        ObservableList<Room> data = FXCollections.observableArrayList(HotelDatabase.getInstance().getRooms());
        roomsTable.setItems(data);
    }

    @FXML
    private void handleAdd(ActionEvent event) {
        try {
            String numStr = roomNumField.getText().trim();
            RoomType selectedType = typeBox.getValue();

            if (numStr.isEmpty() || selectedType == null) {
                RegisterController.showAlert(Alert.AlertType.WARNING, "Missing Data", "Please enter a room number and select a room type.");
                return;
            }

            int roomNum = Integer.parseInt(numStr);

            //add the room via the admin backend
            currentAdmin.addRoom(roomNum, selectedType);

            //update the room to include data
            ArrayList<Amenity> selectedAmenities = new ArrayList<>(amenitiesListView.getSelectionModel().getSelectedItems());
            currentAdmin.updateRoom(roomNum, selectedType, selectedAmenities, availableCheck.isSelected());

            loadTableData();
            handleClear(null);
            RegisterController.showAlert(Alert.AlertType.INFORMATION, "Success", "Room " + roomNum + " successfully created and configured!");

        } catch (NumberFormatException e) {
            RegisterController.showAlert(Alert.AlertType.ERROR, "Invalid Input", "Room number must be a whole number.");
        } catch (IllegalArgumentException e) {
            RegisterController.showAlert(Alert.AlertType.WARNING, "Action Failed", e.getMessage());
        } catch (Exception e) {
            RegisterController.showAlert(Alert.AlertType.ERROR, "System Error", "An unexpected error occurred.");
        }
    }

    @FXML
    private void handleUpdate(ActionEvent event) {
        Room selected = roomsTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            try {
                RoomType newType = typeBox.getValue();
                ArrayList<Amenity> newAmenities = new ArrayList<>(amenitiesListView.getSelectionModel().getSelectedItems());
                boolean isAvailable = availableCheck.isSelected();

                if (newType == null) {
                    RegisterController.showAlert(Alert.AlertType.WARNING, "Missing Data", "Room must have a valid Room Type.");
                    return;
                }

                currentAdmin.updateRoom(selected.getRoomNumber(), newType, newAmenities, isAvailable);

                roomsTable.refresh();
                RegisterController.showAlert(Alert.AlertType.INFORMATION, "Success", "Room " + selected.getRoomNumber() + " fully updated.");
            } catch (IllegalArgumentException e) {
                RegisterController.showAlert(Alert.AlertType.WARNING, "Update Failed", e.getMessage());
            } catch (Exception e) {
                RegisterController.showAlert(Alert.AlertType.ERROR, "System Error", "An unexpected error occurred.");
            }
        }
    }

    @FXML
    private void handleDelete(ActionEvent event) {
        Room selected = roomsTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            try {
                currentAdmin.deleteRoom(selected.getRoomNumber());

                loadTableData();
                handleClear(null);
                RegisterController.showAlert(Alert.AlertType.INFORMATION, "Success", "Room deleted from database.");
            } catch (IllegalArgumentException e) {
                RegisterController.showAlert(Alert.AlertType.WARNING, "Delete Failed", e.getMessage());
            } catch (Exception e) {
                RegisterController.showAlert(Alert.AlertType.ERROR, "System Error", "An unexpected error occurred.");
            }
        }
    }

    @FXML
    private void handleClear(ActionEvent event) {
        roomsTable.getSelectionModel().clearSelection();
    }
}