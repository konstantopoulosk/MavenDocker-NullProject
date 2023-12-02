package com.nullteam;
import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.model.Container;
import com.github.dockerjava.core.DefaultDockerClientConfig;
import com.github.dockerjava.core.DockerClientBuilder;

import java.io.IOException;
import java.util.List;

public class ClientUpdater {
    public static List<Container> getUpdatedContainersFromClient() {
        DockerClient client = getUpdatedClient();
        List<Container> containers; // instances
        containers = dockerClient.listContainersCmd().withShowAll(true).exec();
        try {
            dockerClient.close();
        } catch(IOException e) {
            System.out.println("Failed to close the client");
        }
        return containers;
    }
    public static List<Image> getUpdatedImagesFromClient() {
        DockerClient client = getUpdatedClient();
        List<Image> images = client.listImagesCmd().exec();
        try {
            client.close();
        } catch (IOException e) {
            System.out.println("Failed to close the client");
        }
        return images;
    }
    public static DockerClient getUpdatedClient() {
        DefaultDockerClientConfig.Builder builder= DefaultDockerClientConfig.createDefaultConfigBuilder();
        builder.withDockerHost("tcp://localhost:2375");
        DockerClient dockerClient = DockerClientBuilder.getInstance(builder).build();
        dockerClient.versionCmd().exec();
        return dockerClient;
    }
    //this method updates the status every time to monitor the time the container has been Up or Exited
    public static String getUpdatedStatus(String containerId) {
        String status = null;
        List<Container> containers = getUpdatedContainersFromClient();
        for (Container c : containers) {
            if (c.getId().equals(containerId)) {
                status = c.getStatus();
            }
        }
        return status;
    }
}
