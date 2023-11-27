package com.nullteam;

import java.util.ArrayList;
import java.util.List;
public class DockerImage {
    private static List<DockerImage> all_images = new ArrayList<>(); //Lista me oles tis eikones
    private String imageId;
    private String imageName;
    private String imageTag;

    public DockerImage(String imageId, String imageName, String imageTag) {
        this.imageId = imageId;
        this.imageName = imageName;
        this.imageTag = imageTag;
        all_images.add(this);
    }
    //getters
    public String getImageName() {
        return imageName;
    }
    public String getImageTag() {
        return imageTag;
    }
    public String getImageId() {
        return imageId;
    }
    @Override
    public String toString() {
        return "Image ID: " + imageId + "\nImage Name: " + imageName + "\nImage Tag: " + imageTag;
    }
}
