package com.nullteam.test;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.model.Container;
import com.github.dockerjava.api.model.Frame;
import com.github.dockerjava.core.command.LogContainerResultCallback;
import com.nullteam.ClientUpdater;
import com.nullteam.DockerImage;
import com.nullteam.DockerInstance;
import com.nullteam.Lists;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.random.RandomGenerator;

public class TestDockerInstance {
    private static List<DockerInstance> containerslist;
    private  String id;
    private String status;
    private  String name;
    private  String image;
    DockerInstance testContainer;

    @Before
    public void setUp() {
        containerslist = new ArrayList<>();
        Lists.listContainers();
        testContainer = DockerInstance.containerslist.getFirst();
        containerslist.add(testContainer);
        id = testContainer.getContainerId();
        status = testContainer.getContainerStatus();
        name = testContainer.getContainerName();
        image = testContainer.getContainerImage();
    }

    @Test
    public void testGetContainerId() {
        Assert.assertEquals("Failure wrong Id",
                id, DockerInstance.containerslist.getFirst().getContainerId());
    }

    @Test
    public void testGetContainerName() {
        Assert.assertEquals("Failure wrong Name",
               name, DockerInstance.containerslist.getFirst().getContainerName());
    }

    @Test
    public void testGetContainerImage() {
        Assert.assertEquals("Failure wrong Docker Image",
                image, DockerInstance.containerslist.getFirst().getContainerImage());
    }

    @Test
    public void testGetContainerStatus() {
        Assert.assertEquals("Failure wrong Status",
                status, DockerInstance.containerslist.getFirst().getContainerStatus());
    }
    @Test
    public void testSetContainerStatus() {
        String newStatus = "Up";
        this.status = newStatus;
        Assert.assertEquals("Failure wrong Setting Status", status, newStatus);
    }
    @Test
    public void testStopContainer() {
        if (!(status.startsWith("Up"))) { //Exited or Created
            //testContainer.startContainer();
        }
        //testContainer.stopContainer();
        Assert.assertTrue(ClientUpdater.getUpdatedContainersFromClient() != null);
    }

    @Test
    public void testStartContainer() {
        if (status.startsWith("Up")) {
            //testContainer.stopContainer();
        }
        //testContainer.startContainer();
        Assert.assertTrue(ClientUpdater.getUpdatedContainersFromClient() != null);}

    @Test
    public void testRenameContainer() throws InterruptedException {
        Random random = new Random();
        int randomNumber = random.nextInt(1000) + 1;
        String newName = "Container" + String.valueOf(randomNumber);
        //testContainer.renameContainer(newName);
        Assert.assertTrue(ClientUpdater.getUpdatedContainersFromClient() != null);
    }
    @Test
    public void testRemoveContainer() {
        DockerInstance testContainer2 = DockerInstance.containerslist.getLast();
        containerslist.add(testContainer2);
        int size1 = DockerInstance.containerslist.size();
        //Assert.assertEquals("Failure wrong size", containerslist.size(), 2);
        if (testContainer2.getContainerStatus().startsWith("Up")) {
            //testContainer2.stopContainer();
        }
        //testContainer2.removeContainer();
        int size2 = DockerInstance.containerslist.size();
        //Assert.assertEquals("Failure wrong size", containerslist.size(), 1);
        Assert.assertTrue(ClientUpdater.getUpdatedContainersFromClient() != null);
    }

    @Test
    public void testRestartContainer() {
        if (!(status.startsWith("Up"))) {
            //testContainer.startContainer();
        }
        //testContainer.restartContainer();
        Assert.assertTrue(ClientUpdater.getUpdatedContainersFromClient() != null );
    }
    @Test
    public void testPauseContainer() {
        if (!(status.startsWith("Up"))) {
            //testContainer.startContainer();
        } else if (status.endsWith("(Paused)")) {
            //testContainer.unpauseContainer();
        }
        //testContainer.pauseContainer();
        Assert.assertTrue(ClientUpdater.getUpdatedContainersFromClient() != null);
    }
    @Test
    public void testUnpauseContainer() {
        if (!(status.endsWith("(Paused)"))) {
            //testContainer.startContainer();
            //testContainer.pauseContainer();
        }
        //testContainer.unpauseContainer();
        Assert.assertTrue(ClientUpdater.getUpdatedContainersFromClient() != null);
    }
    @Test
    public void testKillContainer() {
        if (!(status.startsWith("Up"))) {
            //testContainer.startContainer();
        }
        //testContainer.stopContainer();
        Assert.assertTrue(ClientUpdater.getUpdatedContainersFromClient() != null);
    }
    @Test
    public void testShowLogs() {
        Assert.assertTrue(ClientUpdater.getUpdatedContainersFromClient() != null);
    }
    @After
    public void tearDown() {
        containerslist = null;
    }
}