package nullteam.gui;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Paths;

public class App extends Application {
    /*
    String sceneOne = "nullteam.gui/Scene1.fxml";
    URL url  = null;

     */
    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent root;
        try {
            URL url = Paths.get("dockermonitor/src/main/resources/Scene1.fxml").toUri().toURL();
            root = FXMLLoader.load(url);
            Scene scene = new Scene(root);
            primaryStage.setScene(scene);
            primaryStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public static void main(String[] args) throws Exception {
        launch(args);
    }
}