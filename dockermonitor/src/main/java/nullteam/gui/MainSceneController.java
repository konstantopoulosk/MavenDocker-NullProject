package nullteam.gui;

import com.github.dockerjava.api.model.Container;
import com.google.gson.Gson;
import com.nullteam.*;
import com.sun.net.httpserver.HttpServer;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.SubScene;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.shape.Box;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import javax.swing.text.BoxView;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.URI;
import java.net.URL;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.Duration;
import java.util.ArrayList;
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
    private ListView<String> pausedContainers = new ListView<>(observableList); //List View to see PAUSED Containers
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
    @FXML
    private TextField nameToRename; //Name that user types to rename a container
    //private String containerId; //field for container id, user presses an item in List View and the container id is here
    //private String imageId; //field for image id, user presses in list view an item and image id is here
    static Connection connection = null; //Variable to store the connection
    final String ip = ClientUpdater.getIp(); //variable to get the System Ip of a User.
    private String idForApi;
    private static String id;
    Stage stage; //Stage to show
    Scene scene; //Current scene
    Parent root;
    static boolean isPressed = false; //This is used to only execute the initialize once.
    private static final String API_URL = "http://localhost:8080/api/perform-action"; //URL for API.
    Gson gson = new Gson();
    /**
     * This method sends a request in our API.
     * @param actionRequest ActionRequest
     * @param gson Gson
     */
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
            System.out.println("Caught Error: " + e.getMessage());
        }
    }
    /**
     * This method starts the srever that
     * takes user's requests.
     * @param handler PerformActionHandler
     */
    private void startHttpServer(PerformActionHandler handler) {
        try {
            // Create an HTTP server on the specified port
            HttpServer server =
                    HttpServer.create(new InetSocketAddress(8080), 0);

            // Create a context for the API path and set the handler
            server.createContext("/api/perform-action", handler);

            // Use a fixed thread pool to handle incoming requests
            server.setExecutor(Executors.newFixedThreadPool(10));

            // Start the server
            server.start();
        } catch (IOException e) {
            System.out.println("Caught Error: " + e.getMessage());
        }
    }
    /**
     * This method sends to the Executor the action the user
     * wants (START, STOP, REMOVE...)
     * and the id of the container or image the user chose.
     * @param action String
     */
    void implementAPIRequest(String action) {
        ActionRequest actionRequest = new ActionRequest(action, idForApi);
        CompletableFuture.runAsync(() -> sendActionRequest(actionRequest, gson))
                .thenRun(() -> System.out.println("Request sent successfully"))
                .exceptionally(throwable -> {
                    throwable.printStackTrace();
                    return null;
                });
    }
    //API CONFIGURATION
    //Initialize Method is necessary
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        if (!isPressed) {
            connection = DatabaseThread.takeCredentials();
            Lists.listContainers();
            Lists.listImage(); //Creating objects of DockerImage class to use DockerImage.imagesList later
            Lists.listVolumes();//Creating objects of DockerVolume class to use DockerVolume.volumesList later
            Lists.listNetworks();//Creating objects of DockerNetwork class to use DockerNetwork.networksList later
            isPressed = true; // flag is now true -> initialize does not execute
            DockerMonitor monitor = new DockerMonitor();
            monitor.start(); //starting Monitor Thread
            BlockingQueue<ActionRequest> actionQueue = new LinkedBlockingQueue<>();
            PerformActionHandler performActionHandler = new PerformActionHandler(actionQueue, gson);
            ExecutorThread executorThread = new ExecutorThread(actionQueue);
            executorThread.start(); //Starting Executor Thread
            startHttpServer(performActionHandler);
            databaseThread(); //Executing / Running Database Thread (ONCE)
        }
    }
    /**
     * This method runs the Database Thread.
     */
    public void databaseThread() {
        DatabaseThread databaseThread = new DatabaseThread(connection, ip);
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
     */
    public void changeTheScenes(String fxmlFile, ActionEvent event) {
        try {
            root = FXMLLoader.load(getClass().getResource(fxmlFile));
            stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
        } catch (CannotChangeTheScenes e) {
            System.out.println("Caught Error: " + e.getMessage());
        } catch (Exception e1) {
            System.out.println("Exception Happened ...");
        }

    }
    /**
     * This method opens a new window when necessary.
     * @param event ActionEvent
     * @param fxml String
     * @param title String
     */
    @FXML
    public void openNewWindow(ActionEvent event, String fxml, String title) {
        try {
            Parent root1 = FXMLLoader.load(getClass().getClassLoader().getResource(fxml));
            Stage stage = new Stage(); //Stage
            stage.setTitle(title); //Title of the Stage.
            stage.getIcons().add(App.getIcon());
            stage.setScene(new Scene(root1, 600, 400));
            stage.show();
        } catch (CannotOpenNewWindow e) {
            System.out.println("Caught Error: " + e.getMessage());
        } catch (Exception e1) {
            System.out.println("Exception happened ...");
        }
    }
    /**
     * This method opens a confirmation window.
     * @param event ActionEvent
     * @param title String
     * @param fxml String
     */
    @FXML
    public void openConfirmationWindow(ActionEvent event, String title, String fxml) {
        try {
            Parent root1 = FXMLLoader.load(getClass().getClassLoader().getResource(fxml));
            Stage stage = new Stage(); //Stage
            stage.setTitle(title); //Title of the stage
            stage.getIcons().add(App.getIcon()); //Added the Icon.
            stage.setScene(new Scene(root1, 300, 150)); //Setting the Scene.
            stage.show(); //Showing the Scene.
        } catch (CannotOpenNewWindow cannotOpenNewWindow) {
            System.out.println("Caught Error: " + cannotOpenNewWindow.getMessage());
        } catch (Exception e) {
            System.out.println("Exception Happened ... 123");
        }
    }
    /**
     * This method closes any new window
     * when user clicks on close.
     * @param event ActionEvent
     */
    @FXML
    public void closeNewWindow(ActionEvent event) {
        try {
            id = null;
            final Node source = (Node) event.getSource();
            final Stage stage = (Stage) source.getScene().getWindow();
            stage.close();
        } catch (Exception e) {
            System.out.println("Caught Error: " + e.getMessage());
        }
    }
    /**
     * This method starts the application. It changes the scene from
     * Scene1 to Scene2, from our homepage to the main menu.
     * @param event ActionEvent
     */
    @FXML
    public void startApp(ActionEvent event) {
        //Goes from Scene1 to Scene2.
        try {
            changeTheScenes("/Scene2.fxml", event);
        } catch (Exception e) {
            System.out.println("Caught Error: " + e.getMessage());
        }
    }
    /**
     * This method is used to exit the application
     * @param event ActionEvent
     */
    @FXML
    public void exitApp(ActionEvent event) {
        System.exit(0);

    }
    /**
     * This method is used to go to Images Menu.
     * @param event ActionEvent
     */
    @FXML
    public void pressImages(ActionEvent event) {
        changeTheScenes("/images.fxml", event);
    }
    /**
     * This method is used when user clicks
     * to see user's images when
     * is on an image tool.
     * @param event ActionEvent
     */
    @FXML
    public void tapToListImages(ActionEvent event) {
        imagesList = Lists.setListImages(imagesList);
    }

    /** This method is used when user clicks
     * on a Button to see his images.
     * @param event ActionEvent
     */
    @FXML
    public void tapFirstListImages(ActionEvent event) {
        imagesList = Lists.setListImages(imagesList);
        imagesList = new ListView<>(observableList);

    }
    /**
     * This method is used to go to Containers Menu.
     * @param event ActionEvent
     */
    @FXML
    public void pressContainers(ActionEvent event) {
        changeTheScenes("/containers.fxml", event);

    }
    /**
     * This method is used when user clicks
     * on a Button to see his Containers.
     * @param event ActionEvent
     */
    @FXML
    public void tapToListContainers(ActionEvent event) {
        System.out.println("You tapped to see the containers");
        containersList = Lists.setListContainers(containersList);
        databaseThread();
        containersList = new ListView<>(observableList);
    }
    /**
     * This method is used to go to Networsks Menu.
     * @param event ActionEvent
     */
    @FXML
    public void pressNetworks(ActionEvent event) {
        changeTheScenes("/networks.fxml", event);
    }
    /**
     * This method is used when user clicks
     * on a button to see his Networks.
     * @param event ActionEvent
     */
    @FXML
    public void tapToSeeYourNetworks(ActionEvent event) {
        networksList = Lists.setListNetworks(networksList);
        networksList = new ListView<>(observableList);
    }

    /**
     * This method is used to go to Volumes Menu.
     * @param event ActionEvent
     */
    @FXML
    public void pressVolumes(ActionEvent event) {
        changeTheScenes("/volumes.fxml", event);
    }

    /**
     * This method is used when user clicks
     * on a button to see his Volumes.
     * @param event ActionEvent
     */
    @FXML
    public void tapToSeeVolumes(ActionEvent event) {
        volumesList = Lists.setListVolumes(volumesList);
        volumesList = new ListView<>(observableList);
    }
    /**
     * This method takes the user to a new scene
     * where he/she can see the Exited containers
     * and choose one to start.
     * @param event ActionEvent
     */
    @FXML
    public void pressStart(ActionEvent event) {
        changeTheScenes("/startContainerNew.fxml", event);
    }

    /**
     * This method is executed when user clicks
     * on a button to see the Exited Containers.
     * @param event ActionEvent
     */
    @FXML
    public void tapToSeeExitedContainers(ActionEvent event) {
        exitedContainers = Lists.setListExitedContainers(exitedContainers);
    }

    /**
     * This method is executed when user clicks
     * Apply to start a container chosen from the ListView.
     * @param event ActionEvent
     */
    @FXML
    public void startContainer(ActionEvent event) {
        idForApi = Lists.choiceContainers.getLast();
        System.out.println(idForApi);
        if (idForApi != null && !idForApi.equals("NULL")) { //It may be null.
            implementAPIRequest("START");
            openConfirmationWindow(event, "Starting Container Properties", "startContainerConfirmation.fxml");
            databaseThread();
            //exitedContainers = new ListView<>(observableList); //Initialize this again so Listing all Over again
        } //and not below.
    }
    /**
     * This method takes the user to a new scene
     * where he/she can see the Active containers
     * and choose one to stop.
     * @param event ActionEvent
     */
    @FXML
    public void pressStop(ActionEvent event) {
        changeTheScenes("/stopContainerNew.fxml", event);
    }
    /**
     * This method is executed when user clicks
     * Apply to stop a container chosen from the ListView.
     * @param event ActionEvent
     */
    @FXML
    public void stopContainer(ActionEvent event) {
        idForApi = Lists.choiceContainers.getLast();
        System.out.println(idForApi);
        if (idForApi != null && !idForApi.equals("NULL")) {
            implementAPIRequest("STOP");
            openConfirmationWindow(event, "Stop Container Properties", "stopContainerConfirmation.fxml");
            databaseThread();
            activeContainers = new ListView<>(observableList);
        }
    }
    /**
     * This method is executed when user presses
     * a button to see the active containers.
     * @param event ActionEvent
     */
    @FXML
    public void tapToSeeActiveContainers(ActionEvent event) {
        activeContainers = Lists.setListActiveContainers(activeContainers);
    }
    /**
     * This method takes the user to a new scene that has a
     * List View and a Text Field for the new name.
     * @param event ActionEvent
     */
    @FXML
    public void pressRename(ActionEvent event) {
        changeTheScenes("/renameContainerNew.fxml", event);
    }

    /**
     * This method is executed when user presses a button
     * to see all the containers and choose one to rename it.
     * @param event ActionEvent
     */
    @FXML
    public void tapToListContainersToRename(ActionEvent event) {
        containersList = Lists.setListContainers(containersList);
        databaseThread();
    }

    /**
     * This method is executed when user presses Apply to rename
     * a container chosen previously from the List View.
     * @param event ActionEvent
     */
    @FXML
    public void renameContainer(ActionEvent event) {
        idForApi = Lists.choiceContainers.getLast();
        System.out.println(idForApi);
        String newName = nameToRename.getText();
        System.out.println(newName);
        if (idForApi != null && !idForApi.equals("NULL")) {
            ActionRequest actionRequest = new ActionRequest("RENAME", idForApi, newName);
            CompletableFuture.runAsync(() -> sendActionRequest(actionRequest, gson))
                    .thenRun(() -> System.out.println("Request sent successfully"))
                    .exceptionally(throwable -> {
                        throwable.printStackTrace();
                        return null;
                    });
            openConfirmationWindow(event, "Rename Container Properties", "renameContainerConfirmation.fxml");
            databaseThread();
            containersList = new ListView<>(observableList);
        }
    }
    /**
     * This method takes the user to a new scene where he/she
     * can see all the containers and choose one to remove.
     * @param event ActionEvent
     */
    @FXML
    public void pressRemove(ActionEvent event) {
        changeTheScenes("/removeContainerNew.fxml", event);
    }
    /**
     * This method is executed when user presses Apply to remove
     * a container chosen previously from the List View.
     * @param event ActionEvent
     */
    @FXML
    public void removeContainer(ActionEvent event) {
        idForApi = Lists.choiceContainers.getLast();
        System.out.println(idForApi);
        if (idForApi != null && !idForApi.equals("NULL")) {
            implementAPIRequest("REMOVE");
            openConfirmationWindow(event, "Remove Container Properties", "removeContainerConfirmation.fxml");
            databaseThread();
            containersList = new ListView<>(observableList);
        }
    }
    /**
     * This method takes the user to a new scene where he/she
     * can see the Active containers and choose one to restart.
     * @param event ActionEvent
     */
    @FXML
    public void pressRestart(ActionEvent event) {
        changeTheScenes("/restartContainerNew.fxml", event);
    }
    /**
     * This method is executed when user presses Apply to restart
     * a container chosen previously from the List View.
     * @param event ActionEvent
     */
    @FXML
    public void restartContainer(ActionEvent event) {
        idForApi = Lists.choiceContainers.getLast();
        if (idForApi != null && !idForApi.equals("NULL")) {
            implementAPIRequest("RESTART");
            openConfirmationWindow(event, "Restart Container Properties", "restartContainerConfirmation.fxml");
            databaseThread();
            activeContainers = new ListView<>(observableList);
        }
    }
    /**
     * This method is executed when user clicks a button to see
     * the paused containers in order to unpause.
     * @param event ActionEvent
     */
    @FXML
    public void tapToSeePausedContainers(ActionEvent event) {
        pausedContainers = Lists.setListPausedContainers(pausedContainers);
    }
    /**
     * This method takes the user to a new scene
     * where he/she can see the Active containers
     * and choose one to pause.
     * @param event ActionEvent
     */
    @FXML
    public void pressPause(ActionEvent event) {
        changeTheScenes("/pauseContainerNew.fxml", event);
    }
    /**
     * This method is executed when user presses Apply to pause
     * a container chosen previously from the List View.
     * @param event ActionEvent
     */
    @FXML
    public void pauseContainer(ActionEvent event) {
        idForApi = Lists.choiceContainers.getLast();
        if (idForApi != null && !idForApi.equals("NULL")) {
            implementAPIRequest("PAUSE");
            openConfirmationWindow(event, "Pause Container Properties", "pauseContainerConfirmation.fxml");
            databaseThread();
            activeContainers = new ListView<>(observableList);
        }
    }
    /**
     * This method takes the user to a new scene where he/she
     * can see the Paused containers and choose one to unpause.
     * @param event ActionEvent
     */
    @FXML
    public void pressUnpause(ActionEvent event) {
        changeTheScenes("/unpauseContainerNew.fxml", event);
    }
    /**
     * This method is executed when user presses Apply to unpause
     * a container chosen previously from the List View.
     * @param event ActionEvent
     */
    @FXML
    public void unpauseContainer(ActionEvent event) {
        idForApi = Lists.choiceContainers.getLast();
        if (idForApi != null && !idForApi.equals("NULL")) {
            implementAPIRequest("UNPAUSE");
            openConfirmationWindow(event, "Unpause Container Properties", "unpauseContainerConfirmation.fxml");
            databaseThread();
            pausedContainers = new ListView<>(observableList); //THIS SHOWS PAUSED CONTAINERS!!!!
        }
    }
    /**
     * This method takes the user to a new scene where he/she
     * can see all the active containers and choose one to kill.
     * @param event ActionEvent
     */
    @FXML
    public void pressKill(ActionEvent event) {
        changeTheScenes("/killContainerNew.fxml", event);
    }
    /**
     * This method is executed when user presses Apply to kill
     * a container chosen previously from the List View.
     * @param event ActionEvent
     */
    @FXML
    public void killContainer(ActionEvent event) {
        idForApi = Lists.choiceContainers.getLast();
        if (idForApi != null && !idForApi.equals("NULL")) {
            implementAPIRequest("KILL");
            openConfirmationWindow(event, "Kill Container Properties", "killContainerConfirmation.fxml");
            databaseThread();
            activeContainers = new ListView<>(observableList);
        }
    }
    /**
     * This method takes the user to a new scene
     * where he/she can see the Active containers.
     * @param event ActionEvent
     */
    @FXML
    public void pressLogs(ActionEvent event) {
        changeTheScenes("/logsOfContainerNew.fxml", event);
    }
    /**
     * This method is executed when user presses Apply to see the
     * logs of a container chosen previously from the List View.
     * @param event ActionEvent
     */
    @FXML
    public void applyToSeeTheLogs(ActionEvent event) {
        idForApi = Lists.choiceContainers.getLast();
        if (idForApi != null && !idForApi.equals("NULL")) {
            openNewWindow(event, "listOfLogsNew.fxml", "List of Logs");
            //Opens a new Window with a List View of the Logs
            id = idForApi;
        }
        activeContainers = new ListView<>(observableList);
    }
    /**
     * This method is executed when the user presses a button
     * to see the logs of a container
     * @param event ActionEvent
     */
    @FXML
    public void tapToSeeTheLogs(ActionEvent event) {
        System.out.println(id);
        logsList = Lists.setListLogs(logsList, id);
        logsList = new ListView<>(observableList);
    }
    /**
     * This method takes the user to a new scene where he/she can
     * see the Active containers and choose one to see its subnets.
     * @param event ActionEvent
     */
    @FXML
    public void pressSubnets(ActionEvent event) {
        changeTheScenes("/subnetsOfContainerNew.fxml", event);
    }
    /**
     * This method is executed when user presses Apply to see the
     * subnets of a container chosen previously from the List View.
     * @param event ActionEvent
     */
    @FXML
    public void applyToSeeSubnets(ActionEvent event) {
        idForApi = Lists.choiceContainers.getLast();
        if (idForApi != null && !idForApi.equals("NULL")) {
            openNewWindow(event, "listOfSubnetsNew.fxml", "List of Subnets");
            //Opens a new window to show the subnets
            id = idForApi;
        }
        activeContainers = new ListView<>(observableList);
    }
    /**
     * This method is executed when the user presses a button
     * to see the logs of a container.
     */
    @FXML
    public void tapToSeeSubnets() {
        System.out.println(id);
        subnetsList = Lists.setListSubnets(subnetsList, id);
        subnetsList = new ListView<>(observableList);
    }
    /**
     * This method takes the user to a new scene that has a List View of
     * the current images and a Text Field for the user to write
     * the image they want to pull.
     * @param event ActionEvent
     */
    @FXML
    public void pressPull(ActionEvent event) {
        changeTheScenes("/imagePullNew.fxml", event);
    }
    /**
     * This method is executed when user presses Apply
     * to pull an image.
     * @param event ActionEvent
     */
    @FXML
    public void applyPull(ActionEvent event) {
            if (imageToPull != null && !DockerImage.imageslist.contains(idForApi)) {
                String image = imageToPull.getText(); //This is what User wrote he wants to pull.
                System.out.println(image);
                openConfirmationWindow(event, "Pull Image Properties", "imagePullConfirmation.fxml");
                DockerImage.pullImage(image);
            } else {
                System.out.println(imageToPull);
                System.out.println("Something Is Wrong!");
            }
    }
    /**
     * THis method takes the user to a new scene with
     * a list view of the current images.
     * @param event ActionEvent
     */
    @FXML
    public void pressImplement(ActionEvent event) {
        changeTheScenes("/imageImplementNew.fxml", event);
    }
    /**
     * This method is executed when user presses Apply
     * to implement an image, run an instance of this image.
     * @param event ActionEvent
     */
    @FXML
    public void applyImplement(ActionEvent event) {
        idForApi = Lists.choiceImages.getLast();
        System.out.println(idForApi);
        if (idForApi != null && !idForApi.equals("NULL")) {
            implementAPIRequest("IMPLEMENT");
            openConfirmationWindow(event, "Implement Image Properties", "imageImplementConfirmation.fxml");
            databaseThread();
        }
    }
    /**
     * This method takes the user to a new scene with a
     * ListView of the images.
     * @param event ActionEvent
     */
    @FXML
    public void pressRemoveImage(ActionEvent event) {
        changeTheScenes("/imageRemoveNew.fxml", event);
    }
    /**
     * This method is executed when user presses Apply
     * to remove an image and its containers.
     * @param event ActionEvent
     */
    @FXML
    public void applyRemove(ActionEvent event) {
        idForApi = Lists.choiceImages.getLast();
        if (idForApi != null && !idForApi.equals("NULL")) {
            implementAPIRequest("REMOVEIMAGE");
            openConfirmationWindow(event, "Remove Image Properties", "imageRemoveConfirmation.fxml");
            databaseThread();
        }
    }
    /**
     * This method takes the user to a new scene with a
     * ListView only of the IN-USE images.
     * @param event ActionEvent
     */
    @FXML
    public void changeToSeeAnotherList(ActionEvent event) {
        changeTheScenes("/seeImagesInUse.fxml", event);
    }
    /**
     * This method is executed when user presses a butyon to
     * see the used images.
     * @param event ActionEvent
     */
    @FXML
    public void tapToSeeImagesInUse(ActionEvent event) {
        imagesInUse = Lists.setImagesInUse(imagesInUse);
        imagesInUse = new ListView<>(observableList);
    }
    /**
     * This method retrieves the id of the last exited
     * container that user clicked on, on the List View.
     * @param mouseEvent MouseEvent
     */
    @FXML
    public void retrieveIdToStart(MouseEvent mouseEvent) {
        System.out.println(exitedContainers.getSelectionModel().getSelectedItem());
        if (exitedContainers.getSelectionModel().getSelectedItem() != null
                && !exitedContainers.getSelectionModel().getSelectedItem().equals("NULL")) {
            String c = exitedContainers.getSelectionModel().getSelectedItem().toString();
            String[] c1 = c.split("ID: ", 2);
            Lists.choiceContainers.add(c1[1]);
        }
    }
    /**
     * This method retrieves the id of the last active container
     * that user clicked on, on the List View.
     * @param mouseEvent MouseEvent
     */
    @FXML
    public void retrieveIdToStop(MouseEvent mouseEvent) {
        System.out.println(activeContainers.getSelectionModel().getSelectedItem());
        if (activeContainers.getSelectionModel().getSelectedItem() != null
        && !activeContainers.getSelectionModel().getSelectedItem().equals("NULL")) {
            String c = activeContainers.getSelectionModel().getSelectedItem().toString();
            String[] c1 = c.split("ID: ", 2);
            Lists.choiceContainers.add(c1[1]);
            //containerId = c1[1];
        }
    }
    /**
     * This method retrieves the id of the last container
     * that user clicked on, on the List View
     * @param mouseEvent MouseEvent
     */
    @FXML
    public void retrieveId(MouseEvent mouseEvent) {
        System.out.println(containersList.getSelectionModel().getSelectedItem());
        if (containersList.getSelectionModel().getSelectedItem() != null
                && !containersList.getSelectionModel().getSelectedItem().equals("NULL")) {
            String c = containersList.getSelectionModel().getSelectedItem().toString();
            String[] c1 = c.split("ID: ", 2);
            Lists.choiceContainers.add(c1[1]);
            //containerId = c1[1];
        }
    }
    /**
     * This method retrieves the id of the last paused
     * container that the user clicked on, on the List View.
     * @param mouseEvent MouseEvent
     */
    @FXML
    public void retrieveIdToUnpause(MouseEvent mouseEvent) {
        System.out.println(pausedContainers.getSelectionModel().getSelectedItem());
        if (pausedContainers.getSelectionModel().getSelectedItem() != null
                && !pausedContainers.getSelectionModel().getSelectedItem().equals("NULL")) {
            String c = pausedContainers.getSelectionModel().getSelectedItem().toString();
            String[] c1 = c.split("ID: ", 2);
            Lists.choiceContainers.add(c1[1]);
            //containerId = c1[1];
        }
    }
    /**
     * This method retrieves the id of the last image
     * that the user clicked on, on the List View.
     * @param mouseEvent MouseEvent
     */
    @FXML
    public void retrieveIdForImage(MouseEvent mouseEvent) {
        System.out.println(imagesList.getSelectionModel().getSelectedItem());
        if (imagesList.getSelectionModel().getSelectedItem() != null
                && !imagesList.getSelectionModel().getSelectedItem().equals("NULL")) {
            String c = imagesList.getSelectionModel().getSelectedItem().toString();
            String[] c1 = c.split("ImageID: ", 2);
            Lists.choiceImages.add(c1[1]);
        }
        System.out.println(Lists.choiceImages.getLast());
    }
    @FXML
    private TextField date;
    @FXML
    private ListView<String> measure = new ListView<>(observableList);
    @FXML
    private Text upContainers;
    @FXML
    private Text downContainers;
    @FXML
    public void measurements(ActionEvent event) {
        databaseThread();
        changeTheScenes("/measurements.fxml", event);
    }
    @FXML
    public void applyMeasurements(ActionEvent event) {
        upContainers.setText("0");
        downContainers.setText("0");
        String d = this.date.getText();
        Date date = Date.valueOf(d);
        List<String> list = new ArrayList<>();
        int up = 0, down = 0;
        try {
            ResultSet output = measurementsQuery(date);
            while (output.next()) {
                String containerId, name, image, state;
                int id;
                id = output.getInt("id");
                containerId = output.getString("containerId");
                name = output.getString("name");
                image = output.getString("image");
                state = output.getString("state");
                String s =
                        "\nMeasurement ID: " + id
                        + "\nContainer ID: " + containerId
                        + "\nName: " + name
                        + " Image: " + image
                        + " State: " + state;

                list.add(s);
                if (state.startsWith("running")
                    || state.endsWith("paused")) {
                    up++;
                } else {
                    down++;
                }
            }
            measure = Lists.setListMeasure(list, measure);
            upContainers.setText(String.valueOf(up));
            downContainers.setText(String.valueOf(down));
            measure = new ListView<>(observableList);
        } catch (Exception e) {
            System.out.println("Caught Error: " + e.getMessage());
        }
    }
    public ResultSet measurementsQuery(Date date) {
        try {
            String query = "select measurements.id, containerId, name, image, state from measurements, " +
                    "containers where containers.SystemIp = ? " +
                    "and containers.SystemIp = measurements.SystemIp and containers.id = measurements.id " +
                    "and measurements.date = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, ip);
            preparedStatement.setDate(2, date);
            return preparedStatement.executeQuery();
        } catch (Exception e) {
            System.out.println("Caught Error: " + e.getMessage());
            return null;
        }
    }
}
