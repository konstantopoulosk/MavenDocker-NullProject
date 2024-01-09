package com.nullteam;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.command.*;


import com.github.dockerjava.api.exception.NotModifiedException;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.InputMismatchException;

import com.github.dockerjava.api.model.Frame;
import com.github.dockerjava.core.DockerClientBuilder;
import com.github.dockerjava.core.command.LogContainerResultCallback;
import org.glassfish.jersey.internal.util.collection.StringIgnoreCaseKeyComparator;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class DockerInstance {
    /**
     * List of all the DockerInstance objects,
     * all the containers in the DockerDesktop.
     */
    public static List<DockerInstance> containerslist = new ArrayList<>();
    /* Fields 'containerId' and 'image' are final
       because they never change in the program.
       On the contrary, fields 'status' and 'name'
       can change if the user chooses to
     */
    /**
     * A field for the container ID.
     */
    private final String containerId;
    /**
     * Every container is an instance of an image.
     * The name of this image is described by this field.
     */
    private final String image;
    /**
     * A field for the status of the container.
     * 'Up' if it's running and 'Exited' otherwise.
     */
    private String status;
    /**
     * A field for the container's name.
     */
    private String name;
    /**
     * Constructor for Class DockerInstance.
     * It creates a new DockerInstance object
     * and adds it to the containerslist.
     * @param name String
     * @param containerId String
     * @param image String
     * @param status String
     */
    public DockerInstance(String name, final String containerId,
                          final String image, String status) {
        this.name = name;
        this.containerId = containerId;
        this.image = image;
        this.status = status;
        containerslist.add(this);
    }
    //getters
    /**
     * Returns the container's ID.
     * @return String
     */
    public String getContainerId() {
        return containerId;
    }

    /**
     * Returns the container's name.
     * @return String
     */
    public String getContainerName() {
        return name;
    }
    /**
     * Returns the image that runs the container.
     * @return String
     */
    public String getContainerImage() {
        return image;
    }
    /**
     * Returns the container's status.
     * (Up or Exited)
     * @return String
     */
    public String getContainerStatus() {
        return status;
    }
    /**
     * This method is used to change the status of
     * the container when a change happens.
     * (START, STOP, PAUSE, UNPAUSE...)
     * @param status String
     */
    public void setContainerStatus(String status) {
        this.status = status;
    }
    /**
     * A classic toString method.
     * We use it to show some of every container's information
     * (name, id, image, status)
     * @return String
     */
    @Override
    public String toString() {
        return "Name: " + name + "  ID: " + containerId
                + "  Image: " + image + "  STATUS: " + status;
    } //We choose to show only the Name, ID, Image and Status of a Container

    //Container Menu: case 3 (Container tools)
    /**
     * This method stops an active container.
     */
    public void stopContainer() {
        try {
            final int m = 10;
            try (StopContainerCmd stopContainerCmd = ClientUpdater
                    .getUpdatedClient().stopContainerCmd(containerId)
                    .withTimeout(m)) {
                stopContainerCmd.exec(); //Changed the status from Up to Exited
                System.out.println("Container stopped: " + containerId
                        + "\n\n"); //Friendly Message to User
                this.setContainerStatus("Exited"); //Change the status of the object
            } catch (NotModifiedException e) {
                // Container was already stopped or in the process of stopping
                System.out.println("Container is already stopped "
                        + "or in the process of stopping: " + containerId + "\n\n");
                this.setContainerStatus(ClientUpdater
                        .getUpdatedStatus(containerId));
            } catch (Exception e) {
                // Handle other exceptions
                e.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    /**
     * This method starts a container.
     */
    public void startContainer() {
        try (StartContainerCmd startContainerCmd = ClientUpdater
                .getUpdatedClient().startContainerCmd(containerId)) {
        startContainerCmd.exec(); //Starts Container in the Cluster
            System.out.println("Container started: " + containerId + "\n\n");
            this.setContainerStatus(ClientUpdater //changes the status of the object
                    .getUpdatedStatus(containerId));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    /**
     * With this method the user can rename a container.
     * @param newName String
     */
    public void renameContainer(String newName) {
        try (RenameContainerCmd renameContainerCmd
                = ClientUpdater.getUpdatedClient().renameContainerCmd(
                        containerId).withName(newName)) {
            renameContainerCmd.exec(); //Renames in the Cluster
            System.out.println("Container with id: " + this.getContainerId()
                    + " has been renamed to: " + newName + "\n\n");
            this.name = newName; //Renames the object
        }catch (Exception e) {
            e.printStackTrace();
        }
    }
    /**
     * This method removes a container from the Cluster
     * and the containerslist.
     */
    public void removeContainer() {
        try (RemoveContainerCmd removeContainerCmd = ClientUpdater
                .getUpdatedClient().removeContainerCmd(containerId)) {
            removeContainerCmd.exec(); //removes container from the cluster
            System.out.println("Container removed: " + containerId + "\n\n");
            containerslist.remove(this); //removes container from our containerslist
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    /**
     * This method restarts a container.
     */
    public void restartContainer() {
        try (RestartContainerCmd restartContainerCmd = ClientUpdater
                .getUpdatedClient().restartContainerCmd(containerId)) {
            restartContainerCmd.exec(); //restarts container in the cluster
            System.out.println("Container restarted: " + containerId + "\n\n");
            this.setContainerStatus(ClientUpdater //changes the status of the object
                    .getUpdatedStatus(containerId));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    /**
     * This method pauses an active container.
     */
    public void pauseContainer() {
        // Check if the container is already paused
        if (this.getContainerStatus().equalsIgnoreCase("paused")) {
            System.out.println("Container is already paused: " + containerId + "\n\n");
            return;
        }

        // If not paused, pause the container
        try (PauseContainerCmd pauseContainerCmd = ClientUpdater
                .getUpdatedClient().pauseContainerCmd(containerId)) {
            pauseContainerCmd.exec(); //Pauses the container in Docker Cluster
            System.out.println("Container paused: " + containerId + "\n\n");
            this.setContainerStatus(ClientUpdater //changes the status of the object
                    .getUpdatedStatus(containerId));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    /**
     * This method unpauses a paused container.
     */
    public void unpauseContainer() {
        // Check if the container is already unpaused
        if (this.getContainerStatus().equalsIgnoreCase("running")) {
            System.out.println("Container is already unpaused: " + containerId + "\n\n");
            return;
        }

        // If not unpaused, unpause the container
        try (UnpauseContainerCmd unpauseContainerCmd = ClientUpdater
                .getUpdatedClient().unpauseContainerCmd(containerId)) {
            unpauseContainerCmd.exec(); //Unpauses container in cluster
            System.out.println("Container unpaused: " + containerId + "\n\n");
            this.setContainerStatus(ClientUpdater //changes the status of the object
                    .getUpdatedStatus(containerId));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    /**
     * This method kills a container.
     */
    public void killContainer() {
        try (KillContainerCmd killContainerCmd = ClientUpdater
                .getUpdatedClient().killContainerCmd(containerId)) {
            killContainerCmd.exec(); //Kills container in cluster
            System.out.println("Container killed: " + containerId + "\n\n");
            this.setContainerStatus(ClientUpdater
                    .getUpdatedStatus(containerId)); //Changes the status
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * This method lists all the logs
     * of a running container.
     *
     * @param containerId String
     * @return List&lt;String&gt;
     */
    public static List<String> showlogs(String containerId) {
        List<String> logs = new ArrayList<>();
        try {
            DockerClient client = ClientUpdater.getUpdatedClient();
            client.logContainerCmd(containerId)
                    .withStdOut(true)
                    .withStdErr(true)
                    .withTailAll()
                    .exec(new LogContainerResultCallback() {
                        @Override
                        public void onNext(Frame frame) {
                            logs.add(new String(frame.getPayload()));
                        }
                    }).awaitCompletion();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return logs;
    }
}
