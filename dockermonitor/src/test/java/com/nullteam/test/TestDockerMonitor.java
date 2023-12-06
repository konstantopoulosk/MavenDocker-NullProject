package com.nullteam.test;

import com.nullteam.DockerInstance;
import com.nullteam.DockerMonitor;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.beans.Transient;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
public class TestDockerMonitor {
    DockerMonitor obj = new DockerMonitor();
    private List<String[]> containers = new ArrayList<>();
    private String name = "Name";
    private String id = "Id";
    private String image = "image";
    private String status = "Up";
    String[] array = { name , id , image , status };
    @Before
    public void setUp() {
        containers.add(array);
    }
    @Test
    public void testHasNewData() {
        String[] container2 = {"Name1" , "Id", "image", "status"};
        List<String[]> array2 = new ArrayList<>();
        array2.add(container2);
    }
    @Test
    public void testWriteCsv() {
        obj.run();
        File file = new File("containers.csv");
        Assert.assertTrue(file.exists());
        Assert.assertTrue("The file is empty", file.length() > 0);
        //List<String[]> csvContent = readCsvFile(file);
        //Assert.assertArrayEquals(new String[]{"Container ID", "Name", "Image", "Status", "Command", "Created"}, csvContent.get(0));
        //Assert.assertArrayEquals(new String[]{"id1", "Name1", "Image1", "Status1", "Command1", "Created1"}, csvContent.get(1));
        //Assert.assertArrayEquals(new String[]{"id2", "Name2", "Image2", "Status2", "Command2", "Created2"}, csvContent.get(2))
    }
    public void testListsAreEqual() {
        List<String[]> list1 = new ArrayList<>();
        List<String[]> list2 = new ArrayList<>();
        String[] data1 = new String[]{"id1", "Name1", "Image1", "Status1", "Command1", "Created1"};
        String[] data2 = new String[]{"id2", "Name2", "Image2", "Status2", "Command2", "Created2"};
        list1.add(data1);
        list2.add(data2);
        boolean res = obj.listsAreEqual(list1, list2);
        Assert.assertTrue("Lists aren't equal", res);

    }
}
