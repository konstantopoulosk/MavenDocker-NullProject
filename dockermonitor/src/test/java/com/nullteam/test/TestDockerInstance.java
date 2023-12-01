package com.nullteam.test;

import com.nullteam.DockerImage;
import com.nullteam.DockerInstance;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class TestDockerInstance {
    private static List<DockerInstance> allContainers;
    private final String containerId = "89938596d8d0";
    private final DockerImage image = new DockerImage("ee3b4d1239f1", "mongo", "Latest");
    private String status = "Exited";
    private String name = "GREGORY";

    @Before
    public void setUp() {
        allContainers = new ArrayList<>();
        allContainers.add(new DockerInstance(name, containerId,
                new DockerImage("ee3b4d1239f1", "mongo", "Latest"), status));
    }

    @Test
    public void testGetContainerId() {
        Assert.assertEquals("Failure wrong Id", allContainers.get(0).getContainerId(), containerId);
    }

    @Test
    public void testGetContainerName() {
        Assert.assertEquals("Failure wrong Name", allContainers.get(0).getContainerName(), name);
    }

    @Test
    public void testGetContainerImage() {
        Assert.assertEquals("Failure wrong Docker Image", allContainers.get(0).getContainerImage(), image);
    }

    @Test
    public void testGetContainerStatus() {
        Assert.assertEquals("Failure wrong Status", allContainers.get(0).getContainerStatus(), status);
    }

    @Test
    public void testSetContainerStatus() {
        /*
        DockerInstance container = new DockerInstance("NewContainer", "123456",
                new DockerImage("456789", "mongo", "Latest"), "Exited");

         */
        final String newStatus = "Up";
        this.status = newStatus;
        Assert.assertEquals("Failure wrong Setting Status", allContainers.get(0).getContainerStatus(), newStatus);
    }

    @Test
    public void testStopContainer() {
        Assert.assertEquals("Container does not stop", allContainers.get(0).getContainerStatus(), "Exited");
    }

    @Test
    public void testStartContainer() {
        Assert.assertEquals("Container does not start", allContainers.get(0).getContainerStatus(), "Up");
    }

    @Test
    public void testRenameContainer() {
        final String newName = "GUSTAVO FRING";
        this.name = newName;
        Assert.assertEquals("Failure to rename", allContainers.get(0).getContainerName(), newName);
    }

    @Test
    public void testListAllContainers() {
        DockerInstance container = new DockerInstance("NewContainer", "123456",
                new DockerImage("456789", "mongo", "Latest"), "Exited");
        allContainers.add(container);
        final String expectedOutput = "1) Name: GREGORY ID: 89938596d8d0 Image: mongo STATUS: Exited" +
                "\n2) Name: NewContainer ID: 123456 Image: mongo STATUS: Exited";
        final String actualOutput = "1) Name" + allContainers.get(0).getContainerName() + " ID: " + allContainers.get(0).getContainerId()
                + " Image: " + allContainers.get(0).getContainerImage().getImageName() + " STATUS: " + allContainers.get(0).getContainerStatus()
                + "\n2) Name" + allContainers.get(1).getContainerName() + " ID: " + allContainers.get(1).getContainerId()
                + " Image: " + allContainers.get(1).getContainerImage().getImageName() + " STATUS: " + allContainers.get(1).getContainerStatus();

        Assert.assertEquals("FAIL", expectedOutput, actualOutput);
    }

    @Test
    public void testListActiveContainers() {
        // Redirect output to a string buffer to capture the output
        java.io.ByteArrayOutputStream outContent = new java.io.ByteArrayOutputStream();
        System.setOut(new java.io.PrintStream(outContent));
        DockerInstance container = new DockerInstance("NewContainer", "123456",
                new DockerImage("456789", "mongo", "Latest"), "Up");
        allContainers.add(container);
        final String expectedOutput = "1) Name: NewContainer ID: 123456 Image: mongo STATUS: Exited";
        String actualOutput = "1) Name: " + container.getContainerName() + " ID: " + container.getContainerId() +
                " Image: " + container.getContainerImage().getImageName() + " STATUS: " + container.getContainerStatus();
        if (allContainers.get(0).getContainerStatus().startsWith("Up")) {
            actualOutput = "1) Name: " + allContainers.get(0).getContainerName() + " ID: " + allContainers.get(0).getContainerId() +
                    " Image: " + allContainers.get(0).getContainerImage().getImageName() + " STATUS: " + allContainers.get(0).getContainerStatus() +
                    "2) Name: " + allContainers.get(1).getContainerName() + " ID: " + allContainers.get(1).getContainerId() +
                    " Image: " + allContainers.get(1).getContainerImage().getImageName() + " STATUS: " + allContainers.get(1).getContainerStatus();
        }
        Assert.assertEquals("Fail", expectedOutput, actualOutput);
    }

    @Test
    public void testChooseAnActiveContainer() {
        List<DockerInstance> actives = new ArrayList<>();
        DockerInstance container = new DockerInstance("NewContainer", "123456",
                new DockerImage("456789", "mongo", "Latest"), "Up");
        allContainers.add(container);
        for (DockerInstance c : allContainers) {
            if (c.getContainerStatus().startsWith("Up")) {
                actives.add(c);
            }
        }
        Assert.assertEquals("Failure wrong size", actives.size(), 1);
        Assert.assertEquals("Failure wrong head", actives.getFirst(), container);
        Assert.assertTrue("Failure does not contain an Up Container", actives.contains(container));
        Assert.assertFalse("Failure contains a stopped container", actives.contains(allContainers.get(0)));
    }

    @Test
    public void testChooseAStoppedContainer() {
        List<DockerInstance> stopped = new ArrayList<>();
        DockerInstance container = new DockerInstance("NewContainer", "123456",
                new DockerImage("456789", "mongo", "Latest"), "Up");
        allContainers.add(container);
        for (DockerInstance c : allContainers) {
            if (c.getContainerStatus().startsWith("Exited")) {
                stopped.add(c);
            }
        }
        Assert.assertFalse("Failure contains Up container", stopped.contains(container));
        Assert.assertTrue("Failure does not contain an Exited container", stopped.contains(allContainers.get(0)));
        Assert.assertEquals("Failure wrong size", stopped.size(), 1);
        Assert.assertEquals("Failure wrong head", stopped.getFirst(), (DockerInstance) allContainers.get(0));
    }
}

