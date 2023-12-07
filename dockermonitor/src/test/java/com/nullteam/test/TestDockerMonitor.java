package com.nullteam.test;

import com.nullteam.DockerMonitor;
import com.opencsv.CSVWriter;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
public class TestDockerMonitor {
    private List<String[]> containers1 = new ArrayList<>();
    private List<String[]> containers2 = new ArrayList<>();
    private String name = "Name";
    private String id = "Id";
    private String image = "image";
    private String status = "status";
    String[] array1 = { name , id , image , status };
    String[] array2 = {"Name1" , "Id", "image", "status"};
    DockerMonitor obj = new DockerMonitor();
    @Before
    public void setUp() {
        containers1.add(array1);
        containers2.add(array2);
        //obj.start();
    }
    @Test
    public void testWriteCsv() {
        DockerMonitor dockerMonitor = new DockerMonitor();
        File file = new File("containers.csv");
        //dockerMonitor.writeCsv();
        final String csvFilePath = "containers.csv";
        try(CSVWriter c = new CSVWriter(new FileWriter(csvFilePath,false))){
            c.writeNext(new String[]{"Name", "ID", "Image", "Status"});
            c.writeNext(array1);
            c.writeNext(array2);
            try {
                c.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        Assert.assertTrue("Fail empty", file.length() > 0);
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
