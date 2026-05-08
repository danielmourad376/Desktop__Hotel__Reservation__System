package GUI;

import back_end_classes.Admin;
import back_end_classes.HotelDatabase;
import back_end_classes.Role;
import back_end_classes.Staff;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

public class ManageStaffController {

    private Admin currentAdmin;

    @FXML private TableView<Staff> staffTable;
    @FXML private TableColumn<Staff, String> colUsername;
    @FXML private TableColumn<Staff, String> colRole;
    @FXML private TableColumn<Staff, String> colDOB;
    @FXML private TableColumn<Staff, String> colHours;

    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private DatePicker dobPicker;
    @FXML private ComboBox<Role> roleBox;
    @FXML private TextField hoursField;

    public void setSession(Admin admin) {
        this.currentAdmin = admin;
    }

    @FXML
    public void initialize() {
        roleBox.getItems().addAll(Role.values());

        colUsername.setCellValueFactory(new PropertyValueFactory<>("username"));
        colRole.setCellValueFactory(new PropertyValueFactory<>("role"));
        colDOB.setCellValueFactory(new PropertyValueFactory<>("dateOfBirth"));
        colHours.setCellValueFactory(new PropertyValueFactory<>("workingHours"));

        loadTableData();
    }

    private void loadTableData() {
        ObservableList<Staff> data = FXCollections.observableArrayList(HotelDatabase.getInstance().getStaffMembers());
        staffTable.setItems(data);
    }

    @FXML
    private void handleHire(ActionEvent event) {
        if (currentAdmin == null) return;

        try {
            String uname = usernameField.getText().trim();
            String pass = passwordField.getText();
            java.time.LocalDate dob = dobPicker.getValue();
            Role jobTitle = roleBox.getValue();
            String hoursText = hoursField.getText().trim();

            if (uname.isEmpty() || pass.isEmpty() || dob == null || jobTitle == null || hoursText.isEmpty()) {
                RegisterController.showAlert(Alert.AlertType.WARNING, "Missing Information", "Please fill out all fields to hire a new employee.");
                return;
            }

            int hours = Integer.parseInt(hoursText);

            currentAdmin.hireEmployee(uname, pass, dob, jobTitle, hours);

            //update UI
            loadTableData();
            clearForm();
            RegisterController.showAlert(Alert.AlertType.INFORMATION, "Success", jobTitle + " " + uname + " was successfully hired and added to the database.");

        } catch (NumberFormatException e) {
            RegisterController.showAlert(Alert.AlertType.ERROR, "Invalid Input", "Working hours must be a whole number.");
        } catch (IllegalArgumentException e) {
            RegisterController.showAlert(Alert.AlertType.WARNING, "Hire Failed", e.getMessage());
        } catch (Exception e) {
            RegisterController.showAlert(Alert.AlertType.ERROR, "System Error", "An unexpected error occurred.");
        }
    }

    private void clearForm() {
        usernameField.clear();
        passwordField.clear();
        dobPicker.setValue(null);
        roleBox.getSelectionModel().clearSelection();
        roleBox.setValue(null);
        hoursField.clear();
    }
}