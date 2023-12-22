package nullteam.gui;

import com.nullteam.ClientUpdater;
import com.nullteam.Messages;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Orientation;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollBar;
import javafx.scene.effect.BoxBlur;
import javafx.scene.effect.Reflection;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.FileInputStream;
import java.sql.Connection;

public class App extends Application {
    /*
    String sceneOne = "nullteam.gui/Scene1.fxml";
    URL url  = null;

     */
    @Override
    public void start(Stage primaryStage) throws Exception {
        /*
        Parent root;
        try {
            URL url = Paths.get("dockermonitor/src/main/resources/Scene1.fxml").toUri().toURL();
            root = FXMLLoader.load(url);
            Button buttonExit = new Button("Exit");
            root.getChildrenUnmodifiable().add(buttonExit);
            Scene scene = new Scene(root);
            primaryStage.setScene(scene);
            primaryStage.setTitle("Docker Project");
            primaryStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }

         */
        //Button to exit the app
        Button buttonExit = new Button("Exit App");
        buttonExit.setLayoutX(700);
        buttonExit.setLayoutY(10);
        buttonExit.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                Messages.exitApp(); //Exiting Application.
            }
        });
        //end button to exit

        //Button to start the app
        Button buttonStart = new Button("Start");
        buttonStart.setLayoutY(10);
        buttonStart.setLayoutX(50);
        buttonStart.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                //todo next slide
                System.out.println("Starting App");
                //This is printed in the command line!
            }
        });
        //End button Start

        //Title
        Text title = new Text();
        title.setText("NULL-TEAM");
        title.setX(250);
        title.setY(100);
        title.setFont(Font.font("Abyssinica SIL", FontWeight.BOLD, FontPosture.REGULAR,50));
        title.setFill(Color.BLUE);
        title.setStroke(Color.BLACK);
        title.setStrokeWidth(1);

        Reflection ref = new Reflection();
        ref.setBottomOpacity(0.2);
        ref.setFraction(12);
        ref.setTopOffset(10);
        ref.setTopOpacity(0.2);
        title.setEffect(ref);
        //end title


        //BackGround
        FileInputStream input=new FileInputStream("AppLogo.jpg");
        Image logo = new Image(input);
        ImageView logoView = new ImageView(logo);
        //End BackGround

        //narcissism and signature
        Text text = new Text();
        text.setText("The Best App you've had!");
        text.setY(780);
        text.setX(10);
        text.setFont(Font.font("Calibri",FontWeight.BLACK,FontPosture.ITALIC,20));
        text.setFill(Color.RED);
        text.setStroke(Color.BLACK);
        text.setUnderline(true);
        BoxBlur b = new BoxBlur();
        b.setHeight(5);
        b.setWidth(2);
        b.setIterations(1);
        text.setEffect(b); //end signature

        // Progress Bar For Connecting to the Database
        // and Opening Docker Desktop
        /*
        ProgressIndicator connectionToDatabase=new ProgressIndicator();
        connectionToDatabase.setProgress(50);

         */

        /*
        ScrollBar s = new ScrollBar();
        s.setMin(0);
        s.setMax(800);
        s.setValue(110);
        s.setOrientation(Orientation.VERTICAL);
        s.setUnitIncrement(12);
        s.setBlockIncrement(10);

         */

        Group root = new Group();
        root.getChildren().add(logoView); //Add Background
        root.getChildren().add(buttonStart); //adding start button
        root.getChildren().add(buttonExit); //adding exit button
        root.getChildren().add(title); //adding title
        root.getChildren().add(text); //adding signature
        //root.getChildren().add(connectionToDatabase);
        //root.getChildren().add(s);

        Scene sceneLobby = new Scene(root, 800, 800);
        primaryStage.setScene(sceneLobby);
        primaryStage.setTitle("Docker Project");
        primaryStage.show();

    }
    public static void main(String[] args) throws Exception {
        //ClientUpdater.connectionAccomplished();

        launch(args);
        Connection connection = ClientUpdater.connectToDatabase();

    }
}