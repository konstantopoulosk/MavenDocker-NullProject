package nullteam.gui;

import com.nullteam.ClientUpdater;
import com.nullteam.DatabaseThread;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Objects;
import java.util.Scanner;

public class App extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent root;
        try {
            root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/Home.fxml")));
            Scene scene = new Scene(root);
            primaryStage.getIcons().add(getIcon());
            primaryStage.setTitle("Docker Project");
            primaryStage.setResizable(false);
            primaryStage.setScene(scene);
            primaryStage.show();
        } catch (IOException e) {
            System.out.println("System IO exception thrown!");
            System.out.println(e.getMessage());
        }
    }

    /**
     *
     * @return icon Image
     */
    public static Image getIcon() {
        try {
            /* Getting the Image for the Icon. */
            FileInputStream inputStream = new FileInputStream("Icon.png");
            return new Image(inputStream); // Return the Icon
        } catch (Exception e) {
            System.out.println("Caught Error: " + e.getMessage());
            return null;
        }
    }
    public static void main(String[] args) {
        ClientUpdater.connectionAccomplished();
        launch(args);
    }
}