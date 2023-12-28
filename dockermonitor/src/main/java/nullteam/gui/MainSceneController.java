package nullteam.gui;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.effect.Reflection;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import java.io.IOException;
import java.util.Objects;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;
import javafx.scene.Node;

import static nullteam.gui.App.height;
import static nullteam.gui.App.width;

public class MainSceneController {
     Stage stage;
     Scene scene;
     Parent root;

    @FXML
    public void startApp(ActionEvent event) throws IOException  {
        //gia allagi skhnhs
        System.out.println("Start!!!!");
        root = FXMLLoader.load(getClass().getResource("/Scene2.fxml"));
        stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }
    @FXML
    public void exitApp(ActionEvent event) {
        System.exit(0);
        //Exiting Application.
    }
    @FXML
    public void switchToImages(ActionEvent event) throws IOException {
        root = FXMLLoader.load(getClass().getResource("/images.fxml"));
        stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    @FXML
    public void pressContainers(ActionEvent event) throws IOException {
        System.out.println("Containers Pressed");
        root = FXMLLoader.load(getClass().getResource("/containers.fxml"));
        stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }
    @FXML
    public void pressLogs(ActionEvent event) throws IOException {
        System.out.println("Logs Pressed");
        root = FXMLLoader.load(getClass().getResource("/logs.fxml"));
        stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }
    @FXML
    public void pressNetworks(ActionEvent event) throws IOException {
        System.out.println("Networks Pressed");
        root = FXMLLoader.load(getClass().getResource("/networks.fxml"));
        stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }
    @FXML
    public void pressVolumes(ActionEvent event) throws IOException {
        System.out.println("Volumes Pressed");
        root = FXMLLoader.load(getClass().getResource("/volumes.fxml"));
        stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }
}
