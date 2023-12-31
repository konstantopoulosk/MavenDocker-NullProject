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
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ResourceBundle;

public class MainSceneController implements Initializable {
    static final Connection connection = ClientUpdater.connectToDatabase();
     Stage stage;
     Scene scene;
     Parent root;
     static boolean containerFlag = true, imageFlag = true,
             networkFlag = true, volumeFlag = true;
     static boolean isPressed = false;
     static boolean startContainerFlag = true, stopContainerFlag = true;
     public void changeTheScenes(String fxmlFile, ActionEvent event) throws IOException {
         root = FXMLLoader.load(getClass().getResource(fxmlFile));
         stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
         scene = new Scene(root);
         stage.setScene(scene);
         stage.show();
     }

    @FXML
    public void startApp(ActionEvent event) throws IOException  {
        //Goes from Scene1 to Scene2.
        changeTheScenes("/Scene2.fxml", event);
    }
    @FXML
    public void exitApp(ActionEvent event) {
        //Exiting Application.
        System.exit(0);
    }
    @FXML
    public void pressImages(ActionEvent event) throws IOException {
        //Goes to Images Menu.
        changeTheScenes("/images.fxml", event);
    }
    @FXML
    public void tapToListImages(ActionEvent event) throws IOException {
        setListImages();
    }
    @FXML
    public void pressContainers(ActionEvent event) throws IOException {
        //Goes to Containers Menu.
        changeTheScenes("/containers.fxml", event);
    }
    @FXML
    public void tapToListContainers(ActionEvent event) throws IOException {
        setListContainers();
    }
    @FXML
    public void pressNetworks(ActionEvent event) throws IOException {
        //Goes to Networks Menu.
        changeTheScenes("/networks.fxml", event);
    }
    @FXML
    public void pressVolumes(ActionEvent event) throws IOException {
        //Goes to Volumes Menu.
        changeTheScenes("/volumes.fxml", event);
    }
    @FXML
    public void pressStart(ActionEvent event) throws IOException {
        changeTheScenes("/startContainer.fxml", event);
    }
    @FXML
    public void tapToSeeExitedContainers(ActionEvent event) throws IOException {
        setListExitedContainers();
    }
    @FXML
    public void startContainer(ActionEvent event) throws IOException {
        System.out.println("User selected container to start");
        System.out.println("Connect this with the Executor Thread!");
    }
    @FXML
    public void pressStop(ActionEvent event) throws IOException {
        System.out.println("User pressed Stop Container");
        changeTheScenes("/stopContainer.fxml", event);
    }
    @FXML
    public void stopContainer(ActionEvent event) throws IOException {
        System.out.println("User selected to Stop container");
        System.out.println("Connect this with Executor Thread");
    }
    @FXML
    public void tapToSeeActiveContainers(ActionEvent event) throws IOException {
        System.out.println("User pressed");
        setListActiveContainers();
    }
    @FXML
    public void pressRename(ActionEvent event) throws IOException {
        System.out.println("User pressed Rename Container");
        changeTheScenes("/renameContainer.fxml", event);
    }
    @FXML
    public void renameContainer(ActionEvent event) throws IOException {
        System.out.println("User pressed Rename Container");
        System.out.println("Connect this with Executor Thread.");
    }
    @FXML
    public void pressRemove(ActionEvent event) throws IOException {
        System.out.println("User pressed Remove Container");
        changeTheScenes("/removeContainer.fxml", event);
    }
    @FXML
    public void removeContainer(ActionEvent event) throws IOException {
         System.out.println("User pressed Remove Container");
         System.out.println("Connect with Executor Thread");
    }
    @FXML
    public void pressRestart(ActionEvent event) throws IOException {
        System.out.println("User pressed Restart Container");
    }
    @FXML
    public void pressPause(ActionEvent event) throws IOException {
        System.out.println("User pressed Pause Container");
        changeTheScenes("/pauseContainer.fxml", event);
    }
    @FXML
    public void pauseContainer(ActionEvent event) throws IOException {
         System.out.println("User pressed Pause container");
         System.out.println("Connect with executor thread.");
    }
    @FXML
    public void tapToSeePausedContainers(ActionEvent event) throws IOException {
         setListPauseContainers();
    }
    @FXML
    public void pressUnpause(ActionEvent event) throws IOException {
        System.out.println("User pressed Unpause Container");
    }
    @FXML
    public void pressKill(ActionEvent event) throws IOException {
        System.out.println("User pressed Kill Container");
        changeTheScenes("/killContainer.fxml", event);
    }
    @FXML
    public void killContainer(ActionEvent event) throws IOException {
         System.out.println("User Pressed KIll COntainer");
         System.out.println("Connect with Executor Thread");
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
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        if (!isPressed) {
            System.out.println("The first time");
            isPressed = true;
            DockerMonitor monitor = new DockerMonitor();
            monitor.start();
            DatabaseThread databaseThread = new DatabaseThread(connection);
            databaseThread.start();
        }
        try {
            if (containerFlag) {
                //setListContainers();
            }
            if (imageFlag) {
                //setListImages();
            }
            if (volumeFlag) {
                System.out.println("You are in Volume Scene.");
                //todo.
                volumeFlag = false;
            }
            if (networkFlag) {
                System.out.println("You are in Network Scene.");
                //todo.
                networkFlag = false;
            }
            if (startContainerFlag) {
                //User selected to start container.
                setListExitedContainers();
                startContainerFlag = false;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private final ObservableList<String> containers = FXCollections.observableArrayList("Name", "Image", "State");
    @FXML
    private ListView<String> containersList = new ListView<>(containers);
    public void setListContainers() {
        try {
            System.out.println("You are in Container Scene.");
            String queryContainers = "select name, image, state from dockerinstance";
            Statement statement = connection.createStatement();
            ResultSet containersOutput = statement.executeQuery(queryContainers);
            int i = 0;
            while (containersOutput.next()) {
                i++;
                String name, image, state;
                name = containersOutput.getString("name");
                image = containersOutput.getString("image");
                state = containersOutput.getString("state");
                String containersOut = i + ", " + name + ", " + image + ", " + state + " ";
                containersList.getItems().add(containersOut);
            }
            if (i == 0) {
                containersList.getItems().removeAll();
                ObservableList<String> containers1 = FXCollections.observableArrayList("No Containers");
                containersList = new ListView<>(containers1);
                containersList.getItems().add("Nothing to Show Here");
            } else {
                containersList.getItems().remove(containers);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        containerFlag = false;
    }
    private final ObservableList<String> images = FXCollections.observableArrayList("Repository",
            "Tag", "Times Used", "Size");
    @FXML
    private ListView<String> imagesList = new ListView<>(images);
    public void setListImages() {
        try {
            System.out.println("You are in Image Scene.");
            String queryImages = "select repository, tag, timesUsed, size from dockerimage";
            Statement statement1 = connection.createStatement();
            ResultSet imagesOutput = statement1.executeQuery(queryImages);
            int i = 0;
            while (imagesOutput.next()) {
                i++;
                String repository, tag, timesUsed, size;
                repository = imagesOutput.getString("repository");
                tag = imagesOutput.getString("tag");
                timesUsed = imagesOutput.getString("timesUsed");
                size = imagesOutput.getString("size");
                String imagesOut = i + ", " + repository + ", " + tag + ", "
                        + timesUsed + ", " + size + " ";
                imagesList.getItems().add(imagesOut);
            }
            if (i == 0) {
                imagesList.getItems().removeAll();
                ObservableList<String> images1 = FXCollections.observableArrayList("No Images");
                imagesList = new ListView<>(images1);
                imagesList.getItems().add("Nothing to Show Here :(");
            } else {
                imagesList.getItems().remove(images);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        imageFlag = false;
    }
    private final ObservableList<String> exitedContainersINIT = FXCollections.observableArrayList("name");
    @FXML
    private ListView<String> exitedContainers = new ListView<>(exitedContainersINIT);
    public void setListExitedContainers() {
        try {
            String exited = "exited";
            String queryExited = "select name from dockerinstance where state = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(queryExited);
            preparedStatement.setString(1, exited);
            ResultSet resultSet = preparedStatement.executeQuery();
            int i = 0;
            while (resultSet.next()) {
                i++;
                String name = resultSet.getString("name");
                String listOut = i + ", " + name + " ";
                exitedContainers.getItems().add(listOut);
            }
            if (i == 0) {
                exitedContainers.getItems().removeAll();
                ObservableList<String> exitedContainersINIT1  = FXCollections.observableArrayList("No Exited Containers!");
                exitedContainers = new ListView<>(exitedContainersINIT1);
                exitedContainers.getItems().add("Nothing to Show Here :(");
            } else {
                exitedContainers.getItems().remove(exitedContainersINIT);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        startContainerFlag = true;
    }
    private final ObservableList<String> actives = FXCollections.observableArrayList("name");
    @FXML
    private ListView<String> activeContainers = new ListView<>(actives);
    public void setListActiveContainers() {
        try {
            String state = "running";
            String queryExited = "select name from dockerinstance where state = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(queryExited);
            preparedStatement.setString(1, state);
            ResultSet resultSet = preparedStatement.executeQuery();
            int i = 0;
            while (resultSet.next()) {
                i++;
                String name = resultSet.getString("name");
                String listOut = i + ", " + name + " ";
                activeContainers.getItems().add(listOut);
            }
            if (i == 0) {
                activeContainers.getItems().removeAll();
                ObservableList<String> actives1 = FXCollections.observableArrayList("No Active Containers");
                activeContainers = new ListView<>(actives1);
                activeContainers.getItems().add("Nothing to Show Here :(");
            } else {
                activeContainers.getItems().remove(actives);
            }
            stopContainerFlag = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private ObservableList<String> pause = FXCollections.observableArrayList("name1");
    @FXML
    private ListView<String> pauseContainers = new ListView<>(pause);
    public void setListPauseContainers() {
        setListContainers();
        if (containersList.getItems().size() > 1) {
            int j = 0, i;
            for (i = 0; i < containersList.getItems().size(); i++) {
                String containerInfo = containersList.getItems().get(i);
                String[] containerInfoArr = containerInfo.split(",");
                String state = containerInfoArr[0];
                if (!state.endsWith("(Paused)")) {
                    j++;
                    pauseContainers.getItems().add(containerInfoArr[0]);
                }
            }
            if (i == 0 || j == 0) {
                pauseContainers.getItems().removeAll();
                ObservableList<String> pause1 = FXCollections.observableArrayList("No Paused Containers");
                pauseContainers = new ListView<>(pause1);
                pauseContainers.getItems().add("Nothing to Show Here :(");
            } else {
                pauseContainers.getItems().remove(pause);
            }
        } else {
            pauseContainers.getItems().removeAll();
            ObservableList<String> pause1 = FXCollections.observableArrayList("No Paused Containers");
            pauseContainers = new ListView<>(pause1);
            pauseContainers.getItems().add("Nothing to Show Here :(");
        }
    }
}
