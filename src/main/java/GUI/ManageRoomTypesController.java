package GUI;

import back_end_classes.Admin;
import back_end_classes.HotelDatabase;
import back_end_classes.RoomType;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.util.Callback;

public class ManageRoomTypesController {

    private Admin currentAdmin;

    @FXML private TableView<RoomType> roomTypesTable;
    @FXML private TableColumn<RoomType, String> colName;
    @FXML private TableColumn<RoomType, String> colPrice;
    @FXML private TableColumn<RoomType, String> colMaxOcc;
    @FXML private TableColumn<RoomType, String> colDesc;

    @FXML private TextField nameField;
    @FXML private TextField priceField;
    @FXML private TextField occField;
    @FXML private TextField descField;

    @FXML private Button updateBtn;
    @FXML private Button deleteBtn;

    private AdminController parentController;

    public void setSession(Admin admin, AdminController parent) {
        this.currentAdmin = admin;
        this.parentController = parent;
    }

    @FXML
    public void initialize() {
        colName.setCellValueFactory(new PropertyValueFactory<>("name"));

        colPrice.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<RoomType, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TableColumn.CellDataFeatures<RoomType, String> param) {
                return new SimpleStringProperty(String.format("$%.2f", param.getValue().getBasePrice()));
            }
        });

        colMaxOcc.setCellValueFactory(new PropertyValueFactory<>("maxOccupancy"));
        colDesc.setCellValueFactory(new PropertyValueFactory<>("description"));

        roomTypesTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSel, newSel) -> {
            if (newSel != null) {
                nameField.setText(newSel.getName());
                priceField.setText(String.valueOf(newSel.getBasePrice()));
                occField.setText(String.valueOf(newSel.getMaxOccupancy()));
                descField.setText(newSel.getDescription());
                updateBtn.setDisable(false);
                deleteBtn.setDisable(false);
            } else {
                nameField.clear();
                priceField.clear();
                occField.clear();
                descField.clear();
                updateBtn.setDisable(true);
                deleteBtn.setDisable(true);
            }
        });

        loadTableData();
    }

    private void loadTableData() {
        ObservableList<RoomType> data = FXCollections.observableArrayList(HotelDatabase.getInstance().getRoomTypes());
        roomTypesTable.setItems(data);
    }

    @FXML
    private void handleAdd(ActionEvent event) {
        try {
            String name = nameField.getText().trim();
            String priceStr = priceField.getText().trim();
            String occStr = occField.getText().trim();
            String desc = descField.getText().trim();

            if (name.isEmpty() || priceStr.isEmpty() || occStr.isEmpty() || desc.isEmpty()) {
                RegisterController.showAlert(Alert.AlertType.WARNING, "Missing Data", "Please fill out all fields before adding a Room Type.");
                return;
            }

            double price = Double.parseDouble(priceStr);
            int occ = Integer.parseInt(occStr);

            currentAdmin.addRoomType(name, price, desc, occ);

            loadTableData();
            handleClear(null);
            if (parentController != null) {
                parentController.updateStats();
            }
            RegisterController.showAlert(Alert.AlertType.INFORMATION, "Success", "Room Type added to database.");

        } catch (NumberFormatException e) {
            RegisterController.showAlert(Alert.AlertType.ERROR, "Invalid Input", "Price and Occupancy must be valid numbers (e.g., 150.00).");
        } catch (IllegalArgumentException e) {
            RegisterController.showAlert(Alert.AlertType.WARNING, "Action Failed", e.getMessage());
        } catch (Exception e) {
            RegisterController.showAlert(Alert.AlertType.ERROR, "System Error", "An unexpected error occurred.");
        }
    }

    @FXML
    private void handleUpdate(ActionEvent event) {
        RoomType selected = roomTypesTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            try {
                String name = nameField.getText().trim();
                String priceStr = priceField.getText().trim();
                String occStr = occField.getText().trim();
                String desc = descField.getText().trim();

                if (name.isEmpty() || priceStr.isEmpty() || occStr.isEmpty() || desc.isEmpty()) {
                    RegisterController.showAlert(Alert.AlertType.WARNING, "Missing Data", "Fields cannot be empty when updating.");
                    return;
                }

                double price = Double.parseDouble(priceStr);
                int occ = Integer.parseInt(occStr);

                currentAdmin.updateRoomType(selected.getTypeId(), name, price, desc, occ);

                roomTypesTable.refresh();
                RegisterController.showAlert(Alert.AlertType.INFORMATION, "Success", "Room Type fully updated.");

            } catch (NumberFormatException e) {
                RegisterController.showAlert(Alert.AlertType.ERROR, "Invalid Input", "Price and Occupancy must be numbers.");
            } catch (IllegalArgumentException e) {
                RegisterController.showAlert(Alert.AlertType.WARNING, "Update Failed", e.getMessage());
            } catch (Exception e) {
                RegisterController.showAlert(Alert.AlertType.ERROR, "System Error", "An unexpected error occurred.");
            }
        }
    }

    @FXML
    private void handleDelete(ActionEvent event) {
        RoomType selected = roomTypesTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            try {
                currentAdmin.deleteRoomType(selected.getTypeId());

                loadTableData();
                handleClear(null);
                if (parentController != null) {
                    parentController.updateStats();
                }
                RegisterController.showAlert(Alert.AlertType.INFORMATION, "Success", "Room Type deleted from database.");
            } catch (IllegalArgumentException e) {
                RegisterController.showAlert(Alert.AlertType.WARNING, "Delete Failed", e.getMessage());
            } catch (Exception e) {
                RegisterController.showAlert(Alert.AlertType.ERROR, "System Error", "An unexpected error occurred.");
            }
        }
    }

    @FXML
    private void handleClear(ActionEvent event) {
        nameField.clear();
        priceField.clear();
        occField.clear();
        descField.clear();
        roomTypesTable.getSelectionModel().clearSelection();
        updateBtn.setDisable(true);
        deleteBtn.setDisable(true);
    }
}