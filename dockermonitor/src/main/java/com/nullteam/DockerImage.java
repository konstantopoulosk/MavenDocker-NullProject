/**
 * Package for our .java files
 */
package com.nullteam;

import com.github.dockerjava.api.command.CreateContainerCmd;
import com.github.dockerjava.api.model.Container;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class DockerImage {
    /**
     * List of all the DockerImage objects,
     * all the images in the DockerDesktop.
     */
    public static List<DockerImage> imageslist = new ArrayList<>();
    // These fields won't ever change
    // That's why they are final
    /**
     * A field for the Repository where the image is located.
     * Also known as the name of the image.
     */
    private final String imageRep;
    /**
     * A field for the Tag (=version) of the image.
     * Usually it's "latest".
     */
    private final String imageTag;
    /**
     * A field for the Image ID.
     */
    private final String imageId;

    /**
     * Constructor for Class DockerImage.
     * It creates a new DockerImage object
     * and adds it to the imageslist.
     * @param iRep String
     * @param iTag String
     * @param iId String
     */
    public DockerImage(final String iRep, final String iTag,
                       final String iId) {
        this.imageRep = iRep;
        this.imageTag = iTag;
        this.imageId = iId;
        imageslist.add(this); //Adding to the list a DockerImage Object
    }
    //getters
    /**
     * Gets the Repository of the image.
     * @return String
     */
    public String getImageRep() {
        String[] parts = this.imageRep.split("@");
        return parts[0]; //to keep only the name of the repository
    }
    /**
     * Gets the Tag / version of the image.
     * @return String
     */
    public String getImageTag() {
        return imageTag;
    }
    /**
     * Gets the ID of the image.
     * @return String
     */
    public String getImageId() {
        return imageId;
    }
    //Classic toString for one DockerImage at the time

    /**
     * A classic toString method.
     * We use it to show every image's information
     * (repository, tag, id)
     * @return String
     */
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
        for (DockerImage img : imageslist) {
            num++;
            System.out.println(num + ") "
                    + img.toString()); //toString inside a for loop
        }
    }
    /**
     * This method helps to match the numbers from
     * the printed images with their ID.
     * @return String
     */
    public static String chooseAnImage() {
        DockerImage.listAllImages(); //User sees his images with numbers
        Scanner in = new Scanner(System.in);  //chooses the image by typing the
        System.out.print("YOUR CHOICE---> "); //number next to the one they want
        int choice = in.nextInt(); //We find out the id of this specific image
        return imageslist.get(choice - 1).getImageId();
    }
    /**
     * This method creates and starts an instance of the selected image
     * (creates a new container of the image).
     */
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
        ExecutorThread executorStart = new ExecutorThread(
                containerIdStart, ExecutorThread.TaskType.START);
        executorStart.start(); //we start the container
        try {
            executorStart.join(); // waiting for the thread to finish
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("The new container has been created and is running"
                + "\n");
    }
}
