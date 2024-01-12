package com.nullteam.test;

import com.nullteam.DockerMonitor;
import com.opencsv.CSVReader;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
public class TestDockerMonitor {
    List<String[]> containers1 = new ArrayList<>(); //One List for LastState
    List<String[]> containers2 = new ArrayList<>(); //One List for Current Data
    private final String name = "Name"; // Name of container
    private final String id = "Id"; //ID of Container.
    private final String image = "image"; //Image of Container.
    private final String status = "status"; //Status of Container.
    String[] array1 = { name , id , image , status }; // Make those elements and Array
    String[] array2 = {"Name1" , "Id", "image", "status"}; //Another array for current Data
    DockerMonitor obj = new DockerMonitor(); //Object of Docker Monitor.
    @Before
    public void setUp() { //Initialize
        containers1.add(array1); //add to last state the array1
        containers2.add(array2); //add to current data the array2
        obj.start();
    }
    @Test
    public void testSetLastState() {
        List<String[]> list = new ArrayList<>();
        list.add(new String[]{"NULL"});
        this.containers1 = list;
        Assert.assertEquals(containers1, list);
        list.add(new String[]{"Name1", "Id", "image", "status"});
        Assert.assertEquals(containers1, list);

    }
    @Test
    public void testSetCurrentData() {
        List<String[]> list = new ArrayList<>();
        list.add(new String[]{"NULL"});
        this.containers2 = list;
        Assert.assertEquals(containers2, list);
        list.add(new String[]{"Name1", "Id", "image", "status"});
        Assert.assertEquals(containers2, list);
    }
    @Test
    public void testWriteCsv() {
        obj.setCurrentData(containers1);
        obj.writeCsv();
        String s = readCsvFile();
        Assert.assertNotNull(s);
    }
    private String readCsvFile() {
        final String csvFilePath = "containers.csv";
        Path path = Paths.get(csvFilePath);
        try {
            byte[] fileBytes = Files.readAllBytes(path);
            return new String(fileBytes);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Test
    public void testHasNewData() {
        DockerMonitor dockerMonitor = new DockerMonitor();
        Assert.assertTrue("Fail", dockerMonitor.hasNewData());
    }
    @Test
    public void testListsAreEqual() {
        DockerMonitor dockerMonitor = new DockerMonitor();
        Assert.assertFalse("They Have the same content", dockerMonitor.listsAreEqual(containers1,containers2));
        List<String[]> containers3 = new ArrayList<>();
        String[] array3 = {"Name", "Id", "image", "status"};
        containers3.add(array3);
        Assert.assertTrue("They have different content", dockerMonitor.listsAreEqual(containers1,containers3));
    }
    @After
    public void tearDown() {
        containers1 = null;
        containers2 = null;
    }
}
