package GUI;

import back_end_classes.Admin;
import back_end_classes.Amenity;
import back_end_classes.HotelDatabase;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.util.Callback;

public class ManageAmenitiesController {

    private Admin currentAdmin;

    @FXML private TableView<Amenity> amenitiesTable;
    @FXML private TableColumn<Amenity, String> colName;
    @FXML private TableColumn<Amenity, String> colType;
    @FXML private TableColumn<Amenity, String> colPrice;

    @FXML private TextField nameField;
    @FXML private TextField typeField;
    @FXML private TextField priceField;

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
        colType.setCellValueFactory(new PropertyValueFactory<>("type"));
        colPrice.setCellValueFactory(new Callback<>() {
            @Override
            public ObservableValue<String> call(TableColumn.CellDataFeatures<Amenity, String> param) {
                return new SimpleStringProperty(String.format("$%.2f", param.getValue().getPricePerDay()));
            }
        });

        amenitiesTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSel, newSel) -> {
            if (newSel != null) {
                nameField.setText(newSel.getName());
                typeField.setText(newSel.getType());
                priceField.setText(String.valueOf(newSel.getPricePerDay()));
                updateBtn.setDisable(false);
                deleteBtn.setDisable(false);
            } else {
                nameField.clear();
                typeField.clear();
                priceField.clear();
                updateBtn.setDisable(true);
                deleteBtn.setDisable(true);
            }
        });

        loadTableData();
    }

    private void loadTableData() {
        ObservableList<Amenity> data = FXCollections.observableArrayList(HotelDatabase.getInstance().getAmenities());
        amenitiesTable.setItems(data);
    }

    @FXML
    private void handleAdd(ActionEvent event) {
        try {
            String name = nameField.getText().trim();
            String type = typeField.getText().trim();
            String priceStr = priceField.getText().trim();

            if (name.isEmpty() || type.isEmpty() || priceStr.isEmpty()) {
                RegisterController.showAlert(Alert.AlertType.WARNING, "Missing Data", "Please fill out all fields before adding an Amenity.");
                return;
            }

            double price = Double.parseDouble(priceStr);

            currentAdmin.addAmenity(name, price, type);

            loadTableData();
            handleClear(null);
            RegisterController.showAlert(Alert.AlertType.INFORMATION, "Success", "Amenity added to database.");

        } catch (NumberFormatException e) {
            RegisterController.showAlert(Alert.AlertType.ERROR, "Invalid Input", "Price must be a valid number (e.g., 15.00).");
        } catch (IllegalArgumentException e) {
            RegisterController.showAlert(Alert.AlertType.WARNING, "Action Failed", e.getMessage());
        } catch (Exception e) {
            RegisterController.showAlert(Alert.AlertType.ERROR, "System Error", "An unexpected error occurred.");
        }
    }

    @FXML
    private void handleUpdate(ActionEvent event) {
        Amenity selected = amenitiesTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            try {
                String name = nameField.getText().trim();
                String type = typeField.getText().trim();
                String priceStr = priceField.getText().trim();

                if (name.isEmpty() || type.isEmpty() || priceStr.isEmpty()) {
                    RegisterController.showAlert(Alert.AlertType.WARNING, "Missing Data", "Fields cannot be empty when updating.");
                    return;
                }

                double price = Double.parseDouble(priceStr);

                currentAdmin.updateAmenity(selected.getAmenityId(), name, price, type);

                amenitiesTable.refresh();
                RegisterController.showAlert(Alert.AlertType.INFORMATION, "Success", "Amenity fully updated.");

            } catch (NumberFormatException e) {
                RegisterController.showAlert(Alert.AlertType.ERROR, "Invalid Input", "Price must be a number.");
            } catch (IllegalArgumentException e) {
                RegisterController.showAlert(Alert.AlertType.WARNING, "Update Failed", e.getMessage());
            } catch (Exception e) {
                RegisterController.showAlert(Alert.AlertType.ERROR, "System Error", "An unexpected error occurred.");
            }
        }
    }

    @FXML
    private void handleDelete(ActionEvent event) {
        Amenity selected = amenitiesTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            try {
                currentAdmin.deleteAmenity(selected.getAmenityId());

                loadTableData();
                handleClear(null);
                RegisterController.showAlert(Alert.AlertType.INFORMATION, "Success", "Amenity deleted from database.");
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
        typeField.clear();
        priceField.clear();
        amenitiesTable.getSelectionModel().clearSelection();
        updateBtn.setDisable(true);
        deleteBtn.setDisable(true);
    }
}