package GUI;

import back_end_classes.DatabaseHelper;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.stage.Stage;
import javafx.scene.Scene;

import java.util.Objects;

public class HelloApplication extends Application {


    @Override
    public void start(Stage primaryStage) throws Exception {
        DatabaseHelper.initializeDatabase();
        DatabaseHelper.loadFromSQLite();
        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("Launch.fxml")));

        Scene scene = new Scene(root, 900, 600);

        String css = Objects.requireNonNull(this.getClass().getResource("Styles.css")).toExternalForm();
        scene.getStylesheets().add(css);

        primaryStage.setTitle("Hotel Reservation System");

        primaryStage.setMinWidth(900);
        primaryStage.setMinHeight(600);

        primaryStage.setScene(scene);
        primaryStage.show();
    }
}
