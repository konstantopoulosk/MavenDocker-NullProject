/**
 * Package for our .java files
*/
package com.nullteam;

import com.github.dockerjava.api.command.CreateContainerCmd;
import com.github.dockerjava.api.command.PullImageCmd;
import com.github.dockerjava.api.command.PullImageResultCallback;
import com.github.dockerjava.api.command.RemoveImageCmd;
import com.github.dockerjava.api.model.Container;
import com.github.dockerjava.api.model.Image;
import com.github.dockerjava.api.model.PullResponseItem;

import java.util.ArrayList;
import java.util.InputMismatchException;
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
        String[] parts = this.imageId.split(":");
        return parts[1]; //to keep only the id without the 'sha256'
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
        return "REPOSITORY: " + this.getImageRep() +  "   TAG: "
                + this.getImageTag() + "   IMAGE ID: " + this.getImageId();
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
        DockerImage.listAllImages(); //Users sees his images with numbers
        Scanner in = new Scanner(System.in); //chooses the image by typing the number next to the image
        System.out.print("YOUR CHOICE---> ");
        try {
        int choice = in.nextInt(); //We find out the id of this specific image with this method.
        if (choice < 1 || choice > imageslist.size()) {
            System.out.println("Invalid choice.  Please choose on of the images below.");
            return chooseAnImage(); }// Showing available images again

            return imageslist.get(choice - 1).getImageId();

        } catch (InputMismatchException e) {
            System.out.println("Invalid input.  Please choose on of the images below.");
            in.nextLine(); // Consume the invalid input
            return chooseAnImage(); // Showing available images again
        }
    }
    /**
     * This method creates and starts an instance of the selected image
     * (creates a new container of the image).
    */
    public void implementImage() {
        try (CreateContainerCmd createContainerCmd =
                     ClientUpdater.getUpdatedClient().createContainerCmd(
                             getImageRep())) {
        createContainerCmd.exec(); //new container has been created
        List<Container> containers =  //We get the updated container list
                ClientUpdater.getUpdatedContainersFromClient();
        Container c = null;
        for (Container container : containers) {
            if (container.getStatus() != null && container.getStatus().startsWith("Created")) {
                c = container;
            }
        } //we create a new DockerInstance object with the created container
            // Check if a container was found
            if (c != null) {
                String[] names = c.getNames();
                // Check if names is not null before accessing its elements
                if (names != null && names.length > 0) {
                    // Create a new DockerInstance object with the created container
                    DockerInstance newContainer = new DockerInstance(
                            c.getNames()[0], c.getId(), c.getImage(), c.getStatus());
                    String containerIdStart = newContainer.getContainerId();
                    /*
                    ExecutorThread executorStart = new ExecutorThread(
                            containerIdStart, ExecutorThread.TaskType.START);
                    executorStart.start(); //we start the container
                    try {
                        executorStart.join(); // waiting for the thread to finish
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                     */
                    System.out.println("The new container has been created and is running"
                            + "\n");
                } else {
                    System.out.println("Error: Container names are null or empty.\n");
                }
            } else {
                System.out.println("Error: No created container found.\n");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    /**
     * This method removes an image from the DockerCluster
     * and from the image list
    */
    public void removeImage() {
        //first we need to remove all instances of this image
        List<Container> containers = ClientUpdater.getUpdatedContainersFromClient();
        for (Container c : containers) {
            if (c.getImage().equals(getImageRep()) //e.g. 'mongo'
                    || c.getImage().equals(getImageId().substring(0, 12)) //e.g. '76506809a39f'
                    || c.getImage().startsWith(getImageRep())) { //e.g. 'mongo:latest'
                //now we have to check if the container is running
                /*
                if (c.getStatus().startsWith("Up")) {
                    ExecutorThread executorStop = new ExecutorThread(
                            c.getId(), ExecutorThread.TaskType.STOP);
                    executorStop.start();
                    try {
                        executorStop.join(); // waiting for the thread to finish
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }


                //finally we remove the container
                ExecutorThread executorRemove = new ExecutorThread(
                        c.getId(), ExecutorThread.TaskType.REMOVE);
                executorRemove.start();
                try {
                    executorRemove.join(); // waiting for the thread to finish
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                 */
            }
        }
        try (RemoveImageCmd removeImageCmd = ClientUpdater.getUpdatedClient().removeImageCmd(imageId)) {
            removeImageCmd.exec();
            System.out.println("Image Removed: " + imageId + "\n");
            imageslist.remove(this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * This method lists all the images that are in use.
     * This means all the images that run containers.
    */
    public static List<String> listUsedImages() {
        List<String> usedImages = new ArrayList<>();
        int num = 0; //Numbers to make the output more User Friendly
        for (DockerImage img : imageslist) {
            List<Container> containers = ClientUpdater.getUpdatedContainersFromClient();
            boolean f = false;
            for (Container c : containers) {
                if (c.getImage().equals(img.getImageRep()) //e.g. 'mongo'
                        || c.getImage().equals(img.getImageId().substring(0, 12)) //e.g. '76506809a39f'
                        || c.getImage().startsWith(img.getImageRep())) { //e.g. 'mongo:latest'
                    f = true;
                    break;
                }
            }
            if (f) {
                num++;
                usedImages.add(num + ") " + img);
            }
        }
        return usedImages;
    }
}
