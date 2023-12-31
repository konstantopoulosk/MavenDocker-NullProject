package com.nullteam.test;

import com.nullteam.DockerVolume;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.*;
public class TestDockerVolume {
    private static List<DockerVolume> volumeslist;
    private final String driver = "local";
    private final String name = "02cf53413ed35002a1a56531e43feda3ac39e029fbe635a0cca0cf30cab21a59";
    private final String created = "2023-11-15T18:33:33Z";
    private final String mountpoint = "/var/lib/docker/volumes/02cf53413ed35002a1a56531e43feda3ac39e029fbe635a0cca0cf30cab21a59/_data";
    private final DockerVolume dockerVolume = new DockerVolume(driver, name, created, mountpoint);
    private final DockerVolume dv = new DockerVolume("other",
            "414778454d1b3dfdaf3c58249c8dee5fab72c4d528ff1650544cecb1323d8011", "2022-11-12T11:11:11Z",
            "/var/lib/docker/volumes/414778454d1b3dfdaf3c58249c8dee5fab72c4d528ff1650544cecb1323d8011/_data");
    @Before
    public void setUp() {
        volumeslist = new ArrayList<>();
        volumeslist.add(dockerVolume);
        volumeslist.add(dv);
    }
    @Test
    public void testConstructor() {
        Assert.assertEquals("Failure wrong size", volumeslist.size(), 2);
        Assert.assertEquals("Failure wrong First", volumeslist.getFirst().name(), name);
    }
    @Test
    public void testGetDriver() {
        Assert.assertEquals("Failure - wrong Driver",
                volumeslist.getFirst().driver(), "local");
    }
    @Test
    public void testGetName() {
        Assert.assertEquals("Failure - wrong Name",
                volumeslist.getFirst().name(),
                "02cf53413ed35002a1a56531e43feda3ac39e029fbe635a0cca0cf30cab21a59");
    }
    @Test
    public void testGetCreated() {
        Assert.assertEquals("Failure - wrong Created",
                volumeslist.getFirst().created(),
                "2023-11-15T18:33:33Z");
    }
    @Test
    public void testGetMountpoint() {
        Assert.assertEquals("Failure - wrong Mountpoint",
                volumeslist.getFirst().mountpoint(),
                "/var/lib/docker/volumes/02cf53413ed35002a1a56531e43feda3ac39e029fbe635a0cca0cf30cab21a59/_data");
    }
    @Test
    public void testToString() {
        Assert.assertEquals("Failure wrong to String",
                volumeslist.getFirst().toString(),
                dockerVolume.toString());
    }
    @Test
    public void testShowVolumes() {
        String actual = "1) Driver: local Name: 02cf53413ed35002a1a56531e43feda3ac39e029fbe635a0cca0cf30cab21a59"
                + " CreatedAt: 2023-11-15T18:33:33Z"
                + " Mountpoint: /var/lib/docker/volumes/02cf53413ed35002a1a56531e43feda3ac39e029fbe635a0cca0cf30cab21a59/_data"
                + "2) Driver: other Name: 414778454d1b3dfdaf3c58249c8dee5fab72c4d528ff1650544cecb1323d8011\n"
                + " CreatedAt: 2022-11-12T11:11:11Z"
                + " Mountpoint: /var/lib/docker/volumes/414778454d1b3dfdaf3c58249c8dee5fab72c4d528ff1650544cecb1323d8011/_data\n";
        String expected = "1) " + volumeslist.getFirst().toString() + "\n\n"
                + "2) " + volumeslist.get(1).toString() + "\n";
        Assert.assertEquals("Fail wrong output", expected,actual);
    }
    @Test
    public void testCreatedAt() {
        //no idea what  do to :/
    }
    @After
    public void tearDown() {
        volumeslist = null;
    }
}
