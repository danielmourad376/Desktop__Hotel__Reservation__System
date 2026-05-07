module com.example.hotelreservationsystem {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;

    opens GUI to javafx.fxml;
    exports GUI;
    exports back_end_classes;
    exports exception;
}