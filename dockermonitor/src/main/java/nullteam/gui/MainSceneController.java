package nullteam.gui;

import com.nullteam.ClientUpdater;
import com.nullteam.DatabaseThread;
import com.nullteam.DockerMonitor;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ListView;
import javafx.stage.Stage;
import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ResourceBundle;

public class MainSceneController implements Initializable {
     Stage stage;
     Scene scene;
     Parent root;

    @FXML
    public void startApp(ActionEvent event) throws IOException  {
        //Goes from Scene1 to Scene2.
        root = FXMLLoader.load(getClass().getResource("/Scene2.fxml"));
        stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }
    @FXML
    public void exitApp(ActionEvent event) {
        //Exiting Application.
        System.exit(0);
    }
    @FXML
    public void pressImages(ActionEvent event) throws IOException {
        //Goes to Images Menu.
        root = FXMLLoader.load(getClass().getResource("/images.fxml"));
        stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    @FXML
    public void pressContainers(ActionEvent event) throws IOException {
        //Goes to Containers Menu.
        root = FXMLLoader.load(getClass().getResource("/containers.fxml"));
        stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }
    @FXML
    public void pressNetworks(ActionEvent event) throws IOException {
        //Goes to Networks Menu.
        root = FXMLLoader.load(getClass().getResource("/networks.fxml"));
        stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }
    @FXML
    public void pressVolumes(ActionEvent event) throws IOException {
        //Goes to Volumes Menu.
        root = FXMLLoader.load(getClass().getResource("/volumes.fxml"));
        stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }
    @FXML
    public void pressStart(ActionEvent event) throws IOException {
        System.out.println("User pressed Start Container");
    }
    @FXML
    public void pressStop(ActionEvent event) throws IOException {
        System.out.println("User pressed Stop Container");
    }
    @FXML
    public void pressRename(ActionEvent event) throws IOException {
        System.out.println("User pressed Rename Container");
    }
    @FXML
    public void pressRemove(ActionEvent event) throws IOException {
        System.out.println("User pressed Remove Container");
    }
    @FXML
    public void pressRestart(ActionEvent event) throws IOException {
        System.out.println("User pressed Restart Container");
    }
    @FXML
    public void pressPause(ActionEvent event) throws IOException {
        System.out.println("User pressed Pause Container");
    }
    @FXML
    public void pressUnpause(ActionEvent event) throws IOException {
        System.out.println("User pressed Unpause Container");
    }
    @FXML
    public void pressKill(ActionEvent event) throws IOException {
        System.out.println("User pressed Kill Container");
    }
    @FXML
    public void pressLogs(ActionEvent event) throws IOException {
        System.out.println("User pressed Logs of a Container");
    }
    @FXML
    public void pressSubnets(ActionEvent event) throws IOException {
        System.out.println("User pressed Subnets of a Container");
    }
    @FXML
    public void pressPull(ActionEvent event) throws IOException {
        System.out.println("User pressed Pull Image");
    }
    @FXML
    public void pressImplement(ActionEvent event) throws IOException {
        System.out.println("User pressed implement image");
    }
    @FXML
    public void pressRemoveImage(ActionEvent event) throws IOException {
        System.out.println("User pressed Remove an image");
    }
    private final ObservableList<String> containers = FXCollections.observableArrayList("Name", "Image", "State");
    @FXML
    private ListView<String> containersList = new ListView<>(containers);
    private final ObservableList<String> images = FXCollections.observableArrayList("Repository",
            "Tag", "Times Used", "Size");
    @FXML
    private ListView<String> imagesList = new ListView<>(images);

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        DockerMonitor monitor = new DockerMonitor();
        monitor.start();
        Database database = new Database();
        Connection connection = database.getConnection();
        DatabaseThread databaseThread = new DatabaseThread(connection);
        databaseThread.start();
        String queryContainers = "select name, image, state from dockerinstance";
        String queryImages = "select repository, tag, timesUsed, size from dockerimage";
        try {
            Statement statement = connection.createStatement();
            ResultSet containersOutput = statement.executeQuery(queryContainers);
            while (containersOutput.next()) {
                String name, image, state;
                name = containersOutput.getString("name");
                image = containersOutput.getString("image");
                state = containersOutput.getString("state");
                String containersOut = name + " \"" + image + " \"" + state + " \"";
                containersList.getItems().add(containersOut);
            }
            Statement statement1 = connection.createStatement();
            ResultSet imagesOutput = statement1.executeQuery(queryImages);
            while (imagesOutput.next()) {
                String repository, tag, timesUsed, size;
                repository = imagesOutput.getString("repository");
                tag = imagesOutput.getString("tag");
                timesUsed = imagesOutput.getString("timesUsed");
                size = imagesOutput.getString("size");
                String imagesOut = repository + " \"" + tag + " \""
                        + timesUsed + " \"" + size + " \"";
                imagesList.getItems().add(imagesOut);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
