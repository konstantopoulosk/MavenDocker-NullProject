package nullteam.gui;

import com.nullteam.ClientUpdater;
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
            root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/homeNew.fxml")));
            Scene scene = new Scene(root);
            primaryStage.getIcons().add(new Image("Icon.png"));
            primaryStage.setTitle("Docker Project");
            primaryStage.setResizable(false);
            primaryStage.setScene(scene);
            primaryStage.show();
        } catch (IOException e) {
            System.out.println("System IO exception thrown!");
            System.out.println(e.getMessage());
        }
    }
    public static void main(String[] args) {
        ClientUpdater.connectionAccomplished();
        launch(args);
    }
}