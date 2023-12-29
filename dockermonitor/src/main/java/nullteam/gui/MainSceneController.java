package nullteam.gui;

import com.nullteam.DockerInstance;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

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
    public void pressImages(ActionEvent event) throws IOException {
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
    /*
    @FXML
    private Label nameLabel, imageLabel, statusLabel;
    private List<DockerInstance> containers = DockerInstance.containerslist;

    public void initialize(URL location, ResourceBundle resources) {
        DockerInstance.listAllContainers(); // Update the status of all containers
        updateLabels();
    }

    private void updateLabels() {
        nameLabel.setText("");
        imageLabel.setText("");
        statusLabel.setText("");

        int i = 0;
        for (DockerInstance container : containers) {
            i++;
            nameLabel.setText(nameLabel.getText() + i + ") " + container.getContainerName() + "\n");
            imageLabel.setText(imageLabel.getText() + i + ") " + container.getContainerImage() + "\n");
            statusLabel.setText(statusLabel.getText() + i + ") " + container.getContainerStatus() + "\n");
        }
    }
    @FXML
//creating a choicebox so the user can choose what tool they want to use
    private ChoiceBox<String> Actions;

    public void ContainerClicked(ActionEvent event) throws IOException {
        handleAction(event);
    }


    private void handleAction(ActionEvent event) {
        String selectedAction = Actions.getValue();
        // Perform actions based on the selected choice
        switch (selectedAction) {
            case "Start":
                // Handle start action
                break;
            case "Stop":
                // Handle stop action
                break;
            case "Pause":
                // Handle pause action
                break;
            case "Unpause":
                // Handle unpause action
                break;
            case "Restart":
                //Handle Restart action
                break;
            case "Rename":
                // Handle Rename action
                break;
            case "Remove":
                // Handle Remove action
                break;
            case "Kill":
                // Handle Kill action
                break;
        }
    }
    */
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
}
