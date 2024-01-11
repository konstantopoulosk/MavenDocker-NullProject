package com.nullteam;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.command.InspectVolumeResponse;
import com.github.dockerjava.api.command.ListVolumesResponse;
import com.github.dockerjava.api.model.Container;
import com.github.dockerjava.api.model.Image;
import com.github.dockerjava.api.model.Network;
import com.github.dockerjava.core.DefaultDockerClientConfig;
import com.github.dockerjava.core.DockerClientBuilder;

import javax.ws.rs.ProcessingException;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.List;

public final class ClientUpdater {

    /**
     * Checks if the connection with the Docker Client was accomplished.
     * If not, an exception is thrown so that the program can handle it and
     * open the Docker Desktop App
     */
    public static void connectionAccomplished() {
        boolean connected = true;
        try {
            getUpdatedClient();
        } catch (ProcessingException e) {
            System.out.println("ERROR!: Couldn't connect to the client...");
            System.out.println("\n.\n.\n.WAITING FOR DOCKER DESKTOP.EXE");
            File file = new File("C:\\Program Files\\Docker\\Docker\\Docker Desktop.exe");
            try {
                Desktop.getDesktop().open(file); // opening the docker.exe
                Process process = Runtime.getRuntime().exec("cmd /c start \"\" \""
                        + "C:\\Program Files\\Docker\\Docker\\Docker Desktop.exe");
                int exitCode = process.waitFor();
                Thread.sleep(30000);
                if (exitCode == 0) {
                    System.out.println("File or application opened successfully.");
                } else {
                    System.out.println("Failed to open file or application.");
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    /**
     * This method gets a list of updated containers from
     * the updated Docker Client in order to always have
     * the correct list after any change made by the user.
     * @return List&lt;Container&gt;
     */
    public static List<Container> getUpdatedContainersFromClient() {
        DockerClient dockerClient = getUpdatedClient(); //Method Below
        List<Container> containers; // instances
        containers = dockerClient.listContainersCmd()
                .withShowAll(true).exec(); //all containers from cluster
        closeClient(dockerClient);
        return containers; //Updated Containers
    }
    /**
     * This method gets a list of updated images from
     * the updated Docker Client in order to always have
     * the correct list after any change made by the user.
     * @return List&lt;Image&gt;
     */
    public static List<Image> getUpdatedImagesFromClient() {
        DockerClient client = getUpdatedClient(); //Method Below
        List<Image> images
                = client.listImagesCmd().exec(); //Images from the cluster
        closeClient(client);
        return images; //Updated Images
    }
    /**
     * This method gets a list of updated volumes from
     * the updated Docker Client in order to always have
     * the correct list after any change.
     *
     * @return List&lt;InspectVolumeResponse&gt;
     */
    public static List<InspectVolumeResponse> getUpdatedVolumesFromClient() {
        DockerClient client = getUpdatedClient(); //Method Below
        ListVolumesResponse volumesResponse = client.listVolumesCmd().exec();
        List<InspectVolumeResponse> volumes = volumesResponse.getVolumes();
        closeClient(client);
        return volumes; //Updated Volumes
    }
    /**
     * This method gets a list of updated networks from
     * the updated Docker Client in order to always have
     * the correct list after any change.
     * (Truth is our app doesn't provide the possibility
     * of creating a new network so the list will
     * always be the same unless the user changes something
     * in his Docker Cluster outside our app)
     *
     * @return List&lt;Network&gt;
     */
    public static List<Network> getUpdatedNetworksFromClient() {
        DockerClient client = getUpdatedClient(); //Method Below
        List<Network> networks = client.listNetworksCmd().exec();
        closeClient(client);
        return networks; //Updated Networks
    }
    /**
     * This method gets the Updated Docker Client.
     * It is used after a change, for example Start / Stop / Remove
     *
     * @return DockerClient
     */
    public static DockerClient getUpdatedClient() {
        DefaultDockerClientConfig.Builder builder
                = DefaultDockerClientConfig.createDefaultConfigBuilder();
        builder.withDockerHost("tcp://localhost:2375");
        DockerClient dockerClient = DockerClientBuilder
                .getInstance(builder).build();
        dockerClient.versionCmd().exec();
        return dockerClient; //The same docker client but UPDATED!
    }

    /**
     * This method makes sure that the client closes
     * after it's used.
     * If the process fails, there is a message.
     * @param dockerClient
     */
    public static void closeClient(DockerClient dockerClient) {
        try {
            dockerClient.close();
        } catch (IOException e) {
            System.out.println("Failed to close the client");
        }
    }
    /**
     * This method updates the status every time, in order
     * to monitor the time the container has been Up or Exited.
     *
     * @param containerId String
     * @return String
     */
    public static String getUpdatedStatus(final String containerId) {
        String status = null;
        List<Container> containers
                = getUpdatedContainersFromClient(); //updated containers
        for (Container c : containers) {
            if (c.getId().equals(containerId)) { //finds the desired container
                status = c.getStatus(); //changes the status
            }
        }
        return status;
    }
    /**
     * This method makes a connection
     * to a database using a given url,
     * username and password.
     * @return Connection
     */
    public static Connection connectToDatabase(String driver, String url, String user, String password) {
        Connection connection = null;
        try {
            Class.forName(driver);
            connection = DriverManager.getConnection(url, user, password);
            System.out.println(connection);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return connection;
    }
    /**
     * This method gets the System Ip
     * from User to identify him.
     * @return String
     */
    public static String getIp() {
        InetAddress localhost = null;
        try {
            localhost = InetAddress.getLocalHost();
        } catch (UnknownHostException e) {
                throw new RuntimeException(e);
        }
        return String.valueOf(localhost);
    }
}
