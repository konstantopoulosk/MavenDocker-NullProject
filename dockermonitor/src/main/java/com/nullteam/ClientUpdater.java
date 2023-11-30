package com.nullteam;
import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.model.Container;
import com.github.dockerjava.core.DefaultDockerClientConfig;
import com.github.dockerjava.core.DockerClientBuilder;

import java.io.IOException;
import java.util.List;

public class ClientUpdater {
    public static List<Container> getUpdatedContainersFromClient() {
        DefaultDockerClientConfig.Builder builder= DefaultDockerClientConfig.createDefaultConfigBuilder();
        builder.withDockerHost("tcp://localhost:2375");
        DockerClient dockerClient = DockerClientBuilder.getInstance(builder).build();
        dockerClient.versionCmd().exec();
        List<Container> containers; // instances
        containers = dockerClient.listContainersCmd().withShowAll(true).exec();
        try {
            dockerClient.close();
        } catch(IOException e) {
            System.out.println("Failed to close the client");
        }
        return containers;
    }
    public static DockerClient getUpdatedClient() {
        DefaultDockerClientConfig.Builder builder= DefaultDockerClientConfig.createDefaultConfigBuilder();
        builder.withDockerHost("tcp://localhost:2375");
        DockerClient dockerClient = DockerClientBuilder.getInstance(builder).build();
        dockerClient.versionCmd().exec();
        return dockerClient;
    }
}
