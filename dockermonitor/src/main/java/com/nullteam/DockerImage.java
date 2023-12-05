package com.nullteam;

import com.github.dockerjava.api.command.CreateContainerCmd;
import com.github.dockerjava.api.model.Container;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class DockerImage {
    /**
     * List with all the Images
     * That's why they're final
     */
    public static List<DockerImage> all_images = new ArrayList<>();
    // These fields won't ever change
    private final String imageRep; //mage Rep = Image Name
    private final String imageTag; //Tag = Version
    private final String imageId; //Image ID

    public DockerImage(final String imageRep, final String imageTag,
                       final String imageId) {
        this.imageRep = imageRep;
        this.imageTag = imageTag;
        this.imageId = imageId;
        all_images.add(this); //Adding to the list a DockerImage Object
    }
    //getters
    /**
     * Gets the Repository of the image.
     * @return the Repository.
     */
    public String getImageRep() {
        String[] parts = this.imageRep.split("@");
        return parts[0]; //to keep only the name of the repository
    }
    /**
     * Gets the Tag / version of the image.
     * @return "latest"
     */
    public String getImageTag() {
        return imageTag;
    }
    /**
     * Gets the ID of the image.
     * @return ID
     */
    public String getImageId() {
        return imageId;
    }
    //Classic toString for one DockerImage at the time
    @Override
    public String toString() {
        return "REPOSITORY: " + this.getImageRep() +  "  TAG: "
                + this.getImageTag() + "  IMAGE ID: " + this.getImageId();
    }
    //helpful methods
    /**
     * Images Menu: case 1 (Show All Images)
     * This method prints all images with numbers in the
     * beginning to make the output more user-friendly.
     */
    public static void listAllImages() {
        System.out.println("Listing all the images...\n.\n.\n.");
        int num = 0; //Numbers to make the output more User Friendly
        for (DockerImage img : all_images) {
            num++;
            System.out.println(num + ") "
                    + img.toString()); //toString inside a for loop
        }
    }
    /*this method helps to match the numbers
      form the printed images with their id */
    public static String chooseAnImage() {
        DockerImage.listAllImages(); //User sees his images with numbers
        Scanner in = new Scanner(System.in);  //chooses the image by typing the
        System.out.print("YOUR CHOICE---> "); //number next to the one they want
        int choice = in.nextInt(); //We find out the id of this specific image
        return all_images.get(choice - 1).getImageId();
    }
    //this method creates and starts a container
    public void implementImage() {
        CreateContainerCmd createContainerCmd =
                ClientUpdater.getUpdatedClient().createContainerCmd(
                        getImageRep());
        createContainerCmd.exec(); //new container has been created
        List<Container> containers =  //We get the updated container list
                ClientUpdater.getUpdatedContainersFromClient();
        Container c = null;
        for (Container container : containers) {
            if (container.getStatus().startsWith("Created")) {
                c = container;
            }
        } //we create a new DockerInstance object with the created container
        DockerInstance newContainer = new DockerInstance(
                c.getNames()[0], c.getId(), c.getImage(), c.getStatus());
        String containerIdStart = newContainer.getContainerId();
        ExecutorThread executor_start = new ExecutorThread(
                containerIdStart, ExecutorThread.TaskType.START);
        executor_start.start(); //we start the container
        try {
            executor_start.join(); // waiting for the thread to finish
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("The new container has been created and is running"
                + "\n");
    }
}
