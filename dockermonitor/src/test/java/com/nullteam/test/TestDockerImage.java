package com.nullteam.test;

import com.nullteam.DockerImage;
import org.jetbrains.annotations.NotNull;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.*;

public class TestDockerImage {
    private static List<com.nullteam.DockerImage> allImages;
    private final String imageId = "593aee2afb64";
    private final String imageName = "nginx";
    private final String imageTag = "Latest";
    private final DockerImage dockerImage = new DockerImage(imageName,imageTag,imageId);
    private final DockerImage di = new DockerImage("Gregory", "Latest", "123456");

    @Before
    public void setUp() { //FINISHED
        allImages = new ArrayList<>();
        allImages.add(dockerImage);
        allImages.add(di);
    }
    @Test
    public void testGetImageRep() {
        Assert.assertEquals("Failure - wrong Name" , allImages.get(0).getImageRep(), "nginx");
    }
    @Test
    public void testGetImageTag() {
        Assert.assertEquals("Failure - wrong Tag", allImages.get(0).getImageTag(), "Latest");
    }
    @Test
    public void testGetImageId() throws NullPointerException {
        Assert.assertEquals("Failure - wrong Id",
                allImages.get(0).getImageId(),
                "593aee2afb64");
    }
    @Test
    public void testToString() {
        Assert.assertEquals("Failure wrong to String", allImages.get(0).toString(),dockerImage.toString());
    }
    @Test
    public void testConstructor() {
        Assert.assertEquals("Failure wrong size", allImages.size(), 2);
        Assert.assertEquals("Failure wrong First", allImages.get(0).getImageId(), imageId);
    }
    @Test
    public void testListAllImages() { //FINISHED
        String actual = "1) REPOSITORY: nginx  TAG: Latest  IMAGE ID: 593aee2afb64" +
                "\n2) REPOSITORY: Gregory  TAG: Latest  IMAGE ID: 123456";
        String expected = "1) " + allImages.get(0).toString() + "\n2) "
                + allImages.get(1).toString();
        Assert.assertEquals("Fail wrong output", expected,actual);
    }
    @Test
    public void testChooseAnImage() {
        Assert.assertTrue("Fail", allImages.contains(dockerImage));
        Assert.assertTrue("Fail", allImages.contains(di));
        DockerImage dockerImage1 = new DockerImage("REP",
                "TAG", "ID");
        Assert.assertFalse("Fail", allImages.contains(dockerImage1));
    }
    @Test
    public void testImplementAnImage() {
        /* TODO */
    }
    @After //FINISHED
    public void tearDown() {
        allImages = null;
    }
}