package com.nullteam;

import com.github.dockerjava.api.command.InspectVolumeResponse;
import com.github.dockerjava.api.model.Container;
import com.github.dockerjava.api.model.Image;
import com.github.dockerjava.api.model.Network;

import java.util.ArrayList;
import java.util.List;

public class Lists {
    public static List<String> choiceContainers = new ArrayList<>();
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
}
