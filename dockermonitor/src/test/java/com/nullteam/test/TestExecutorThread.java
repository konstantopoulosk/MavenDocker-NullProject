package com.nullteam.test;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.cfg.ContextAttributes;
import com.github.dockerjava.api.model.Container;
import com.google.gson.Gson;
import com.nullteam.*;
import com.sun.net.httpserver.HttpServer;
import org.junit.*;

import javax.ws.rs.client.Client;
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
    private Container container = containers.getFirst();
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
                break;
            } else {
                idToStart = c.getId();
                break;
            }
        }
    }
    @Test
    public void testFindContainerInClient() {
        DockerInstance dockerInstance1 = ExecutorThread.findContainerInClient(id);
        String id1 = dockerInstance1.getContainerId();
        Assert.assertTrue(ClientUpdater.getUpdatedContainersFromClient() != null);
    }
    @Test
    public void testFindImageInClient() {
        System.out.println(container.getImage());
        System.out.println(container.getImageId());
        String[] i = container.getImageId().split(":",2);
        String id = i[1];
        System.out.println(id);
        DockerImage dockerImage = ExecutorThread.findImageInClient(id);
        Assert.assertTrue(ClientUpdater.getUpdatedContainersFromClient() != null);
    }
    @Test
    public void testStopContainer() {
        Assert.assertTrue(ClientUpdater.getUpdatedContainersFromClient() != null);;
    }
    @Test
    public void testRenameContainer() {
        Assert.assertTrue(ClientUpdater.getUpdatedContainersFromClient() != null);
    }
    @Test
    public void testRemoveContainer() {
        Container container1 = ClientUpdater.getUpdatedContainersFromClient().get(0);
        String id = container.getId();
        Assert.assertTrue(ClientUpdater.getUpdatedContainersFromClient() != null);
    }
    @Test
    public void testRestartContainer() {
        Container container = null;
        for (Container container1 : ClientUpdater.getUpdatedContainersFromClient()) {
            if (container1.getStatus().startsWith("Up"));
                container = container1;
                break;
        }
        Assert.assertTrue(ClientUpdater.getUpdatedContainersFromClient() != null);
    }
    @Test
    public void testPauseContainer() {
        Container container = null;
        for (Container container1 : ClientUpdater.getUpdatedContainersFromClient()) {
            if (container1.getStatus().startsWith("Up"));
            container = container1;
            break;
        }
        Assert.assertTrue(ClientUpdater.getUpdatedContainersFromClient() != null);
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
        Assert.assertTrue(ClientUpdater.getUpdatedContainersFromClient() != null);
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
        Assert.assertTrue(ClientUpdater.getUpdatedContainersFromClient() != null);
    }
    @Test
    public void testImplementImage() {
        String imageId = container.getImageId().split(":", 2)[1];
        Assert.assertTrue(ClientUpdater.getUpdatedContainersFromClient() != null);
    }
    @After
    public void tearDown() {
        //not really needed.
    }
}
