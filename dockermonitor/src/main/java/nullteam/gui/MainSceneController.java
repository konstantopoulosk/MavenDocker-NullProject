package nullteam.gui;

import com.github.dockerjava.api.model.Container;
import com.google.gson.Gson;
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
import java.util.List;
import java.util.ResourceBundle;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

public class MainSceneController implements Initializable {
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
            isPressed = true;
            GetHelp.listContainers();
            DockerMonitor monitor = new DockerMonitor();
            monitor.start();
            BlockingQueue<ActionRequest> actionQueue = new LinkedBlockingQueue<>();
            PerformActionHandler performActionHandler = new PerformActionHandler(actionQueue, gson);
            ExecutorThread executorThread = new ExecutorThread(actionQueue);
            executorThread.start();
            startHttpServer(performActionHandler);
            databaseThread();
        }
    }
    public void databaseThread() {
        DatabaseThread databaseThread = new DatabaseThread(connection);
        databaseThread.run();
        /*
        try {
            databaseThread.join();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
         */
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
        System.out.println("Tap List Images");
        //setListImages();
        imagesList = new ListView<>(images);
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
         //todo: Executor.
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
         //todo: Executor.
        setListVolumes();
        volumesList = new ListView<>(volumes);
    }
    @FXML
    public void pressStart(ActionEvent event) throws IOException {
        changeTheScenes("/startContainer.fxml", event);
    }
    @FXML
    public void tapToSeeExitedContainers(ActionEvent event) throws IOException {
        setListExitedContainers();
        exitedContainers = new ListView<>(exitedContainersINIT);
    }
    @FXML
    public void startContainer(ActionEvent event) throws IOException {
        //todo: Executor.
        List<Container> containers = ClientUpdater.getUpdatedContainersFromClient();
        String containerId = containers.getFirst().getId();
        System.out.println(containerId);
        // Create an ActionRequest object
        ActionRequest actionRequest = new ActionRequest("START", containerId);
        // Send the request to the API
        CompletableFuture.runAsync(() -> sendActionRequest(actionRequest, gson))
                .thenRun(() -> System.out.println("Request sent successfully"))
                .exceptionally(throwable -> {
                    throwable.printStackTrace();
                    return null;
                });
        openConfirmationWindow(event, "Starting Container Properties", "startContainerConfirm.fxml");
        databaseThread();
    }
    @FXML
    public void pressStop(ActionEvent event) throws IOException {
        changeTheScenes("/stopContainer.fxml", event);
    }
    @FXML
    public void stopContainer(ActionEvent event) throws IOException {
        //todo: Executor.
        openConfirmationWindow(event, "Stop Container Properties", "stopContainerConfirm.fxml");
        databaseThread();
    }
    @FXML
    public void tapToSeeActiveContainers(ActionEvent event) throws IOException {
        setListActiveContainers();
        activeContainers = new ListView<>(actives);
    }
    @FXML
    public void pressRename(ActionEvent event) throws IOException {
        changeTheScenes("/renameContainer.fxml", event);
    }
    @FXML
    public void renameContainer(ActionEvent event) throws IOException {
        //todo: Executor.
        openConfirmationWindow(event, "Rename Container Properties", "renameContainerConfirm.fxml");
        databaseThread();
    }
    @FXML
    public void pressRemove(ActionEvent event) throws IOException {
        changeTheScenes("/removeContainer.fxml", event);
    }
    @FXML
    public void removeContainer(ActionEvent event) throws IOException {
        //todo: Executor.
        openConfirmationWindow(event, "Remove Container Properties", "removeContainerConfirm.fxml");
        databaseThread();
    }
    @FXML
    public void pressRestart(ActionEvent event) throws IOException {
        changeTheScenes("/restartContainer.fxml", event);
    }
    @FXML
    public void restartContainer(ActionEvent event) throws IOException {
        //todo: Executor.
        openConfirmationWindow(event, "Restart Container Properties", "restartContainerConfirm.fxml");
        databaseThread();
    }
    @FXML
    public void tapToSeeRestartContainers(ActionEvent event) throws IOException {
        setListRestartContainers();
        restartListContainer = new ListView<>(restart);
    }
    @FXML
    public void pressPause(ActionEvent event) throws IOException {
        changeTheScenes("/pauseContainer.fxml", event);
    }
    @FXML
    public void pauseContainer(ActionEvent event) throws IOException {
        //todo: Executor.
        openConfirmationWindow(event, "Pause Container Properties", "pauseContainerConfirm.fxml");
        databaseThread();
    }
    @FXML
    public void tapToSeePausedContainers(ActionEvent event) throws IOException {
         setListPauseContainers();
        pauseContainers = new ListView<>(pause);
    }
    @FXML
    public void pressUnpause(ActionEvent event) throws IOException {
        changeTheScenes("/unpauseContainer.fxml", event);
    }
    @FXML
    public void unpauseContainer(ActionEvent event) throws IOException {
        //todo: Executor.
        openConfirmationWindow(event,"Unpause Container Properties", "unpauseContainerConfirm.fxml");
        databaseThread();
    }
    @FXML
    public void pressKill(ActionEvent event) throws IOException {
        changeTheScenes("/killContainer.fxml", event);
    }
    @FXML
    public void killContainer(ActionEvent event) throws IOException {
        //todo: Executor.
        openConfirmationWindow(event, "Kill Container Properties", "killContainerConfirm.fxml");
        databaseThread();
    }
    @FXML
    public void pressLogs(ActionEvent event) throws IOException {
        changeTheScenes("/logsOfContainer.fxml", event);
    }
    @FXML
    public void applyToSeeTheLogs(ActionEvent event) throws IOException {
        //todo: Executor.
         openNewWindow(event, "listOfLogs.fxml", "List of Logs");
    }
    @FXML
    public void tapToSeeTheLogs(ActionEvent event) throws IOException {
        setListLogs();
        logsList = new ListView<>(logs);
    }
    @FXML
    public void pressSubnets(ActionEvent event) throws IOException {
        changeTheScenes("/subnetsOfContainer.fxml", event);
    }
    @FXML
    public void applyToSeeSubnets(ActionEvent event) throws IOException {
         //todo: Executor.
        openNewWindow(event, "listOfSubnets.fxml", "List of Subnets");
    }
    @FXML
    public void tapToSeeSubnets() {
        setListSubnets();
        subnetsList = new ListView<>(subnets);
    }
    @FXML
    public void pressPull(ActionEvent event) throws IOException {
        changeTheScenes("/imagePull.fxml", event);
    }
    @FXML
    public void applyPull(ActionEvent event) throws IOException {
         //todo: EXECUTOR
         openConfirmationWindow(event, "Pull Image Properties", "imagePullConfirm.fxml");
    }
    @FXML
    public void pressImplement(ActionEvent event) throws IOException {
        changeTheScenes("/imageImplement.fxml", event);
    }
    @FXML
    public void applyImplement(ActionEvent event) throws IOException {
         //todo: Executor
        openConfirmationWindow(event,"Implement Image Properties", "imageImplementConfirm.fxml" );
        databaseThread();
    }
    @FXML
    public void pressRemoveImage(ActionEvent event) throws IOException {
        changeTheScenes("/imageRemove.fxml", event);
    }
    @FXML
    public void applyRemove(ActionEvent event) throws IOException {
         //todo: Executor.
        openConfirmationWindow(event, "Remove Image Properties", "imageRemoveConfirm.fxml");
        databaseThread();
    }
    private final ObservableList<String> containers = FXCollections.observableArrayList("Name", "Image", "State");
    @FXML
    private ListView<String> containersList = new ListView<>(containers);
    public void setListContainers() {
        try {
            String queryContainers = "SELECT name, image, state FROM containers WHERE SystemIp = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(queryContainers);
            preparedStatement.setString(1, ip);
            ResultSet containersOutput = preparedStatement.executeQuery();
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
    }
    private final ObservableList<String> images = FXCollections.observableArrayList("Repository",
            "Tag", "Times Used", "Size");
    @FXML
    private ListView<String> imagesList = new ListView<>(images);
    /*
    public void setListImages() {
        try {
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
    }

     */
    private final ObservableList<String> exitedContainersINIT = FXCollections.observableArrayList("name");
    @FXML
    private ListView<String> exitedContainers = new ListView<>(exitedContainersINIT);
    public void setListExitedContainers() {
        try {
            String queryExited = "select name, id from containers where state = ? and SystemIp = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(queryExited);
            preparedStatement.setString(1, "exited");
            preparedStatement.setString(2, ip);
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
    }
    private final ObservableList<String> actives = FXCollections.observableArrayList("name");
    @FXML
    private ListView<String> activeContainers = new ListView<>(actives);
    public void setListActiveContainers() {
        try {
            String queryExited = "select name from containers where state = ? and SystemIp = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(queryExited);
            preparedStatement.setString(1, "running");
            preparedStatement.setString(2, ip);
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
    private ObservableList<String> restart = FXCollections.observableArrayList("name");
    @FXML
    private ListView<String> restartListContainer = new ListView<>(restart);
    public void setListRestartContainers() {
        setListContainers();
        if (containersList.getItems().size() > 1) {
            int j = 0, i;
            for (i = 0; i < containersList.getItems().size(); i++) {
                String containerInfo = containersList.getItems().get(i);
                String[] containerInfoArr = containerInfo.split(",");
                String state = containerInfoArr[0];
                if (state.endsWith("(Paused)")) {
                    j++;
                    restartListContainer.getItems().add(containerInfoArr[0]);
                }
            }
            if (i == 0 || j == 0) {
                restartListContainer.getItems().removeAll();
                ObservableList<String> restart1 = FXCollections.observableArrayList("No Paused Containers");
                restartListContainer = new ListView<>(restart1);
                restartListContainer.getItems().add("Nothing to Show Here :(");
            } else {
                restartListContainer.getItems().remove(restart);
            }
        } else {
            restartListContainer.getItems().removeAll();
            ObservableList<String> restart1 = FXCollections.observableArrayList("No Paused Containers");
            restartListContainer = new ListView<>(restart1);
            restartListContainer.getItems().add("Nothing to Show Here :(");
        }
    }
    private ObservableList<String> logs = FXCollections.observableArrayList("name");
    @FXML
    private ListView<String> logsList = new ListView<>(logs);
    public void setListLogs() {
        //todo.
    }
    private ObservableList<String> subnets = FXCollections.observableArrayList("name");
    @FXML
    private ListView<String> subnetsList = new ListView<>(subnets);
    public void setListSubnets() {
        //todo.
    }
    private ObservableList<String> volumes = FXCollections.observableArrayList("name");
    @FXML
    private ListView<String> volumesList = new ListView<>(volumes);
    public void setListVolumes() {
        //todo.
    }
    private ObservableList<String> networks = FXCollections.observableArrayList("name");
    @FXML
    private ListView<String> networksList = new ListView<>(networks);
    public void setListNetworks() {
        //todo.
    }
}
