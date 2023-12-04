package com.nullteam;
import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.model.Container;
import com.github.dockerjava.api.model.Image;
import com.github.dockerjava.core.DefaultDockerClientConfig;
import com.github.dockerjava.core.DockerClientBuilder;
import java.io.IOException;
import java.util.List;

public class ClientUpdater {
    public static List<Container> getUpdatedContainersFromClient() {
        DockerClient dockerClient = getUpdatedClient(); //Method Below
        List<Container> containers; // instances
        containers = dockerClient.listContainersCmd().withShowAll(true).exec(); //all containers
        //from cluster
        try {
            dockerClient.close();
        } catch(IOException e) {
            System.out.println("Failed to close the client");
        }
        return containers; //Updated Containers
    }
    public static List<Image> getUpdatedImagesFromClient() {
        DockerClient client = getUpdatedClient(); //Method Below
        List<Image> images = client.listImagesCmd().exec(); //Images from the cluster
        try {
            client.close();
        } catch (IOException e) {
            System.out.println("Failed to close the client");
        }
        return images; //Updated Images
    }

    //This method gets the Updated Docker Client. It is used after a change for example Start / Stop / Remove
    public static DockerClient getUpdatedClient() {
        DefaultDockerClientConfig.Builder builder= DefaultDockerClientConfig.createDefaultConfigBuilder();
        builder.withDockerHost("tcp://localhost:2375");
        DockerClient dockerClient = DockerClientBuilder.getInstance(builder).build();
        dockerClient.versionCmd().exec();
        return dockerClient; //The same docker client but UPDATED!
    }
    //this method updates the status every time to monitor the time the container has been Up or Exited
    public static String getUpdatedStatus(String containerId) { //Updates the status of a specific container
        String status = null;
        List<Container> containers = getUpdatedContainersFromClient(); //List with the updated containers
        for (Container c : containers) {
            if (c.getId().equals(containerId)) { //Finds the desired container
                status = c.getStatus(); //changes the status
            }
        }
        return status;
    }
}
