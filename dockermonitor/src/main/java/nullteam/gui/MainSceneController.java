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
    //private List<DockerImage> dockerImagesStart;
    //private List<DockerImage> dockerImageNow;
    private String containerId;
    private String imageId;
    static final Connection connection = ClientUpdater.connectToDatabase();
    final String ip = ClientUpdater.getIp();
     Stage stage;
     Scene scene;
     Parent root;
     static boolean isPressed = false;
    private static final String API_URL = "http://localhost:8080/api/perform-action";
    Gson gson = new Gson();
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
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        if (!isPressed) {
            GetHelp.listImage();
            //dockerImagesStart = DockerImage.imageslist;
            GetHelp.listVolumes();
            GetHelp.listNetworks();
            isPressed = true;
            DockerMonitor monitor = new DockerMonitor();
            monitor.start();
            BlockingQueue<ActionRequest> actionQueue = new LinkedBlockingQueue<>();
            PerformActionHandler performActionHandler = new PerformActionHandler(actionQueue, gson);
            ExecutorThread executorThread = new ExecutorThread(actionQueue);
            executorThread.start();
            startHttpServer(performActionHandler);
            databaseThread();
        }
        /*
        GetHelp.listImage();
        dockerImageNow = DockerImage.imageslist;
        if (!dockerImageNow.equals(dockerImagesStart)) {
            dockerImagesStart = new ArrayList<>(dockerImageNow);
        }
         */
        //todo: UPDATE?
    }
    public void databaseThread() {
        DatabaseThread databaseThread = new DatabaseThread(connection);
        databaseThread.run();
        try {
            databaseThread.join();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
     public void changeTheScenes(String fxmlFile, ActionEvent event) throws IOException {
         root = FXMLLoader.load(getClass().getResource(fxmlFile));
         stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
         scene = new Scene(root);
         stage.setScene(scene);
         stage.show();
     }
    @FXML
    public void openNewWindow(ActionEvent event, String fxml, String title) throws IOException {
        Parent root1 = FXMLLoader.load(getClass().getClassLoader().getResource(fxml));
        Stage stage = new Stage();
        stage.setTitle(title);
        stage.setScene(new Scene(root1, 600,400));
        stage.show();
    }
    @FXML
    public void openConfirmationWindow(ActionEvent event, String title, String fxml) throws IOException {
        Parent root1 = FXMLLoader.load(getClass().getClassLoader().getResource(fxml));
        Stage stage = new Stage();
        stage.setTitle(title);
        stage.setScene(new Scene(root1, 300, 150));
        stage.show();
    }
    @FXML
    public void closeNewWindow(ActionEvent event) throws IOException {
        final Node source = (Node) event.getSource();
        final Stage stage = (Stage) source.getScene().getWindow();
        stage.close();
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
        containersList = new ListView<>(containers);
        databaseThread();
    }
    @FXML
    public void pressNetworks(ActionEvent event) throws IOException {
        //Goes to Networks Menu.
        changeTheScenes("/networks.fxml", event);
    }
    @FXML
    public void tapToSeeYourNetworks(ActionEvent event) throws IOException {
        setListNetworks();
        networksList = new ListView<>(networks);
    }
    @FXML
    public void pressVolumes(ActionEvent event) throws IOException {
        //Goes to Volumes Menu.
        changeTheScenes("/volumes.fxml", event);
    }
    @FXML
    public void tapToSeeVolumes(ActionEvent event) throws IOException {
        setListVolumes();
        volumesList = new ListView<>(volumes);
    }
    @FXML
    public void pressStart(ActionEvent event) throws IOException {
        changeTheScenes("/startContainerNew.fxml", event);
    }
    @FXML
    public void tapToSeeExitedContainers(ActionEvent event) throws IOException {
        setListExitedContainers();
    }
    @FXML
    public void startContainer(ActionEvent event) throws IOException {
        if (containerId != null) {
            ActionRequest actionRequest = new ActionRequest("START", containerId);
            CompletableFuture.runAsync(() -> sendActionRequest(actionRequest, gson))
                    .thenRun(() -> System.out.println("Request sent successfully"))
                    .exceptionally(throwable -> {
                        throwable.printStackTrace();
                        return null;
                    });
            openConfirmationWindow(event, "Starting Container Properties", "startContainerConfirmation.fxml");
            databaseThread();
        }
        exitedContainers = new ListView<>(exitedContainersINIT);
    }
    @FXML
    public void pressStop(ActionEvent event) throws IOException {
        changeTheScenes("/stopContainerNew.fxml", event);
    }
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
        activeContainers = new ListView<>(actives);
    }
    @FXML
    public void tapToSeeActiveContainers(ActionEvent event) throws IOException {
        setListActiveContainers();
    }
    @FXML
    public void pressRename(ActionEvent event) throws IOException {
        changeTheScenes("/renameContainerNew.fxml", event);
    }
    @FXML
    public void renameContainer(ActionEvent event) throws IOException {
        //todo: Executor.
        if (containerId != null) {
            openConfirmationWindow(event, "Rename Container Properties", "renameContainerConfirmation.fxml");
            databaseThread();
        }
        containersList = new ListView<>(containers);
    }
    @FXML
    public void pressRemove(ActionEvent event) throws IOException {
        changeTheScenes("/removeContainerNew.fxml", event);
    }
    @FXML
    public void removeContainer(ActionEvent event) throws IOException {
        //todo: Executor.
        if (containerId != null) {
            openConfirmationWindow(event, "Remove Container Properties", "removeContainerConfirmation.fxml");
            databaseThread();
        }
        containersList = new ListView<>(containers);
    }
    @FXML
    public void pressRestart(ActionEvent event) throws IOException {
        changeTheScenes("/restartContainerNew.fxml", event);
    }
    @FXML
    public void restartContainer(ActionEvent event) throws IOException {
        //todo: Executor.
        if (containerId != null) {
            openConfirmationWindow(event, "Restart Container Properties", "restartContainerConfirmation.fxml");
            databaseThread();
        }
        activeContainers = new ListView<>(actives);
    }
    @FXML
    public void tapToSeeRestartContainers(ActionEvent event) throws IOException {
        setListRestartContainers();
    }
    @FXML
    public void pressPause(ActionEvent event) throws IOException {
        changeTheScenes("/pauseContainerNew.fxml", event);
    }
    @FXML
    public void pauseContainer(ActionEvent event) throws IOException {
        //todo: Executor.
        if (containerId != null) {
            openConfirmationWindow(event, "Pause Container Properties", "pauseContainerConfirmation.fxml");
            databaseThread();
        }
        restartListContainer = new ListView<>(restart);
    }
    @FXML
    public void pressUnpause(ActionEvent event) throws IOException {
        changeTheScenes("/unpauseContainerNew.fxml", event);
    }
    @FXML
    public void unpauseContainer(ActionEvent event) throws IOException {
        //todo: Executor.
        if (containerId != null) {
            openConfirmationWindow(event, "Unpause Container Properties", "unpauseContainerConfirmation.fxml");
            databaseThread();
        }
        restartListContainer = new ListView<>(restart);
    }
    @FXML
    public void pressKill(ActionEvent event) throws IOException {
        changeTheScenes("/killContainerNew.fxml", event);
    }
    @FXML
    public void killContainer(ActionEvent event) throws IOException {
        //todo: Executor.
        if (containerId != null) {
            openConfirmationWindow(event, "Kill Container Properties", "killContainerConfirmation.fxml");
            databaseThread();
        }
        containersList = new ListView<>(containers);
    }
    @FXML
    public void pressLogs(ActionEvent event) throws IOException {
        changeTheScenes("/logsOfContainerNew.fxml", event);
    }
    @FXML
    public void applyToSeeTheLogs(ActionEvent event) throws IOException {
        //todo: Executor.
        if (containerId != null) {

            openNewWindow(event, "listOfLogsNew.fxml", "List of Logs");
        }
        activeContainers = new ListView<>(actives);
    }
    @FXML
    public void tapToSeeTheLogs(ActionEvent event) throws IOException {
        setListLogs();
        logsList = new ListView<>(logs);
    }
    @FXML
    public void pressSubnets(ActionEvent event) throws IOException {
        changeTheScenes("/subnetsOfContainerNew.fxml", event);
    }
    @FXML
    public void applyToSeeSubnets(ActionEvent event) throws IOException {
        //todo: Executor.
        if (containerId != null) {
            openNewWindow(event, "listOfSubnetsNew.fxml", "List of Subnets");
        }
        activeContainers = new ListView<>(actives);
    }
    @FXML
    public void tapToSeeSubnets() {
        setListSubnets();
        subnetsList = new ListView<>(subnets);
    }
    @FXML
    public void pressPull(ActionEvent event) throws IOException {
        changeTheScenes("/imagePullNew.fxml", event);
    }
    @FXML
    public void applyPull(ActionEvent event) throws IOException {
         openConfirmationWindow(event, "Pull Image Properties", "imagePullConfirmation.fxml");
    }
    @FXML
    public void pressImplement(ActionEvent event) throws IOException {
        changeTheScenes("/imageImplementNew.fxml", event);
    }
    @FXML
    public void applyImplement(ActionEvent event) throws IOException {
         //todo: Executor
        System.out.println(imageId);
        if (imageId != null) {
            openConfirmationWindow(event, "Implement Image Properties", "imageImplementConfirmation.fxml");
            databaseThread();
        }
        imagesList = new ListView<>(images);
    }
    @FXML
    public void pressRemoveImage(ActionEvent event) throws IOException {
        changeTheScenes("/imageRemoveNew.fxml", event);
    }
    @FXML
    public void applyRemove(ActionEvent event) throws IOException {
         //todo: Executor.
        if (imageId != null) {
            openConfirmationWindow(event, "Remove Image Properties", "imageRemoveConfirmation.fxml");
            databaseThread();
        }
        imagesList = new ListView<>(images);
    }
    @FXML
    public void changeToSeeAnotherList(ActionEvent event) throws IOException {
        changeTheScenes("/seeImagesInUse.fxml", event);
    }
    private final ObservableList<String> imagesInUseINIT = FXCollections.observableArrayList(" ");
    @FXML
    private ListView<String> imagesInUse = new ListView<>(imagesInUseINIT);
    @FXML
    public void tapToSeeImagesInUse(ActionEvent event) {
        setImagesInUse();
        imagesInUse = new ListView<>(imagesInUseINIT);
    }
    public void setImagesInUse() {
        List<String> usedImages = DockerImage.listUsedImages();
        for (String usedImage : usedImages) {
            imagesInUse.getItems().add(usedImage);
        }
    }
    private final ObservableList<String> containers = FXCollections.observableArrayList("Name", "Image", "State");
    @FXML
    private ListView<String> containersList = new ListView<>(containers);
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
    }
    private final ObservableList<String> images = FXCollections.observableArrayList("Repository",
            "Tag", "Times Used", "Size");
    @FXML
    private ListView<String> imagesList = new ListView<>(images);
    public void setListImages() {
        int num = 0; //Numbers to make the output more User Friendly
        for (DockerImage img : DockerImage.imageslist) {
            num++;
            imagesList.getItems().add(num + ") " + img.toString());
        }
    }

    private final ObservableList<String> exitedContainersINIT = FXCollections.observableArrayList("Nothing to Show Here :(");
    @FXML
    private ListView<String> exitedContainers = new ListView<>(exitedContainersINIT);
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
                exitedContainers.getItems().remove(exitedContainersINIT);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private final ObservableList<String> actives = FXCollections.observableArrayList("Nothing to Show Here :(");
    @FXML
    private ListView<String> activeContainers = new ListView<>(actives);
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
                activeContainers.getItems().remove(actives);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private ObservableList<String> restart = FXCollections.observableArrayList("Nothing to Show Here :(");
    @FXML
    private ListView<String> restartListContainer = new ListView<>(restart);
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
                restartListContainer.getItems().remove(restart);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private ObservableList<String> logs = FXCollections.observableArrayList("name");
    @FXML
    private ListView<String> logsList = new ListView<>(logs);
    public void setListLogs() {
        //todo.
        /*List<Container> containers = ClientUpdater.getUpdatedContainersFromClient();
        String containerId = containers.getFirst().getId();
        String d = DockerLogs.showAllLogsOfContainer(containerId);
        logsList.getItems().add(d);
        */
    }
    private ObservableList<String> subnets = FXCollections.observableArrayList("name");
    @FXML
    private ListView<String> subnetsList = new ListView<>(subnets);
    public void setListSubnets() {
        List<Container> containers = ClientUpdater.getUpdatedContainersFromClient();
        String containerId = containers.getFirst().getId();
        //temporary to check
        StringBuilder d = DockerNetwork.formatSubnetsSettings(
                DockerNetwork.inspectContainersForSubnet(
                        containerId));
        subnetsList.getItems().add(d.toString());
    }
    private ObservableList<String> volumes = FXCollections.observableArrayList("name");
    @FXML
    private ListView<String> volumesList = new ListView<>(volumes);
    public void setListVolumes() {
        int num = 0; //Numbers to make the output more User Friendly
        for (DockerVolume v : DockerVolume.volumeslist) {
            num++;
            volumesList.getItems().add(num + ") " + v.toString() + "\n");
        }
    }
    private ObservableList<String> networks = FXCollections.observableArrayList("name");
    @FXML
    private ListView<String> networksList = new ListView<>(networks);
    public void setListNetworks() {
        int num = 0;
        for (DockerNetwork n : DockerNetwork.networkslist) {
            num++;
            networksList.getItems().add(num + ") " + n.toString());
        }
    }
    @FXML
    public void retrieveIdToStart(MouseEvent mouseEvent) {
        if (exitedContainers.getSelectionModel().getSelectedItem() != null) {
            String c = exitedContainers.getSelectionModel().getSelectedItem().toString();
            String[] c1 = c.split("->", 2);
            containerId = c1[1];
        }
    }
    @FXML
    public void retrieveIdToStop(MouseEvent mouseEvent) {
        if (activeContainers.getSelectionModel().getSelectedItem() != null) {
            String c = activeContainers.getSelectionModel().getSelectedItem().toString();
            String[] c1 = c.split("->", 2);
            containerId = c1[1];
        }
    }
    @FXML
    public void retrieveId(MouseEvent mouseEvent) {
        if (containersList.getSelectionModel().getSelectedItem() != null) {
            String c = containersList.getSelectionModel().getSelectedItem().toString();
            String[] c1 = c.split("->", 2);
            containerId = c1[1];
        }
    }
    @FXML
    public void retrieveIdToUnpause(MouseEvent mouseEvent) {
        if (restartListContainer.getSelectionModel().getSelectedItem() != null) {
            String c = restartListContainer.getSelectionModel().getSelectedItem().toString();
            String[] c1 = c.split("->", 2);
            containerId = c1[1];
        }
    }
    @FXML
    public void retrieveIdForImage(MouseEvent mouseEvent) {
        if (imagesList.getSelectionModel().getSelectedItem() != null) {
            String c = imagesList.getSelectionModel().getSelectedItem().toString();
            String[] c1 = c.split("ImageID: ", 2);
            imageId = c1[1];
        }
    }
}
