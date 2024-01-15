package com.nullteam.test;

import com.github.dockerjava.api.model.Container;
import com.github.dockerjava.api.model.Image;
import com.github.dockerjava.core.DockerContextMetaFile;
import com.nullteam.*;
import org.jetbrains.annotations.NotNull;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.*;

public class TestDockerImage {
    public static List<com.nullteam.DockerImage> imageslist;
    private String imageRep;
    private String imageTag;
    private String imageId;
    private DockerImage dockerImage;
    @Before
    public void setUp() {
        imageslist = new ArrayList<>();
        Lists.listImage();
        dockerImage = DockerImage.imageslist.getFirst();
        imageslist.add(dockerImage);
        imageRep = dockerImage.getImageRep();
        imageTag = dockerImage.getImageTag();
        imageId = dockerImage.getImageId();
    }
    @Test
    public void testGetImageRep() {
        Assert.assertEquals("Failure - wrong Repository" ,
                imageRep, DockerImage.imageslist.getFirst().getImageRep());
    }
    @Test
    public void testGetImageTag() {
        Assert.assertEquals("Failure - wrong Tag",
                imageTag, DockerImage.imageslist.getFirst().getImageTag());
    }
    @Test
    public void testGetImageId() {
        Assert.assertEquals("Failure - wrong Id",
                imageId, DockerImage.imageslist.getFirst().getImageId());
    }
    @Test
    public void testToString() {
        Assert.assertEquals("Failure wrong to String",
                imageslist.getFirst().toString(),
                DockerImage.imageslist.getFirst().toString());
    }
    @Test
    public void testConstructor() {
        Assert.assertEquals("Failure wrong First", imageslist.getFirst().getImageId(),
                ClientUpdater.getUpdatedImagesFromClient().getFirst().getId().split(":")[1]);
    }
    @Test
    public void testListAllImages() {
        String actual = DockerImage.imageslist.getFirst().toString();
        String expected = "Repository: " + imageRep +  "   Tag: "
                + imageTag + "   ImageID: " + imageId;
        Assert.assertEquals("Fail wrong output", expected,actual);
    }
    @Test
    public void testImplementAnImage() {
        int contSize = ClientUpdater.getUpdatedContainersFromClient().size();
        dockerImage.implementImage();
        int newContSize = ClientUpdater.getUpdatedContainersFromClient().size();
        Assert.assertEquals(newContSize, contSize + 1);
    }
    @Test
    public void testFindContainers() {
        List<Container> expected = dockerImage.findContainers();
        List<Container> actual = new ArrayList<>();
        for (Container c : ClientUpdater.getUpdatedContainersFromClient()) {
            if (c.getImage().equals(imageRep)
                    || c.getImage().equals(imageId.substring(0, 12))
                    || c.getImage().startsWith(imageRep)) {
                actual.add(c);
            }
        }
        Assert.assertEquals(expected, actual);
    }
    @Test
    public void testRemoveImage() {
        DockerImage dockerImage2 = DockerImage.imageslist.getLast();
        int imgSize = DockerImage.imageslist.size();
        List<Container> containers = dockerImage2.findContainers();
        if (containers != null) {
            for (Container c : containers) {
                for (DockerInstance i : DockerInstance.containerslist) {
                    if (c.getId().equals(i.getContainerId())) {
                        if (i.getContainerStatus().startsWith("Up")) {
                            i.stopContainer();
                        }
                        i.removeContainer();
                    }
                    break;
                }
            }
        }
        dockerImage2.removeImage();
        int newImgSize = DockerImage.imageslist.size();
        Assert.assertEquals(newImgSize, imgSize - 1);
        Assert.assertFalse("Failure removed wrong image",
                DockerImage.imageslist.contains(dockerImage2));
    }
    @Test
    public void testListUsedImages() {
        String imageToPull = imageRep; //to save the image name
        dockerImage.removeImage(); //we remove the image
        DockerImage.pullImage(imageToPull); //and pull it
        int size1 = DockerImage.listUsedImages().size(); //it is not in-use
        for (DockerImage img : imageslist) {
            if (img.getImageRep().equals(imageToPull)) {
                img.implementImage(); //now we implement it
                break;
            }
        }
        int size2 = DockerImage.listUsedImages().size(); //it has to be in-use
        Assert.assertFalse(size2 == size1 + 1);
    }
    @Test
    public void testPullImage() {
        String imageToPull = imageRep; //to save the image name
        dockerImage.removeImage(); //we remove an image
        DockerImage.pullImage(imageToPull); //we pull the same image using the saved name
        boolean actual = false;
        boolean expected = false;
        for (DockerImage img : imageslist) {
            if (img.getImageRep().equals(imageToPull)) {
                expected = true;
                break;
            }
        }//we check if the image exists in our list
        for (Image img : ClientUpdater.getUpdatedImagesFromClient()) {
            String[] parts = img.getRepoDigests()[0].split("@");
            if (parts[0].equals(imageToPull)) {
                actual = true;
                break;
            }
        }//we check if the image exists in the Client
        Assert.assertTrue(expected || actual);
    }
    @After //FINISHED
    public void tearDown() {
        imageslist = null;
    }
}