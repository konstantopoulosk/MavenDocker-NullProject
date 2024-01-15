package com.nullteam.test;

import com.github.dockerjava.api.command.InspectVolumeResponse;
import com.github.dockerjava.core.DockerContextMetaFile;
import com.nullteam.ClientUpdater;
import com.nullteam.DockerVolume;
import com.nullteam.Lists;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class TestDockerVolume {
    private static List<DockerVolume> volumesList;
    private String driver;
    private String name;
    private String created;
    private String mountpoint;
    private DockerVolume testVolume;

    @Before
    public void setUp() {
        volumesList = new ArrayList<>();
        Lists.listVolumes();
        testVolume = DockerVolume.volumeslist.getFirst();
        volumesList.add(testVolume);
        driver = testVolume.getDriver();
        name = testVolume.getName();
        created = testVolume.getCreated();
        mountpoint = testVolume.getMountpoint();
    }

    @Test
    public void testGetDriver() {
        Assert.assertEquals("Failure - wrong Driver",
                driver, DockerVolume.volumeslist.getFirst().getDriver());
    }

    @Test
    public void testGetName() {
        Assert.assertEquals("Failure - wrong Name",
                name, DockerVolume.volumeslist.getFirst().getName());
    }
    @Test
    public void testCreated() {
        Assert.assertEquals("Failure - wrong Created",
                created, DockerVolume.volumeslist.getFirst().getCreated());
    }
    @Test
    public void testGetMountpoint() {
        Assert.assertEquals("Failure - wrong Mountpoint",
                mountpoint, DockerVolume.volumeslist.getFirst().getMountpoint());
    }

    @Test
    public void testToString() {
        Assert.assertEquals("Failure wrong to String",
                testVolume.toString(), DockerVolume.volumeslist.getFirst().toString());
    }
    @Test
    public void testCreatedAt() {
        //test the result to make it user-friendly in the actual program
        String date = null;
        try {
            String[] command = {"docker", "volume", "inspect",
                    "--format='{{json .CreatedAt}}'", name};
            Process process = Runtime.getRuntime().exec(command);
            process.waitFor();

            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(process.getInputStream()));

            String line;
            StringBuilder sb = new StringBuilder();
            while ((line = reader.readLine()) != null) {
                sb.append(line).append("\n");
            }
            date =  sb.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println(date);
    }
    @After
    public void tearDown() {
        volumesList = null;
    }
}
