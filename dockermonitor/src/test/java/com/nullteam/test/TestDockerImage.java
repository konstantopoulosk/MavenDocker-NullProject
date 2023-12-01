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
    private final DockerImage dockerImage = new DockerImage(imageId,imageName,imageTag);

    @Before
    public void setUp() {
        allImages = new ArrayList<>();
        allImages.add(new DockerImage(imageId,imageName,imageTag));
    }
    @Test
    public void testGetImageName() {
        Assert.assertEquals("Failure - wrong Name" , allImages.get(0).getImageName(), "nginx");
    }
    @Test
    public void testGetImageTag() {
        Assert.assertEquals("Failure - wrong Tag", allImages.get(0).getImageTag(), "Latest");
    }
    @Test
    public void testGetImageId() throws NullPointerException {
        Assert.assertEquals("Failure - wrong Id",
                allImages.get(0).getImageId().toString(),
                "593aee2afb64");
    }
    @Test
    public void testToString() {
        Assert.assertEquals("Failure wrong to String", allImages.get(0).toString(),dockerImage.toString());
        final DockerImage dockerImage1 = new DockerImage("123456789", "Grigoris","Latest");
        allImages.add(dockerImage1);
        Assert.assertEquals("Failure wrong toString", allImages.get(0).toString() + ", " + allImages.get(1).toString(),
                (dockerImage.toString() + ", " + dockerImage1.toString()));
    }
    @Test
    public void testConstructor() {
            Assert.assertEquals("Failure wrong size", allImages.size(), 1);
    }
    @After
    public void tearDown() {
        allImages = null;
    }
}
