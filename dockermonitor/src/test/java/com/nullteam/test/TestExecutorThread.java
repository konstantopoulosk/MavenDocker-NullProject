package com.nullteam.test;

import com.nullteam.DockerInstance;
import org.junit.*;
import com.nullteam.ExecutorThread;

import java.util.ArrayList;
import java.util.List;

public class TestExecutorThread {
    private List<DockerInstance> c;
    private String name = "Gregory";
    private String id = "6cfa7f0707e0";
    private String image = "mongo";
    private String status = "Up";
    DockerInstance c1 = new DockerInstance(name,id,image,status);
    private String name2 = "GUS";
    private String id2 = "af1214c44590";
    private String image2 = "nginx";
    private String status2 = "Exited";
    DockerInstance c2 = new DockerInstance(name2, id2, image2, status2);
    @Before
    public void setUp() {
        c = new ArrayList<>();
        c.add(c1);
        c.add(c2);
    }
    @Test
    public void testFindContainerInClient() {
        Assert.assertEquals("Fail for c1", c.get(0),c1);
        Assert.assertEquals("Fail for c2", c.get(1),c2);
    }
    @Test
    public void testStartContainer() {
        ExecutorThread e1 = new ExecutorThread(id, ExecutorThread.TaskType.START);
        e1.start();
        try {
            e1.join();
            c.get(0).setContainerStatus("Up");
        } catch (InterruptedException exception) {
            exception.printStackTrace();
        }
        Assert.assertEquals("Fail",c.get(0).getContainerStatus(),"Up");
    }
    @Test
    public void testStopContainer() {
        ExecutorThread e1 = new ExecutorThread(id, ExecutorThread.TaskType.STOP);
        e1.start();
        try {
            e1.join();
            c.get(0).setContainerStatus("Exited");
        } catch (InterruptedException exception) {
            exception.printStackTrace();
        }
        Assert.assertEquals("Fail",c.get(0).getContainerStatus(),"Exited");
    }
    @Test
    public void testRenameContainer() {
        ExecutorThread e1 = new ExecutorThread(id, ExecutorThread.TaskType.RENAME);
        e1.start();
        try {
            e1.join();
            c.get(0).renameContainer("TTT");
        } catch (InterruptedException exception) {
            exception.printStackTrace();
        }
        Assert.assertEquals("Fail", c.get(0).getContainerName().toString(), "TTT");
    }
    @Test
    public void testRemoveContainer() {
        c.add(new DockerInstance("TEST","dcfbd347381a", "mongo", "Up"));
        ExecutorThread e1 = new ExecutorThread("dcfbd347381a", ExecutorThread.TaskType.REMOVE);
        e1.start();
        try {
            e1.join();
        } catch (InterruptedException exception) {
            exception.printStackTrace();
        }
        Assert.assertEquals("Fail Wrong Size", c.size(),3);
    }
    @Test
    public void testRestartContainer() {
        ExecutorThread e1 = new ExecutorThread(id, ExecutorThread.TaskType.RESTART);
        e1.start();
        try {
            e1.join();
            status = "Up";
        } catch (InterruptedException exception) {
            exception.printStackTrace();
        }
        Assert.assertEquals("Fail to restart", status, "Up");
    }
    @Test
    public void testPauseContainer() {
        c.get(0).unpauseContainer();
        ExecutorThread e1 = new ExecutorThread(id, ExecutorThread.TaskType.PAUSE);
        e1.start();
        try {
            e1.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        status = "Paused";
        Assert.assertEquals("Fail to Pause", status, "Paused");
    }
    @Test
    public void testUnpauseContainer() {
        ExecutorThread e1 = new ExecutorThread(id, ExecutorThread.TaskType.UNPAUSE);
        e1.start();
        try {
            e1.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Assert.assertEquals("Fail to Unpause", status , "Up");
    }
    @Test
    public void testKillContainer() {
        c2.startContainer();
        ExecutorThread e1 = new ExecutorThread(id2, ExecutorThread.TaskType.KILL);
        e1.start();
        try {
            e1.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        c.remove(1);
        Assert.assertEquals("Fail to kill", c.size(), 1);
        Assert.assertFalse("Fail", c.contains(c2));
    }
    @Test
    public void testInspectContainer() {

    }
    @Test
    public void testConstructor() {
        DockerInstance c3 = new DockerInstance("name","id","image","status");
        Assert.assertEquals("Failure wrong size", c.size(), 2);
        Assert.assertEquals("Fail wrong head", c.get(0).getContainerId().toString(), id);
        Assert.assertTrue("Fail list does not contain First", c.contains(c1));
        Assert.assertTrue("Fail list does not contain Second", c.contains(c2));
        Assert.assertFalse("Fail list contains unwanted", c.contains(c3));
    }
    @Test
    public void testImplementImage() {

    }
}
