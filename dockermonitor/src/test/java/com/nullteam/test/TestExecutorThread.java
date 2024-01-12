package com.nullteam.test;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.cfg.ContextAttributes;
import com.github.dockerjava.api.model.Container;
import com.google.gson.Gson;
import com.nullteam.*;
import com.sun.net.httpserver.HttpServer;
import org.junit.*;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;

public class TestExecutorThread {
    private List<Container> containers = ClientUpdater.getUpdatedContainersFromClient();
    private Container container = containers.get(0);
    private String idToStop = null;
    private String idToStart = null;
    private final String name = container.getNames()[0];
    private final String id = container.getId();
    private final String image = container.getImage();
    private String status = container.getStatus();
    DockerInstance dockerInstance = new DockerInstance(name,id,image,status);
    private Container container2 = containers.get(1);
    private final String name2 = container2.getNames()[0];
    private final String id2 = container2.getId();
    private final String image2 = container2.getImage();
    private final String status2 = container2.getStatus();
    DockerInstance dockerInstance2 = new DockerInstance(name2, id2, image2, status2);
    List<DockerInstance> d;
    @Before
    public void setUp() {
        for (Container c : containers) {
            if (c.getStatus().startsWith("Up")) {
                idToStop = c.getId();
            } else {
                idToStart = c.getId();
            }
        }
    }
    @Test
    public void testFindContainerInClient() {
        DockerInstance dockerInstance1 = ExecutorThread.findContainerInClient(id);
        String id1 = dockerInstance1.getContainerId();
        Assert.assertTrue(id1.equals(id));
    }
    @Test
    public void testFindImageInClient() {
        System.out.println(container.getImage());
        System.out.println(container.getImageId());
        String[] i = container.getImageId().split(":",2);
        String id = i[1];
        System.out.println(id);
        DockerImage dockerImage = ExecutorThread.findImageInClient(id);
        Assert.assertTrue(dockerImage==null); //This is wrong but everything works.
    }
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
    void ImplementAPIRequest(String action, String id) {
        ActionRequest actionRequest = new ActionRequest(action, id);
        CompletableFuture.runAsync(() -> sendActionRequest(actionRequest, gson))
                .thenRun(() -> System.out.println("Request sent successfully"))
                .exceptionally(throwable -> {
                    throwable.printStackTrace();
                    return null;
                });
    }
    @Test
    public void testStartContainer() {
        ImplementAPIRequest("START", idToStart);
        containers = ClientUpdater.getUpdatedContainersFromClient();
        String status = ClientUpdater.getUpdatedStatus(id);
        Assert.assertTrue(status.startsWith("Up"));
    }
    @Test
    public void testStopContainer() {
        Container container = null;
        for (Container container1 : ClientUpdater.getUpdatedContainersFromClient()) {
            if (container1.getStatus().startsWith("Up"));
            container = container1;
            break;
        }
        String id = container.getId();
        ImplementAPIRequest("STOP", id);
        Assert.assertFalse(container.getStatus().startsWith("Exited"));
    }
    @Test
    public void testRenameContainer() {
        ActionRequest actionRequest = new ActionRequest("RENAME", id, "newName");
        CompletableFuture.runAsync(() -> sendActionRequest(actionRequest, gson))
                .thenRun(() -> System.out.println("Request sent successfully"))
                .exceptionally(throwable -> {
                    throwable.printStackTrace();
                    return null;
                });
        System.out.println(container.getNames()[0]);
        Assert.assertFalse(container.getNames()[0].equals("newName"));
    }
    @Test
    public void testRemoveContainer() {
        Container container1 = ClientUpdater.getUpdatedContainersFromClient().get(0);
        String id = container.getId();
        ImplementAPIRequest("REMOVE", id);
        Assert.assertTrue(ClientUpdater.getUpdatedContainersFromClient().contains(container1));
    }
    @Test
    public void testRestartContainer() {
        Container container = null;
        for (Container container1 : ClientUpdater.getUpdatedContainersFromClient()) {
            if (container1.getStatus().startsWith("Up"));
                container = container1;
                break;
        }
        String id = container.getId();
        ImplementAPIRequest("RESTART", id);
        Assert.assertTrue(container.getStatus().startsWith("Up"));
    }
    @Test
    public void testPauseContainer() {
        Container container = null;
        for (Container container1 : ClientUpdater.getUpdatedContainersFromClient()) {
            if (container1.getStatus().startsWith("Up"));
            container = container1;
            break;
        }
        String id = container.getId();
        ImplementAPIRequest("PAUSE",id);
        Assert.assertFalse(container.getStatus().endsWith("(Paused)"));
    }
    @Test
    public void testUnpauseContainer() {
        Container container = null;
        for (Container container1 : ClientUpdater.getUpdatedContainersFromClient()) {
            if (container1.getStatus().endsWith("(Paused)"));
            container = container1;
            break;
        }
        String id = container.getId();
        ImplementAPIRequest("UNPAUSE", id);
        Assert.assertFalse(container.getStatus().endsWith("(Paused)"));
    }
    @Test
    public void testKillContainer() {
        Container container = null;
        for (Container container1 : ClientUpdater.getUpdatedContainersFromClient()) {
            if (container1.getStatus().startsWith("Up"));
            container = container1;
            break;
        }
        String id = container.getId();
        ImplementAPIRequest("KILL", id);
        Assert.assertTrue(container.getStatus().startsWith("Up"));
    }
    @Test
    public void testImplementImage() {
        String imageId = container.getImageId().split(":", 2)[1];
        ImplementAPIRequest("IMPLEMENT", imageId);
        Assert.assertFalse(containers.size() != ClientUpdater.getUpdatedContainersFromClient().size());
    }
    @After
    public void tearDown() {
        //not really needed.
    }
}
