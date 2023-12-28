package nullteam.gui;

import com.nullteam.ClientUpdater;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;

public class App extends Application {
    /*
     * String sceneOne = "nullteam.gui/Scene1.fxml";
     * URL url = null;
     * 
     */
    static final int width = 1000;
    static final int height = 800;

    @Override
    public void start(Stage primaryStage) throws Exception {

        Parent root;
        try {
            root = FXMLLoader.load(getClass().getResource("/Scene1.fxml"));
            // Icon
            FileInputStream inputStream = new FileInputStream("docker.png");
            Image icon = new Image(inputStream);
            // End Icon
            Scene scene = new Scene(root);
            primaryStage.setScene(scene);
            primaryStage.show();



            primaryStage.getIcons().add(icon);
            primaryStage.setTitle("Docker Project");
            primaryStage.setResizable(false);
            primaryStage.setScene(scene);

            primaryStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws Exception {
        // ClientUpdater.connectionAccomplished();

        launch(args);
         Connection connection = ClientUpdater.connectToDatabase();

    }
}