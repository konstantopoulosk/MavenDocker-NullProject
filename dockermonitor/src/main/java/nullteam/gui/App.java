package nullteam.gui;
import com.nullteam.*;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Objects;

public class App extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent root;
        try {
            root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/Scene1.fxml")));
            // Icon
            FileInputStream inputStream = new FileInputStream("Icon.png");
            Image icon = new Image(inputStream);
            // End Icon
            Scene scene = new Scene(root);
            primaryStage.getIcons().add(icon);
            primaryStage.setTitle("Docker Project");
            primaryStage.setResizable(false);
            primaryStage.setScene(scene);
            primaryStage.show();
        } catch (IOException e) {
            System.out.println("System IO exception thrown!");
        }
    }
    public static void main(String[] args) {
        ClientUpdater.connectionAccomplished();
        launch(args);
    }
}