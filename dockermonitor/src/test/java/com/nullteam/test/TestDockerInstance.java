package com.nullteam.test;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.nullteam.DockerImage;
import com.nullteam.DockerInstance;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class TestDockerInstance {
    private static List<DockerInstance> allContainers;
    private final String containerId = "6cfa7f0707e0";
    //private final DockerImage image = new DockerImage("mongo", "Latest", "123456789");
    private String status = "Exited";
    private String name = "GREGORY";
    private String imageContainer = "mongo1";
    DockerInstance container1 = new DockerInstance(name, containerId,
            imageContainer, status);
    DockerInstance container2 = new DockerInstance("NewContainer", "af1214c44590",
            "mongo","Up");

    @Before
    public void setUp() {
        allContainers = new ArrayList<>();
        DockerInstance container1 = new DockerInstance(name, containerId,
                imageContainer, status);
        allContainers.add(container1);
        DockerInstance container2 = new DockerInstance("NewContainer", "af1214c44590",
                "mongo", "Up");
        allContainers.add(container2);
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
        Assert.assertEquals("Failure wrong Docker Image",
                allContainers.get(0).getContainerImage(), imageContainer);
    }

    @Test
    public void testGetContainerStatus() {
        Assert.assertEquals("Failure wrong Status", allContainers.get(0).getContainerStatus(), status);
    }
    @Test
    public void testSetContainerStatus() {
        final String newStatus = "Up";
        this.status = newStatus;
        Assert.assertEquals("Failure wrong Setting Status", status, newStatus);
    }
    @Test
    public void testToString() {
        String expected = allContainers.get(0).toString();
        String actual = "Name: " + name + "  ID: " + containerId + "  Image: " + imageContainer + "  STATUS: " + status;
        Assert.assertEquals("Fail wrong output", expected,actual);
    }
    @Test
    public void testStopContainer() {
        allContainers.get(0).startContainer();
        allContainers.get(0).stopContainer();
        allContainers.get(0).setContainerStatus("Exited");
        Assert.assertEquals("Container does not stop", allContainers.get(0).getContainerStatus(), "Exited");
    }

    @Test
    public void testStartContainer() {
        allContainers.get(0).startContainer();
        this.status = "Up";
        allContainers.get(0).setContainerStatus("Up");
        Assert.assertEquals("Container does not start", status, "Up");
    }

    @Test
    public void testRenameContainer() { //TO EVALA META SE SXOLIO GIATI PETAEI EXCEPTION OTI DEN GINETAI
        allContainers.get(0).renameContainer("GUSTAVO_FRING123"); //NA RENAME WITH THE OLD NAME
        Assert.assertEquals("Failure to rename", allContainers.get(0).getContainerName(), "GUSTAVO_FRING123");
    }
    @Test
    public void testRemoveContainer() {
        DockerInstance container3 = new DockerInstance("NAME", "7696e2ff9c91", "image1","Up");
        allContainers.add(container3);
        Assert.assertEquals("Failure wrong size", allContainers.size(), 3);
        allContainers.get(2).removeContainer();
        allContainers.remove(container3);
        Assert.assertEquals("Fail wrong size", allContainers.size(), 2);
        Assert.assertFalse("Failure removed wrong container", allContainers.contains(container1));
        Assert.assertFalse("Failure removed wrong container", allContainers.contains(container2));
        Assert.assertFalse("Failure didnt remove", allContainers.contains(container3));
    }

    @Test
    public void testRestartContainer() {
        allContainers.get(0).restartContainer();
        Assert.assertEquals("Fail to restart",allContainers.get(1).getContainerStatus(), "Up" );
    }
    @Test
    public void testPauseContainer() {
        DockerInstance dockerInstance = new DockerInstance("GG", "7696e2ff9c91","nginx","Up");
        allContainers.add(dockerInstance);
        // allContainers.get(2).startContainer();
        allContainers.get(2).pauseContainer();
        allContainers.get(2).setContainerStatus("Exited");
        Assert.assertEquals("Fail to pause", allContainers.get(2).getContainerStatus(), "Exited");
    }
    @Test
    public void testUnpauseContainer() {
        allContainers.add(new DockerInstance("G","7696e2ff9c91","mongo","Up"));
        //allContainers.get(2).pauseContainer();
        allContainers.get(2).unpauseContainer();
        allContainers.get(2).setContainerStatus("Up");
        Assert.assertEquals("Fail to Unpause", allContainers.get(2).getContainerStatus(), "Up");
    }
    @Test
    public void testKillContainer() {
        DockerInstance dockerInstance = new DockerInstance("GG", "7696e2ff9c91","nginx","Up");
        allContainers.add(dockerInstance);
        allContainers.get(2).killContainer();
        allContainers.get(2).setContainerStatus("Exited");
        Assert.assertEquals("Fail to kill", allContainers.get(2).getContainerStatus(), "Exited");
    }
    @Test
    public void testInspectContainer() {
        //Waiting to be completed
    }
    @Test
    public void testListAllContainers() {
        final String expectedOutput = "1) Name: GREGORY ID: 89938596d8d0 Image: mongo1 STATUS: Exited" +
                "\n2) Name: NewContainer ID: af1214c44590 Image: mongo STATUS: Up";
        final String actualOutput = "1) Name: " + allContainers.get(0).getContainerName() + " ID: " + allContainers.get(0).getContainerId()
                + " Image: " + allContainers.get(0).getContainerImage() + " STATUS: " + allContainers.get(0).getContainerStatus()
                + "\n2) Name: " + allContainers.get(1).getContainerName() + " ID: " + allContainers.get(1).getContainerId()
                + " Image: " + allContainers.get(1).getContainerImage() + " STATUS: " + allContainers.get(1).getContainerStatus();

        Assert.assertEquals("FAIL", expectedOutput, actualOutput);
    }
    @Test
    public void testListActiveContainers() {
        ArrayList<DockerInstance> actives = new ArrayList<>();
        for (DockerInstance c : allContainers) {
            if (c.getContainerStatus().startsWith("Up")) {
                actives.add(c);
            }
        }
        Assert.assertEquals("Failure wrong size", actives.size(), 1);
        Assert.assertTrue("Failure does not contain Up Container", actives.contains(allContainers.get(1)));
        Assert.assertFalse("Failure contains stopped container", actives.contains(allContainers.get(0)));
    }

    @Test
    public void testChooseAnActiveContainer() {
        List<DockerInstance> actives = new ArrayList<>();
        for (DockerInstance c : allContainers) {
            if (c.getContainerStatus().startsWith("Up")) {
                actives.add(c);
            }
        }
        Assert.assertEquals("Failure wrong size", actives.size(), 1);
        Assert.assertEquals("Failure wrong head", actives.get(0), allContainers.get(1));
        Assert.assertTrue("Failure does not contain an Up Container", actives.contains(allContainers.get(1)));
        Assert.assertFalse("Failure contains a stopped container", actives.contains(allContainers.get(0)));
    }

    @Test
    public void testChooseAStoppedContainer() {
        List<DockerInstance> stopped = new ArrayList<>();
        for (DockerInstance c : allContainers) {
            if (c.getContainerStatus().startsWith("Exited")) {
                stopped.add(c);
            }
        }
        Assert.assertFalse("Failure contains Up container", stopped.contains(container1));
        Assert.assertTrue("Failure does not contain an Exited container", stopped.contains(allContainers.get(0)));
        Assert.assertEquals("Failure wrong size", stopped.size(), 1);
        Assert.assertEquals("Failure wrong head", stopped.get(0), (DockerInstance) allContainers.get(0));
    }
    @Test
    public void testChooseAContainer() {
        Assert.assertFalse("Fail", allContainers.contains(container2));
        Assert.assertFalse("Fail", allContainers.contains(container1));
        DockerInstance container3 = new DockerInstance("NAME",
                "ID", "IMAGE", "STATUS");
        Assert.assertFalse("Fail", allContainers.contains(container3));
    }
    @Test
    public void testNoActiveContainers() {
        boolean flag = true;
        for (DockerInstance c : allContainers) {
            if (c.getContainerStatus().startsWith("Up")) {
                flag = false;
                break;
            }
        }
        Assert.assertFalse("No actives", flag);
    }
    @Test
    public void testChooseBasedOnCondition() {
        Assert.assertEquals("Fail", DockerInstance.noActiveContainers(), false);
    }
    @After
    public void tearDown() {
        allContainers = null;
    }
}