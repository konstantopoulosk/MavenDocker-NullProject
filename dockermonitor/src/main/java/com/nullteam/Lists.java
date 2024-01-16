package com.nullteam;

import com.github.dockerjava.api.command.InspectVolumeResponse;
import com.github.dockerjava.api.model.Container;
import com.github.dockerjava.api.model.Image;
import com.github.dockerjava.api.model.Network;
import javafx.scene.control.ListView;

import java.util.ArrayList;
import java.util.List;

public class Lists {
    /**
     * A static list used by MainSceneController when user
     * clicks a container in a listview in order to
     * store the id of the last clicked container.
     */
    public static List<String> choiceContainers = new ArrayList<>();
    /**
     * A static list used by MainSceneController when user
     * clicks an image in a listview in order to
     * store the id of the last clicked image.
     */
    public static List<String> choiceImages = new ArrayList<>();
    /**
     * This method lists the updated images
     * from the user's client and creates a
     * DockerImage object for every image.
     */
    public static void listImage() {
        List<Image> images =
                ClientUpdater.getUpdatedImagesFromClient();
        for (Image i : images) {
            new DockerImage(i.getRepoDigests()[0],
                    "latest", i.getId());
        }
    }
    /**
     * This method lists the updated containers
     * from the user's client and creates a
     * DockerInstance object for every container.
     */
    public static void listContainers() {
        List<Container> containers =
                ClientUpdater.getUpdatedContainersFromClient();
        for (Container c : containers) {
            new DockerInstance(c.getNames()[0], c.getId(),
                    c.getImage(), c.getStatus());
        }
    }
    /**
     * This method lists the updated volumes
     * from the user's client and creates a
     * DockerVolume object for every volume.
     */
    public static void listVolumes() {
        List<InspectVolumeResponse> volumes =
                ClientUpdater.getUpdatedVolumesFromClient();
        for (InspectVolumeResponse v : volumes) {
            new DockerVolume(v.getDriver(), v.getName(),
                    DockerVolume.createdAt(v.getName()), v.getMountpoint());
        }
    }
    /**
     * This method lists the updated networks
     * from the user's client and creates a
     * DockerNetwork object for every network.
     */
    public static void listNetworks() {
        List<Network> networks =
                ClientUpdater.getUpdatedNetworksFromClient();
        for (Network n : networks) {
            new DockerNetwork(n.getId(), n.getName(),
                    n.getDriver(), n.getScope());
        }
    }
    /**
     * This Method sets the field containersList.
     */
    public static ListView<String> setListContainers(ListView<String> containersList) {
        int num = 0;
        for (DockerInstance dockerInstance : DockerInstance.containerslist) {
            num++;
            containersList.getItems().add(num + ") " + dockerInstance.toString());
        }
        if (num == 0) {
            containersList.getItems().add("You do not have any Containers.");
            containersList.getItems().add("---NULL---");
        }
        return containersList;
    }
    /**
     * This method sets the field exitedContainers.
     */
    public static ListView<String> setListExitedContainers(ListView<String> exitedContainers) {
        int num = 0;
        int i = 0;
        for (DockerInstance dockerInstance : DockerInstance.containerslist) {
            if (DockerInstance.containerslist.get(num).getContainerStatus().startsWith("Exited")
                    || DockerInstance.containerslist.get(num).getContainerStatus().startsWith("Created")) {
                i++;
                exitedContainers.getItems().add(i + ") " + dockerInstance.toString());
            }
            num++;
        }
        if (i == 0) {
            exitedContainers.getItems().add("All Containers are Running.");
            exitedContainers.getItems().add("---NULL---");
        }
        return exitedContainers;
    }
    /**
     * This Method sets the field activeContainers.
     */
    public static ListView<String> setListActiveContainers(ListView<String> activeContainers) {
        int i = 0, num = 0;
        for (DockerInstance dockerInstance : DockerInstance.containerslist) {
            if (DockerInstance.containerslist.get(num).getContainerStatus().startsWith("Up")) {
                i++;
                activeContainers.getItems().add(i + ") " + dockerInstance.toString());
            }
            num++;
        }
        if (i == 0) {
            activeContainers.getItems().add("All containers are Exited.");
            activeContainers.getItems().add("---NULL---");
        }
        return activeContainers;
    }
    /**
     * This Method sets the field pausedContainers.
     */
    public static ListView<String> setListPausedContainers(ListView<String> pausedContainers) {
        int num = 0, i = 0;
        for (DockerInstance dockerInstance : DockerInstance.containerslist) {
            if (DockerInstance.containerslist.get(num).getContainerStatus().endsWith("(Paused)")) {
                i++;
                pausedContainers.getItems().add(i + ") " + dockerInstance.toString());
            }
            num++;
        }
        if (i == 0) {
            pausedContainers.getItems().add("There are no Paused Containers.");
            pausedContainers.getItems().add("---NULL---");
        }
        return pausedContainers;
    }
    /**
     * This Method sets the field imagesList.
     */
    public static ListView<String> setListImages(ListView<String> imagesList) {
        int num = 0; //Numbers to make the output more User Friendly
        for (DockerImage img : DockerImage.imageslist) {
            num++;
            imagesList.getItems().add(num + ") " + img.toString());
        }
        if (num == 0) {
            imagesList.getItems().add("You do not have any Images.");
            imagesList.getItems().add("---NULL---");
        }
        return imagesList;
    }
    /**
     * This method sets the field imagesInUse.
     */
    public static ListView<String> setImagesInUse(ListView<String> imagesInUse) {
        int i = 0;
        List<String> usedImages = DockerImage.listUsedImages();
        for (String usedImage : usedImages) {
            i++;
            imagesInUse.getItems().add(usedImage);
        }
        if (i == 0) {
            imagesInUse.getItems().add("You do not use any Images.");
            imagesInUse.getItems().add("---NULL---");
        }
        return imagesInUse;
    }
    /**
     * This Method sets the field volumesList.
     */
    public static ListView<String> setListVolumes(ListView<String> volumesList) {
        int num = 0; //Numbers to make the output more User Friendly
        for (DockerVolume v : DockerVolume.volumeslist) {
            num++;
            volumesList.getItems().add(num + ") " + v.toString() + "\n");
        }
        if (num == 0) {
            volumesList.getItems().add("No Volumes for you.");
            volumesList.getItems().add("---NULL---");
        }
        return volumesList;
    }
    /**
     * This Method sets the field networksList.
     */
    public static ListView<String> setListNetworks(ListView<String> networksList) {
        int num = 0;
        for (DockerNetwork n : DockerNetwork.networkslist) {
            num++;
            networksList.getItems().add(num + ") " + n.toString());
        }
        if (num == 0) {
            networksList.getItems().add("No Networks for you.");
            networksList.getItems().add("---NULL---");
        }
        return networksList;
    }
    /**
     * This method lists the measurements that the user
     * chose to see in the List View.
     * @param list List&lt;String&gt;
     */
    public static ListView<String> setListMeasure(List<String> list, ListView<String> measure) {
        int i = 0;
        for (String s : list) {
            i++;
            measure.getItems().add("--> " + s);
        }
        if (i == 0) {
            measure.getItems().add("No Measurements that Day.");
            measure.getItems().add("---NULL---");
        }
        return measure;
    }
    /**
     * This Method sets the field logsList.
     */
    public static ListView<String> setListLogs(ListView<String> logsList, String id) {
        int i = 0;
        List<String> containerLogs = DockerInstance.showlogs(id);
        for (String log : containerLogs) {
            i++;
            logsList.getItems().add(log);
        }
        if (i == 0) {
            logsList.getItems().add("No Logs for this Container.");
            logsList.getItems().add("---NULL---");
        }
        return logsList;
    }
    /**
     * This Method sets the field subnetsList.
     */
    public static ListView<String> setListSubnets(ListView<String> subnetsList, String id) {
        StringBuilder d = DockerNetwork.formatSubnetsSettings(
                DockerNetwork.inspectContainersForSubnet(
                        id));
        subnetsList.getItems().add(d.toString());
        if (subnetsList.getItems().isEmpty()) {
            subnetsList.getItems().add("No Subnets for this Container.");
            subnetsList.getItems().add("---NULL---");
        }
        return subnetsList;
    }
}
