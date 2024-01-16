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
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import nullteam.gui.CannotOpenNewWindow;
import nullteam.gui.MainSceneController;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

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
    /**
     * A classic toString method.
     * We use it to show every image's information
     * (repository, tag, id)
     * @return String
    */
    @Override
    public String toString() {
        return "Repository: " + this.getImageRep() +  "   Tag: "
                + this.getImageTag() + "   ImageID: " + this.getImageId();
    }
    //helpful methods
    /**
     * This method creates and starts an instance of the selected image
     * (creates a new container of the image).
    */
    public String implementImage() {
        String id = null;
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
                    id = containerIdStart;
                } else {
                    System.out.println("Error: Container names are null or empty.\n");
                }
            } else {
                System.out.println("Error: No created container found.\n");
            }
            return id;
        } catch (Exception e) {
            System.out.println("Caught Error: " + e.getMessage());
            return null;
        }
    }
    /**
     * This method returns a list of all the instances
     * of an image.
     * @return List&lt;Container&gt;
     */
    public List<Container> findContainers() {
        List<Container> containerList = new ArrayList<>();
        List<Container> containers = ClientUpdater.getUpdatedContainersFromClient();
        for (Container c : containers) {
            if (c.getImage().equals(getImageRep()) //e.g. 'mongo'
                    || c.getImage().equals(getImageId().substring(0, 12)) //e.g. '76506809a39f'
                    || c.getImage().startsWith(getImageRep())) {
                containerList.add(c);
            }
        }
        return containerList;
    }
    /**
     * This method removes an image from the DockerCluster
     * and from the image list
    */
    public void removeImage() {
        try (RemoveImageCmd removeImageCmd = ClientUpdater.getUpdatedClient().removeImageCmd(imageId)) {
            removeImageCmd.exec();
            System.out.println("Image Removed: " + imageId + "\n");
            imageslist.remove(this);
        } catch (Exception e) {
            System.out.println("Caught Error: " + e.getMessage());
        }
    }

    /**
     * This method lists all the images that are in use.
     * This means all the images that run containers.
     *
     * @return List&lt;String&gt;
    */
    public static List<String> listUsedImages() {
        List<String> usedImages = new ArrayList<>();
        int num = 0; //Numbers to make the output more User Friendly
        List<Container> containerList;
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
    /**
     * This method pulls an image from DockerHub
     * and creates a DockerImage object.
     * @param imageToPull String
     */
    @FXML
    public static void pullImage(String imageToPull) {
        try{
            PullImageCmd pullImageCmd = ClientUpdater.getUpdatedClient()
                    .pullImageCmd(imageToPull).withTag("latest");
            System.out.println("Pulling image...");
            System.out.println("This may take a while...");
            pullImageCmd.exec(new PullImageResultCallback() {
                @Override
                public void onNext(PullResponseItem item) {
                    super.onNext(item);
                }
            }).awaitStarted();
            Thread.sleep(60000);
            //it takes a while to load the new image in  the docker cluster
            //Now we have to create a new DockerImage object
            List<Image> images =
                    ClientUpdater.getUpdatedImagesFromClient();
            for (Image i : images) {
                String[] parts = i.getRepoDigests()[0].split("@");
                if (parts[0].equals(imageToPull)) {
                    new DockerImage(i.getRepoDigests()[0],
                            "latest", i.getId());
                }
            }
            System.out.println("Image pulled successfully");
        } catch (Exception e) {
            try {
                Parent root1 = FXMLLoader.load(Objects.requireNonNull(MainSceneController
                        .class.getClassLoader().getResource("imagePullErrorMessage.fxml")));
                Stage stage = new Stage();
                stage.setTitle("Error while pulling image...");
                stage.setScene(new Scene(root1, 300, 150));
                stage.show();
            } catch (CannotOpenNewWindow cannotOpenNewWindow) {
                System.out.println("Caught Error: " + cannotOpenNewWindow.getMessage());
            } catch (Exception e1) {
                System.out.println("Exception Happened ...");
            }
        }
    }
}
