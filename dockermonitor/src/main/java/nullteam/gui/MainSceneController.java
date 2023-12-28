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
        //todo next slide
        System.out.println("Starting App");
        //This is printed in the command line!

        //gia allagi skhnhs
        root = FXMLLoader.load(getClass().getResource("/Scene2.fxml"));
        stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();




        /*
        Group rootMain = new Group();

        //Title
        Text title = new Text("Main Menu");
        title.setX(50);
        title.setY(50);
        title.setFont(Font.font("Abyssinica SIL", FontWeight.BOLD, FontPosture.REGULAR,25));
        title.setFill(Color.BLUE);
        title.setStroke(Color.BLACK);
        title.setStrokeWidth(1);
        Reflection ref = new Reflection();
        ref.setBottomOpacity(0.2);
        ref.setFraction(12);
        ref.setTopOffset(10);
        ref.setTopOpacity(0.2);
        title.setEffect(ref);
        //End title

        //Line under "Main Menu"
        Line line = new Line();
        line.setStartX(0);
        line.setEndX(width);
        line.setStartY(50);
        line.setEndY(50);
        //End Line under "Main Menu"

         */
        //Create another scene.fxml -> Name: MainMenu.fxml, style it!
        //The above was a preview that I did with pure code.
        //Use SceneBuilder it is much easier.
    }
    @FXML
    public void exitApp(ActionEvent event) {
        System.exit(0);
        //Exiting Application.
    }

    public void backtoscene1(ActionEvent event) throws IOException {
        root = FXMLLoader.load(getClass().getResource("/Scene1.fxml"));
        stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();

    }

    @FXML
    public void SwitchToImages(ActionEvent event) throws IOException {
        root = FXMLLoader.load(getClass().getResource("/Scene4.fxml"));
        stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    @FXML
    public void backtoscene2(ActionEvent event) throws IOException {
        root = FXMLLoader.load(getClass().getResource("/Scene2.fxml"));
        stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }
}
