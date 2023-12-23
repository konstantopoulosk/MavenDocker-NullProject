package nullteam.gui;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.effect.Reflection;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;

import static nullteam.gui.App.height;
import static nullteam.gui.App.width;

public class MainSceneController {

    @FXML
    public void startApp(ActionEvent event) {
        //todo next slide
        System.out.println("Starting App");
        //This is printed in the command line!

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
}
