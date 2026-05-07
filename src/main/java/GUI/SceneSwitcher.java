package GUI;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import java.io.IOException;

public class SceneSwitcher {

    /**
     A helper method to handle swapping scenes
     **/
    public static void goTo(ActionEvent event, String fxmlFileName) {
        try {
            FXMLLoader loader = new FXMLLoader(SceneSwitcher.class.getResource(fxmlFileName));
            Parent root = loader.load();

            Scene currentScene = ((Node) event.getSource()).getScene();
            currentScene.setRoot(root);
        } catch (IOException e) {
            System.out.println("CRITICAL ERROR: Could not load " + fxmlFileName);
        }
    }
}