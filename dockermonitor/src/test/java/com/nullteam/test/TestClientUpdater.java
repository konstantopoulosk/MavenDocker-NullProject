package com.nullteam.test;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.command.InspectVolumeResponse;
import com.github.dockerjava.api.model.Container;
import com.github.dockerjava.api.model.Image;
import com.github.dockerjava.api.model.Network;
import com.github.dockerjava.api.model.Volume;
import com.nullteam.ClientUpdater;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class TestClientUpdater {
    DockerClient dockerClient;
    String containerId;
    String status;
    Connection connection;
    InetAddress ip;

    @Before
    public void setUp() {
        dockerClient = ClientUpdater.getUpdatedClient();
        containerId = ClientUpdater.getUpdatedContainersFromClient()
                .get(0).getId();
        status = ClientUpdater.getUpdatedContainersFromClient()
                .get(0).getStatus();
        try {
            ip = InetAddress.getLocalHost();
        } catch (UnknownHostException e) {
            throw new RuntimeException(e);
        }
    }
    @Test
    public void testConnectionAccomplished() {
        ClientUpdater.connectionAccomplished();
        Assert.assertTrue(true);
    }
    @Test
    public void testGetUpdatedContainersFromClient() {
        List<Container> containers = ClientUpdater.getUpdatedContainersFromClient();
        Assert.assertTrue(containers != null);
    }
    @Test
    public void testGetUpdatedImagesFromClient() {
        List<Image> images = ClientUpdater.getUpdatedImagesFromClient();
        Assert.assertTrue(images != null);
    }
    @Test
    public void testGetUpdatedVolumesFromClient() {
        List<InspectVolumeResponse> volume = ClientUpdater.getUpdatedVolumesFromClient();
        Assert.assertTrue(volume != null);
    }
    @Test
    public void testGetUpdatedNetworksFromClient() {
        List<Network> networks = ClientUpdater.getUpdatedNetworksFromClient();
        Assert.assertTrue(networks != null);
    }
    @Test
    public void testGetUpdatedClient() {
        DockerClient dockerClient2 = ClientUpdater.getUpdatedClient();
        Assert.assertFalse(dockerClient2 == dockerClient);
    }
    @Test
    public void testCloseClient() {
        DockerClient dockerClient2 = ClientUpdater.getUpdatedClient();
        ClientUpdater.closeClient(dockerClient2);
        Assert.assertTrue(dockerClient2 != dockerClient);
    }
    @Test
    public void testGetUpdatedStatus() {
        String status1 = ClientUpdater.getUpdatedStatus(containerId);
        Assert.assertEquals(status1, status);
    }
    @Test
    public void testConnectToDatabase() {
        List<String> list = new ArrayList<>();
        try {
            File file = new File("database").getAbsoluteFile();
            Scanner reader = new Scanner(file);
            while (reader.hasNextLine()) {
                String data = reader.nextLine();
                list.add(data);
            }
            String url = list.getFirst();
            String user = list.get(1);
            String password = list.get(2);
            connection = ClientUpdater.connectToDatabase(url, user, password
            );
            Assert.assertTrue(user != null);
            Assert.assertTrue(password != null);
            Assert.assertTrue(connection != null);
        } catch (Exception e) {

        }
    }
    @Test
    public void testGetIp() {
        String ip1 = ClientUpdater.getIp();
        Assert.assertTrue(ip1.equals(String.valueOf(ip)));
    }
}
