package com.example.hotelreservationsystem;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.scene.layout.BorderPane;


import java.io.IOException;

public class HelloApplication extends Application {

    public static void main(String[] args) {
        launch(args);
    }
    @Override
    public void start(Stage stage) throws Exception {

        //Stage stage = new Stage();
        Group root = new Group();
        Scene scene = new Scene(root, Color.TEAL);

        stage.setTitle("Stage Demo Program");
        stage.setWidth(400);
        stage.setHeight(400);
        stage.setResizable(false);
        stage.setScene(scene);
        stage.show();
    }
}
