package com.nullteam.test;

import com.nullteam.DockerInstance;
import com.nullteam.DockerMonitor;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
public class TestDockerMonitor {
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
}
