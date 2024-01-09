package nullteam.gui;

import com.github.dockerjava.api.model.Container;
import com.google.gson.Gson;
import com.nullteam.ActionRequest;
import com.nullteam.*;
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
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

public class MainSceneController implements Initializable {
    private final ObservableList<String> observableList = FXCollections.observableArrayList(" ");
    @FXML
    private ListView<String> containersList = new ListView<>(observableList); //List View to see All Containers
    @FXML
    private ListView<String> activeContainers = new ListView<>(observableList); //List View to see Active Containers
    @FXML
    private ListView<String> exitedContainers = new ListView<>(observableList); //List View to see Exited Containers
    @FXML
    private ListView<String> restartListContainer = new ListView<>(observableList); //List View to see PAUSED Containers
    @FXML
    private ListView<String> imagesList = new ListView<>(observableList); //List View to see Client's Image
    @FXML
    private ListView<String> imagesInUse = new ListView<>(observableList); //List View to see Images that are IN-USE
    @FXML
    private ListView<String> volumesList = new ListView<>(observableList); //List View to see Client's Volumes
    @FXML
    private ListView<String> networksList = new ListView<>(observableList); //List View to see Client's Networks (of containers)
    //private List<DockerImage> dockerImagesStart;
    //private List<DockerImage> dockerImageNow;
    @FXML
    private ListView<String> subnetsList = new ListView<>(observableList); //List View to see Subnets of an active Container
    @FXML
    private ListView<String> logsList = new ListView<>(observableList); //List View to see Logs of an active Container
    @FXML
    private TextField imageToPull; //Variable to get the text user types in order to pull the image.
    private String containerId; //field for container id, user presses an item in List View and the container id is here
    private String imageId; //field for image id, user presses in list view an item and image id is here
    static final Connection connection = ClientUpdater.connectToDatabase(); //Variable to store the connection
    final String ip = ClientUpdater.getIp(); //variable to get the System Ip of a User.
     Stage stage; //Stage to show
     Scene scene; //Current scene
     Parent root;
     static boolean isPressed = false; //This is used to only execute the initialize once.
    private static final String API_URL = "http://localhost:8080/api/perform-action"; //URL for API.
    Gson gson = new Gson();
    //Method to send the Request
    private void sendActionRequest(ActionRequest actionRequest, Gson gson) {
        try {
            // Serialize the ActionRequest to JSON
            String jsonPayload = gson.toJson(actionRequest);
            // Send the request to the API
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(API_URL))
                    .header("Content-Type", "application/json")
                    .timeout(Duration.ofSeconds(10)) // Set a timeout value in seconds
                    .POST(HttpRequest.BodyPublishers.ofString(jsonPayload))
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            // Check the response status
            if (response.statusCode() == 200) {
                System.out.println("Action request successful");
            } else {
                System.out.println("Error: " + response.statusCode() + ", " + response.body());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    //o parakatw kwdikas afora thn ekkinhsh tou server opou 8a phgainoun ta requests
    private void startHttpServer(PerformActionHandler handler) {
        try {
            // Create an HTTP server on the specified port
            com.sun.net.httpserver.HttpServer server =
                    com.sun.net.httpserver.HttpServer.create(new java.net.InetSocketAddress(8080), 0);

            // Create a context for the API path and set the handler
            server.createContext("/api/perform-action", handler);

            // Use a fixed thread pool to handle incoming requests
            server.setExecutor(Executors.newFixedThreadPool(10));

            // Start the server
            server.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    //API CONFIGURATION
    //Initialize Method is necessary
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        if (!isPressed) {
            GetHelp.listImage(); //Creating objects of DockerImage class to use DockerImage.imagesList later
            GetHelp.listVolumes();//Creating objects of DockerVolume class to use DockerVolume.volumesList later
            GetHelp.listNetworks();//Creating objects of DockerNetwork class to use DockerNetwork.networksList later
            isPressed = true; // flag is now true -> initialize does not execute
            DockerMonitor monitor = new DockerMonitor();
            monitor.start(); //starting Monitor Thread
            BlockingQueue<ActionRequest> actionQueue = new LinkedBlockingQueue<>();
            PerformActionHandler performActionHandler = new PerformActionHandler(actionQueue, gson);
            ExecutorThread executorThread = new ExecutorThread(actionQueue);
            executorThread.start(); //Starting Executor Thread
            startHttpServer(performActionHandler);
            databaseThread(); //Executing / Running Database Thread (once)
        }
    }

    /**
     * This method runs the Database Thread.
     */
    public void databaseThread() {
        DatabaseThread databaseThread = new DatabaseThread(connection);
        databaseThread.run();
        try {
            databaseThread.join();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * This method changes the scenes.
     * @param fxmlFile String
     * @param event ActionEvent
     * @throws IOException
     */
     public void changeTheScenes(String fxmlFile, ActionEvent event) throws IOException {
         root = FXMLLoader.load(getClass().getResource(fxmlFile));
         stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
         scene = new Scene(root);
         stage.setScene(scene);
         stage.show();
     }

    /**
     * This method opens a new window when necessary.
     * @param event ActionEvent
     * @param fxml String
     * @param title String
     * @throws IOException
     */
    @FXML
    public void openNewWindow(ActionEvent event, String fxml, String title) throws IOException {
        Parent root1 = FXMLLoader.load(getClass().getClassLoader().getResource(fxml));
        Stage stage = new Stage();
        stage.setTitle(title);
        stage.setScene(new Scene(root1, 600,400));
        stage.show();
    }

    /**
     * This method opens a confirmation window.
     * @param event ActionEvent
     * @param title String
     * @param fxml String
     * @throws IOException
     */
    @FXML
    public void openConfirmationWindow(ActionEvent event, String title, String fxml) throws IOException {
        Parent root1 = FXMLLoader.load(getClass().getClassLoader().getResource(fxml));
        Stage stage = new Stage();
        stage.setTitle(title);
        stage.setScene(new Scene(root1, 300, 150));
        stage.show();
    }
    //This method closes any new Window when user clicks on close
    @FXML
    public void closeNewWindow(ActionEvent event) throws IOException {
        final Node source = (Node) event.getSource();
        final Stage stage = (Stage) source.getScene().getWindow();
        stage.close();
    }
    //This method starts the Application -> changes the scene from scene1 to scene2
    @FXML
    public void startApp(ActionEvent event) throws IOException  {
        //Goes from Scene1 to Scene2.
        changeTheScenes("/Scene2.fxml", event);
    }
    //This method is used to exit the application
    @FXML
    public void exitApp(ActionEvent event) {
        System.exit(0);
    }
    //This Method is used to go to Images Menu.
    @FXML
    public void pressImages(ActionEvent event) throws IOException {
        changeTheScenes("/images.fxml", event);
    }
    //This is used when User Clicks on Button to See His images
    @FXML
    public void tapToListImages(ActionEvent event) throws IOException {
        setListImages();
    }
    //This is used to go to Containers Menu
    @FXML
    public void pressContainers(ActionEvent event) throws IOException {
        changeTheScenes("/containers.fxml", event);
    }
    //This is used when User Clicks on Button to see All Containers.
    @FXML
    public void tapToListContainers(ActionEvent event) throws IOException {
        setListContainers();
        containersList = new ListView<>(observableList);
        databaseThread();
    }
    //This is used to go to Networks Menu
    @FXML
    public void pressNetworks(ActionEvent event) throws IOException {
        changeTheScenes("/networks.fxml", event);
    }
    //This is used when user clicks on button to see his networks
    @FXML
    public void tapToSeeYourNetworks(ActionEvent event) throws IOException {
        setListNetworks();
        networksList = new ListView<>(observableList);
    }
    //This is used to go to Volumes Menu
    @FXML
    public void pressVolumes(ActionEvent event) throws IOException {
        changeTheScenes("/volumes.fxml", event);
    }
    //This is used when User clicks on Button to see his Volumes
    @FXML
    public void tapToSeeVolumes(ActionEvent event) throws IOException {
        setListVolumes();
        volumesList = new ListView<>(observableList);
    }
    //This travels the User to a new Scene where he can see the exited containers and choose one to start
    @FXML
    public void pressStart(ActionEvent event) throws IOException {
        changeTheScenes("/startContainerNew.fxml", event);
    }
    //When user clicks on Button to see Exited Containers
    @FXML
    public void tapToSeeExitedContainers(ActionEvent event) throws IOException {
        setListExitedContainers();
    }
    //This is the method is executed when user presses apply to start a container that chose in List View
    @FXML
    public void startContainer(ActionEvent event) throws IOException {
        if (containerId != null) { //It may be null.
            ActionRequest actionRequest = new ActionRequest("START", containerId);
            CompletableFuture.runAsync(() -> sendActionRequest(actionRequest, gson))
                    .thenRun(() -> System.out.println("Request sent successfully"))
                    .exceptionally(throwable -> {
                        throwable.printStackTrace();
                        return null;
                    });
            openConfirmationWindow(event, "Starting Container Properties", "startContainerConfirmation.fxml");
            databaseThread();
            exitedContainers = new ListView<>(observableList); //Initialize this again so Listing all Over again
        } //and not below.
    }
    //This travels the User to a new Scene where he can see the active containers and choose one to stop
    @FXML
    public void pressStop(ActionEvent event) throws IOException {
        changeTheScenes("/stopContainerNew.fxml", event);
    }
    //This method is executed when user presses apply to stop a container
    @FXML
    public void stopContainer(ActionEvent event) throws IOException {
        if (containerId != null) {
            ActionRequest actionRequest = new ActionRequest("STOP", containerId);
            // Send the request to the API
            CompletableFuture.runAsync(() -> sendActionRequest(actionRequest, gson))
                    .thenRun(() -> System.out.println("Request sent successfully"))
                    .exceptionally(throwable -> {
                        throwable.printStackTrace();
                        return null;
                    });
            openConfirmationWindow(event, "Stop Container Properties", "stopContainerConfirmation.fxml");
            databaseThread();
        }
        activeContainers = new ListView<>(observableList);
    }
    //This is executed when user presses button to see his active containers in stop container scene.
    @FXML
    public void tapToSeeActiveContainers(ActionEvent event) throws IOException {
        setListActiveContainers();
    }
    //Travels the User to new scene that has a List View and a Text Field for the New Name.
    @FXML
    public void pressRename(ActionEvent event) throws IOException {
        changeTheScenes("/renameContainerNew.fxml", event);
    }
    //This is Executed when user presses apply to rename a container that he chose from List View
    @FXML
    public void renameContainer(ActionEvent event) throws IOException {
        //todo: Executor.
        if (containerId != null) {
            openConfirmationWindow(event, "Rename Container Properties", "renameContainerConfirmation.fxml");
            databaseThread();
        }
        containersList = new ListView<>(observableList);
    }
    //This travels the user to new Scene where user sees his containers and chooses one to remove
    @FXML
    public void pressRemove(ActionEvent event) throws IOException {
        changeTheScenes("/removeContainerNew.fxml", event);
    }
    //This is executed when user presses apply to remove a container
    @FXML
    public void removeContainer(ActionEvent event) throws IOException {
        //todo: Executor.
        if (containerId != null) {
            openConfirmationWindow(event, "Remove Container Properties", "removeContainerConfirmation.fxml");
            databaseThread();
        }
        containersList = new ListView<>(observableList);
    }
    //This travels the user to a new Scene with a List View with the active containers and User chooses one to restart
    @FXML
    public void pressRestart(ActionEvent event) throws IOException {
        changeTheScenes("/restartContainerNew.fxml", event);
    }
    //Executed when user presses apply to Restart a Container.
    @FXML
    public void restartContainer(ActionEvent event) throws IOException {
        //todo: Executor.
        if (containerId != null) {
            openConfirmationWindow(event, "Restart Container Properties", "restartContainerConfirmation.fxml");
            databaseThread();
        }
        activeContainers = new ListView<>(observableList);
    }
    //This is executed when User presses Button to see PAUSED CONTAINERS TO UNPAUSE IT.
    @FXML
    public void tapToSeeRestartContainers(ActionEvent event) throws IOException {
        setListRestartContainers();
    }
    //This travels the user to a new Scene with a List View of active containers and chooses one to pause it
    @FXML
    public void pressPause(ActionEvent event) throws IOException {
        changeTheScenes("/pauseContainerNew.fxml", event);
    }
    //This is executed when user presses apply to pause a container
    @FXML
    public void pauseContainer(ActionEvent event) throws IOException {
        //todo: Executor.
        if (containerId != null) {
            openConfirmationWindow(event, "Pause Container Properties", "pauseContainerConfirmation.fxml");
            databaseThread();
        }
        restartListContainer = new ListView<>(observableList);
    }
    //This travels the user to a new Scene with a List View of PAUSED containers
    @FXML
    public void pressUnpause(ActionEvent event) throws IOException {
        changeTheScenes("/unpauseContainerNew.fxml", event);
    }
    //This is executed when user presses apply to Unpause a container
    @FXML
    public void unpauseContainer(ActionEvent event) throws IOException {
        //todo: Executor.
        if (containerId != null) {
            openConfirmationWindow(event, "Unpause Container Properties", "unpauseContainerConfirmation.fxml");
            databaseThread();
        }
        restartListContainer = new ListView<>(observableList); //THIS SHOWS PAUSED CONTAINERS!!!!
    }
    //Travels the user to a new Scene with a List View of all containers
    @FXML
    public void pressKill(ActionEvent event) throws IOException {
        changeTheScenes("/killContainerNew.fxml", event);
    }
    //This is executed when User presses apply to kill a container
    @FXML
    public void killContainer(ActionEvent event) throws IOException {
        //todo: Executor.
        if (containerId != null) {
            openConfirmationWindow(event, "Kill Container Properties", "killContainerConfirmation.fxml");
            databaseThread();
        }
        containersList = new ListView<>(observableList);
    }
    //This travels the User to a new Scene with a List View of active containers.
    @FXML
    public void pressLogs(ActionEvent event) throws IOException {
        changeTheScenes("/logsOfContainerNew.fxml", event);
    }
    //This is executed when user presses apply to see the Logs of a container that he chose
    @FXML
    public void applyToSeeTheLogs(ActionEvent event) throws IOException {
        //todo: Executor.
        if (containerId != null) {

            openNewWindow(event, "listOfLogsNew.fxml", "List of Logs"); //Opens a new Window with a List View of the Logs
        }
        activeContainers = new ListView<>(observableList);
    }
    //This is Executed in the new window when user presses Button to see the logs of container
    @FXML
    public void tapToSeeTheLogs(ActionEvent event) throws IOException {
        setListLogs();
        logsList = new ListView<>(observableList);
    }
    //Travels the user to a new Scene with a List View of active containers
    @FXML
    public void pressSubnets(ActionEvent event) throws IOException {
        changeTheScenes("/subnetsOfContainerNew.fxml", event);
    }
    //This is executed when user presses apply to see the subnets of a container
    @FXML
    public void applyToSeeSubnets(ActionEvent event) throws IOException {
        //todo: Executor.
        if (containerId != null) {
            openNewWindow(event, "listOfSubnetsNew.fxml", "List of Subnets"); //Opens a new window to show the subnets
        }
        activeContainers = new ListView<>(observableList);
    }
    //THis is executed when user presses on new window button to see the subnets
    @FXML
    public void tapToSeeSubnets() {
        setListSubnets();
        subnetsList = new ListView<>(observableList);
    }
    //Travels the user to a new scene with a List View of current images and a text field
    //for the user to write the image he wants to pull
    @FXML
    public void pressPull(ActionEvent event) throws IOException {
        changeTheScenes("/imagePullNew.fxml", event);
    }
    //This is Executed when user presses apply to pull
    @FXML
    public void applyPull(ActionEvent event) throws IOException {
        if (imageToPull != null && !DockerImage.imageslist.contains(imageId)) {
            //todo: Executor PULL IMAGE.
            String image = imageToPull.getText(); //This is what User wrote he wants to pull.
            System.out.println(image);
            openConfirmationWindow(event, "Pull Image Properties", "imagePullConfirmation.fxml");
        } else {
            System.out.println("Something Is Wrong!");
        }
        imagesList = new ListView<>(observableList);
    }
    //Travels the User to a new Scene with a List View of current images
    @FXML
    public void pressImplement(ActionEvent event) throws IOException {
        changeTheScenes("/imageImplementNew.fxml", event);
    }
    //This is executed when user presses apply to implement an image -> Start a new Container
    @FXML
    public void applyImplement(ActionEvent event) throws IOException {
         //todo: Executor
        System.out.println(imageId);
        if (imageId != null) {
            openConfirmationWindow(event, "Implement Image Properties", "imageImplementConfirmation.fxml");
            databaseThread();
        }
        imagesList = new ListView<>(observableList);
    }
    //Travels the user to a new Scene with a List View of the images
    @FXML
    public void pressRemoveImage(ActionEvent event) throws IOException {
        changeTheScenes("/imageRemoveNew.fxml", event);
    }
    //This is executed when user presses apply to remove an image
    @FXML
    public void applyRemove(ActionEvent event) throws IOException {
         //todo: Executor.
        if (imageId != null) {
            openConfirmationWindow(event, "Remove Image Properties", "imageRemoveConfirmation.fxml");
            databaseThread();
        }
        imagesList = new ListView<>(observableList);
    }
    //Travels the user to a new Scene to see a list view with only IN-USE images
    @FXML
    public void changeToSeeAnotherList(ActionEvent event) throws IOException {
        changeTheScenes("/seeImagesInUse.fxml", event);
    }
    //This is executed when user presses on button to see in use images
    @FXML
    public void tapToSeeImagesInUse(ActionEvent event) {
        setImagesInUse();
        imagesInUse = new ListView<>(observableList);
    }
    //This method sets the field images in use
    public void setImagesInUse() {
        List<String> usedImages = DockerImage.listUsedImages();
        for (String usedImage : usedImages) {
            imagesInUse.getItems().add(usedImage);
        }
        if (imagesInUse.getItems().size() > 1) {
            imagesInUse.getItems().remove(observableList);
        } else {
            imagesInUse.getItems().add("Nothing to Show Here");
        }
    }
    //This Method sets the field containersList.
    public void setListContainers() {
        try {
            String queryContainers = "SELECT containerId, name, image, state FROM containers WHERE SystemIp = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(queryContainers);
            preparedStatement.setString(1, ip);
            ResultSet containersOutput = preparedStatement.executeQuery();
            int i = 0;
            while (containersOutput.next()) {
                i++;
                String name, image, state, containerId;
                name = containersOutput.getString("name");
                image = containersOutput.getString("image");
                state = containersOutput.getString("state");
                containerId = containersOutput.getString("containerId");
                String containersOut = i + ") Name: " + name + "  Image: " + image + "  State: " + state
                        + "  Container ID ->" + containerId;
                containersList.getItems().add(containersOut);
            }
            if (containersList.getItems().size() == 1) {
                containersList.getItems().add("Nothing to Show Here");
            } else {
                containersList.getItems().remove(observableList);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    //This Method sets the field imagesList.
    public void setListImages() {
        int num = 0; //Numbers to make the output more User Friendly
        for (DockerImage img : DockerImage.imageslist) {
            num++;
            imagesList.getItems().add(num + ") " + img.toString());
        }
        if (imagesList.getItems().size() > 1) {
            imagesList.getItems().remove(observableList);
        } else {
            imagesList.getItems().add("Nothing to Show Here");
        }
    }
    //This Method sets the field exitedContainers.
    public void setListExitedContainers() {
        try {
            String queryExited = "select name, containerId from containers where state = ? and SystemIp = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(queryExited);
            preparedStatement.setString(1, "exited");
            preparedStatement.setString(2, ip);
            ResultSet resultSet = preparedStatement.executeQuery();
            int i = 0;
            while (resultSet.next()) {
                i++;
                String name = resultSet.getString("name");
                String containerId = resultSet.getString("containerId");
                String listOut = i + ") Name: " + name + "  Container ID ->" + containerId;
                exitedContainers.getItems().add(listOut);
            }
            if (i != 0) {
                exitedContainers.getItems().remove(observableList);
            } else {
                exitedContainers.getItems().add("Nothing to Show Here");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    //This Method sets the field activeContainers.
    public void setListActiveContainers() {
        try {
            String queryExited = "select name, containerId from containers where state = ? and SystemIp = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(queryExited);
            preparedStatement.setString(1, "running");
            preparedStatement.setString(2, ip);
            ResultSet resultSet = preparedStatement.executeQuery();
            int i = 0;
            while (resultSet.next()) {
                i++;
                String name = resultSet.getString("name");
                String containerId = resultSet.getString("containerId");
                String listOut = i + ") Name: " + name + "  Container ID ->" + containerId;
                activeContainers.getItems().add(listOut);
            }
            if (i != 0) {
                activeContainers.getItems().remove(observableList);
            } else {
                activeContainers.getItems().add("Nothing to Show Here");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    //This Method sets the field restartListContainers -> PAUSED CONTAINERS!!!!
    public void setListRestartContainers() {
        try {
            String queryPaused = "SELECT name, image, containerId FROM containers WHERE SystemIp = ? AND state = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(queryPaused);
            preparedStatement.setString(1, ip);
            preparedStatement.setString(2, "paused");
            ResultSet resultSet = preparedStatement.executeQuery();
            int i = 0;
            while (resultSet.next()) {
                i++;
                String name, image, containerId;
                name = resultSet.getString("name");
                image = resultSet.getString("image");
                containerId = resultSet.getString("containerId");
                String listOut = i + ") Name: " + name + ", Image: " + image + "  Container ID ->" + containerId;
                restartListContainer.getItems().add(listOut);
            }
            if (i != 0) {
                restartListContainer.getItems().remove(observableList);
            } else {
                restartListContainer.getItems().add("Nothing to Show Here");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    //This Method sets the field logsList.
    public void setListLogs() {
        List<Container> containers = ClientUpdater.getUpdatedContainersFromClient();
        String containerId = containers.getFirst().getId();
        //temporary gets only first id
        List<String> containerLogs = DockerInstance.showlogs(containerId);
        for (String log : containerLogs) {
            logsList.getItems().add(log);
        }
        if (logsList.getItems().size() > 1) {
            logsList.getItems().remove(observableList);
        } else {
            logsList.getItems().add("Nothing to Show Here");
        }
    }
    //This Method sets the field subnetsList.
    public void setListSubnets() {
        List<Container> containers = ClientUpdater.getUpdatedContainersFromClient();
        String containerId = containers.getFirst().getId();
        //temporary to check
        StringBuilder d = DockerNetwork.formatSubnetsSettings(
                DockerNetwork.inspectContainersForSubnet(
                        containerId));
        subnetsList.getItems().add(d.toString());
        if (subnetsList.getItems().size() > 1) {
            subnetsList.getItems().remove(observableList);
        } else {
            subnetsList.getItems().add("Nothing to Show Here");
        }
    }
    //This Method sets the field volumesList.
    public void setListVolumes() {
        int num = 0; //Numbers to make the output more User Friendly
        for (DockerVolume v : DockerVolume.volumeslist) {
            num++;
            volumesList.getItems().add(num + ") " + v.toString() + "\n");
        }
        if (num != 0) {
            volumesList.getItems().remove(observableList);
        } else {
            volumesList.getItems().add("Nothing to Show Here");
        }
    }
    //This Method sets the field networksList.
    public void setListNetworks() {
        int num = 0;
        for (DockerNetwork n : DockerNetwork.networkslist) {
            num++;
            networksList.getItems().add(num + ") " + n.toString());
        }
        if (num != 0) {
            networksList.getItems().remove(observableList);
        } else {
            networksList.getItems().add("Nothing to Show Here");
        }
    }
    //This method retrieves the id of the last exited container that user clicked on, on List View
    @FXML
    public void retrieveIdToStart(MouseEvent mouseEvent) {
        if (exitedContainers.getSelectionModel().getSelectedItem() != null) {
            String c = exitedContainers.getSelectionModel().getSelectedItem().toString();
            String[] c1 = c.split("->", 2);
            containerId = c1[1];
        }
    }
    //This method retrieves the id of the last active container that user clicked on, on List View
    @FXML
    public void retrieveIdToStop(MouseEvent mouseEvent) {
        if (activeContainers.getSelectionModel().getSelectedItem() != null) {
            String c = activeContainers.getSelectionModel().getSelectedItem().toString();
            String[] c1 = c.split("->", 2);
            containerId = c1[1];
        }
    }
    //This method retrieves the id of the last container that user clicked on, on List View
    @FXML
    public void retrieveId(MouseEvent mouseEvent) {
        if (containersList.getSelectionModel().getSelectedItem() != null) {
            String c = containersList.getSelectionModel().getSelectedItem().toString();
            String[] c1 = c.split("->", 2);
            containerId = c1[1];
        }
    }
    //This method retrieves the id of the last paused container that user clicked on, on List View
    @FXML
    public void retrieveIdToUnpause(MouseEvent mouseEvent) {
        if (restartListContainer.getSelectionModel().getSelectedItem() != null) {
            String c = restartListContainer.getSelectionModel().getSelectedItem().toString();
            String[] c1 = c.split("->", 2);
            containerId = c1[1];
        }
    }
    //This method retrieves the id of the last image that user clicked on, on List View
    @FXML
    public void retrieveIdForImage(MouseEvent mouseEvent) {
        if (imagesList.getSelectionModel().getSelectedItem() != null) {
            String c = imagesList.getSelectionModel().getSelectedItem().toString();
            String[] c1 = c.split("ImageID: ", 2);
            imageId = c1[1];
        }
    }
}
