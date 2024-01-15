package com.nullteam.test;

import com.github.dockerjava.api.command.InspectVolumeResponse;
import com.github.dockerjava.api.model.Container;
import com.github.dockerjava.api.model.Image;
import com.github.dockerjava.api.model.Network;
import com.nullteam.*;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

public class TestLists {
    private List<Container> containers;
    private List<Image> images;
    private List<Network> networks;
    private List<InspectVolumeResponse> volumes;
    private DockerImage image;
    private DockerVolume volume;
    private DockerNetwork network;
    private DockerInstance container;
    @Before
    public void setUp() {
        containers = ClientUpdater.getUpdatedContainersFromClient();
        images = ClientUpdater.getUpdatedImagesFromClient();
        volumes = ClientUpdater.getUpdatedVolumesFromClient();
        networks = ClientUpdater.getUpdatedNetworksFromClient();
    }
    @Test
    public void testListImage() {
        Lists.listImage();
        image = DockerImage.imageslist.getFirst();
        Assert.assertNotNull(image);
    }
    @Test
    public void testListContainers() {
        Lists.listContainers();
        container = DockerInstance.containerslist.getFirst();
        Assert.assertNotNull(container);
    }
    @Test
    public void testListVolumes() {
        Lists.listVolumes();
        volume = DockerVolume.volumeslist.getFirst();
        Assert.assertNotNull(volume);
    }
    @Test
    public void testListNetworks() {
        Lists.listNetworks();
        network = DockerNetwork.networkslist.getFirst();
        Assert.assertNotNull(network);
    }
    @After
    public void tearDown() {

    }
}
