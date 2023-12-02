package com.nullteam;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class DockerImage {
    private static List<DockerImage> all_images = new ArrayList<>(); //Lista me oles tis eikones
    private final String imageRep;                    // These fields won't ever change
    private final String imageTag;                    // That's why they're final
    private final String imageId;

    public DockerImage(String imageRep, String imageTag, String imageId) {
        this.imageRep = imageRep;
        this.imageTag = imageTag;
        this.imageId = imageId;
        all_images.add(this);
    }
    //getters
    public String getImageRep() {
        String[] parts = this.imageRep.split("@");
        return parts[0]; //to keep only the name of the repository
    }
    public String getImageTag() {
        return imageTag;
    }
    public String getImageId() {
        return imageId;
    }
    @Override
    public String toString() {
        return "REPOSITORY: " + getImageRep() +  "  TAG: " + imageTag + "  IMAGE ID: " + imageId ;
    }

    //helpful methods
    
    //this method prints all images with numbers in the beginning
    public static void listAllImages() {
        System.out.println("Listing all the images...\n.\n.\n.");
        int num = 0;
        for (DockerImage img : all_images) {
            num++;
            System.out.println(num + ") " + img.toString());
        }
    }
    //this method helps to match the numbers form the printed images with their id
    public static String chooseAnImage() {
        DockerImage.listAllImages();
        Scanner in = new Scanner(System.in);
        int choice = in.nextInt();
        return all_images.get(choice - 1).getImageId();
    }
    
    //public static void implementAnImage(String imageId) {}  obviously not done yet...
}
