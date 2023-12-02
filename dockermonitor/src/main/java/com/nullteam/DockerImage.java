package com.nullteam;

import com.github.dockerjava.api.command.CreateContainerCmd;
import com.github.dockerjava.api.model.Container;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class DockerImage {
    public static List<DockerImage> all_images = new ArrayList<>(); //List with all the Images
    private final String imageRep; //Image Rep = Image Name // These fields won't ever change
    private final String imageTag; //Tag = Version  // That's why they're final
    private final String imageId; //Image ID

    public DockerImage(String imageRep, String imageTag, String imageId) {
        this.imageRep = imageRep;
        this.imageTag = imageTag;
        this.imageId = imageId;
        all_images.add(this); //Adding to the list a DockerImage Object
    }
    //getters
    public String getImageRep() {
        String[] parts = this.imageRep.split("@");
        return parts[0]; //to keep only the name of the repository
    }
    public String getImageTag() {
        return imageTag; //Image Tag = Version ---> Usually "Latest"
    }
    public String getImageId() {
        return imageId; //returns the Image ID
    }
    @Override
    public String toString() { //For one DockerImage at the time. Classic toString
        return "REPOSITORY: " + this.getImageRep() +  "  TAG: " + this.getImageTag()
                + "  IMAGE ID: " + this.getImageId() ;
    }

    //helpful methods

    //this method prints all images with numbers in the beginning
    //Images Menu: case 1 (Show All Images)
    public static void listAllImages() {
        System.out.println("Listing all the images...\n.\n.\n.");
        int num = 0; //Numbers to make the output more User Friendly
        for (DockerImage img : all_images) {
            num++;
            System.out.println(num + ") " + img.toString()); //Using toString inside a for loop
        }
    }
    //this method helps to match the numbers form the printed images with their id
    public static String chooseAnImage() {
        DockerImage.listAllImages(); //Users sees his images with numbers
        Scanner in = new Scanner(System.in); //chooses the image by typing the number next to the image
        System.out.print("YOUR CHOICE---> ");
        int choice = in.nextInt(); //We find out the id of this specific image with this method.
        return all_images.get(choice - 1).getImageId();
    }

    public void implementImage() {  //this method creates and starts a container
        CreateContainerCmd createContainerCmd = ClientUpdater.getUpdatedClient().createContainerCmd(getImageRep());
        createContainerCmd.exec(); //new container has been created
        List<Container> containers = ClientUpdater.getUpdatedContainersFromClient(); //We get the updated container list
        Container c = null;
        for (Container container : containers) {
            if (container.getStatus().startsWith("Created")) {
                c = container;
            }
        } //We have to create a new DockerInstance object with the created container
        DockerInstance newContainer = new DockerInstance(c.getNames()[0], c.getId(), c.getImage(), c.getStatus());
        String containerIdStart = newContainer.getContainerId(); //now we start the container
        ExecutorThread executor_start = new ExecutorThread
                (containerIdStart, ExecutorThread.TaskType.START);
        executor_start.start();
        try {
            executor_start.join(); // waiting for the thread to finish
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("The new container has been created and is running" + "\n");
    }
}