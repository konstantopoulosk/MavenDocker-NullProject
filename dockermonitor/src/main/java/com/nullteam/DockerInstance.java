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
    //aid methods

    //Container Menu: Case 1: Show All Containers
    /**
     * This method lists all the containers
     * from the Docker Desktop.
     */
    public static void listAllContainers() {
        System.out.println("Listing all the containers...\n.\n.\n.");
        int i = 0; //Numbers to make the output more user-friendly
        for (DockerInstance c : containerslist) {
            i++;
            c.setContainerStatus(ClientUpdater
                    .getUpdatedStatus(c.getContainerId()));
            System.out.println(i + ") " + c); //ennoeitai c.toSrting()
        }
    }
    //Container Menu: Case 2: Show Active Containers Only
    /**
     * This method lists only the active containers
     * from the Docker Desktop.
     */
    public static void listActiveContainers() {
        int i = 0; //Numbers to make the output more User Friendly
        for (DockerInstance c : containerslist) {
            c.setContainerStatus(ClientUpdater
                    .getUpdatedStatus(c.getContainerId()));
            if (c.getContainerStatus().startsWith("Up")) {
                //Starts with because the status can be Up a second ago
                i++;
                if (i==1) {
                    System.out.println("Listing active containers...\n.\n.\n.");
                }
                System.out.println(i + ") " + c); //ennoeitai c.toString()
            }
        }
        if (i==0) {
            System.out.println("There are no active containers");
        }
    }
    /**
     * This method is used when user wants to Stop,
     * Restart or Kill an Active Container.
     * It returns the id of the container that the user chose.
     * @return String
     */
    public static String chooseAnActiveContainer() {
        List<DockerInstance> actives = new ArrayList<>();
        int i = 1; //Output User Friendly
        for (DockerInstance c : containerslist) {
            if (c.getContainerStatus().startsWith("Up")) { //if the status starts with "Up" it means the container is active
                System.out.println(i + ") " + c);
                actives.add(c); //so we add it to the list
                i++;
            }
        }
        Scanner in = new Scanner(System.in);
        System.out.print("YOUR CHOICE---> ");
        try { //checking if the input is out of bounds
            int answer = in.nextInt();
            if (answer < 1 || answer > actives.size()) {
                System.out.println("Invalid choice. Please choose on of the containers below.");
                return chooseAnActiveContainer(); // Showing active containers again
            }
            return containerslist.get(containerslist.indexOf(actives.get(answer - 1))).getContainerId();

        } catch (InputMismatchException e) {
        System.out.println("Invalid input. Please choose on of the containers below.");
        in.nextLine(); // Consume the invalid input
        return chooseAnActiveContainer(); // Showing active containers again
    }
    }
    /**
     * This method is used when user wants to Start a Stopped Container.
     * It returns the id of the container that the user chose.
     * @return String
     */
    public static String chooseAStoppedContainer() { //returns container's id
        List<DockerInstance> stopped = new ArrayList<>();
        int i = 1; //Output more friendly and easier to the user to choose
        for (DockerInstance c :containerslist) {
            //if the status doesn't start with "Up" it means it's "Exited"
            if (!(c.getContainerStatus().startsWith("Up"))) {
                System.out.println(i + ") " + c);
                stopped.add(c); //so we add it to the list
                i++;
            }
        }
        Scanner in = new Scanner(System.in);
        System.out.print("YOUR CHOICE---> ");
        try { //checking if the input is out of bounds
        int answer = in.nextInt();
        if (answer < 1 || answer > stopped.size()) {
            System.out.println("Invalid choice. Please choose on of the containers below.");
            return chooseAStoppedContainer(); // Showing available containers again
        }
            return containerslist.get(containerslist.indexOf(stopped.get(answer-1))).getContainerId();
        } catch (InputMismatchException e) {
            System.out.println("Invalid input. Please choose on of the containers below.");
            in.nextLine(); // Consume the invalid input
            return chooseAStoppedContainer(); // Showing available containers again
        }
    }
    /*We need a different list of paused containers when case (7)
      in main is chosen (to UNPAUSE a container) because there is a chance
      that the user chooses an already unpaused container form the active ones*/
    /**
     * This method is used when user wants to Unpause a Paused Container.
     * It returns the id of the container that the user chose.
     * @return String
     */
    public static String chooseAPausedContainer() { //returns container's id
        List<DockerInstance> paused = new ArrayList<>();
        int i = 1; //Output User Friendly
        for (DockerInstance c : containerslist) {
            if (c.getContainerStatus().endsWith("(Paused)")) {
                System.out.println(i + ") " + c);
                paused.add(c);
                i++;
            }
        }
        Scanner in = new Scanner(System.in);
        System.out.print("YOUR CHOICE---> ");
        try {
            int answer = in.nextInt();
            if (answer < 1 || answer > paused.size()) {
                System.out.println("Invalid choice. Please choose on of the containers below.");
                return chooseAPausedContainer(); // Showing available containers again
            }
            return containerslist.get(containerslist.indexOf(paused.get(answer - 1))).getContainerId();

        } catch (InputMismatchException e) {
            System.out.println("Invalid input. Please choose on of the containers below.");
            in.nextLine(); // Consume the invalid input
            return chooseAPausedContainer(); // Showing available containers again
        }
    }
    /*We need a different list of unpaused containers when case (6)
      in main is chosen (to PAUSE a container) because there is a chance
      that the user chooses an already paused container form the active ones*/
    /**
     * This method is used when user wants to Pause
     * an active unpaused Container.
     * It returns the id of the container that the user chose.
     * @return String
     */
    public static String chooseAnUnpausedContainer() { //returns container's id
        List<DockerInstance> unpaused = new ArrayList<>();
        int i = 1; //Output User Friendly
        for (DockerInstance c : containerslist) {
            if (c.getContainerStatus().startsWith("Up") && !(c.getContainerStatus().endsWith("(Paused)"))) {
                System.out.println(i + ") " + c);
                unpaused.add(c);
                i++;
            }
        }
        Scanner in = new Scanner(System.in);
        System.out.print("YOUR CHOICE---> ");
        try {
            int answer = in.nextInt();
            if (answer < 1 || answer > unpaused.size()) {
                System.out.println("Invalid choice. Please choose on of the containers below.");
                return chooseAnUnpausedContainer(); // Showing available containers again
            }
            return containerslist.get(containerslist.indexOf(unpaused.get(answer - 1))).getContainerId();
        } catch (InputMismatchException e) {
            System.out.println("Invalid input. Please choose on of the containers below.");
            in.nextLine(); // Consume the invalid input
            return chooseAnUnpausedContainer(); // Showing available containers again
        }
    }
    /**
     * This method lets the user choose a container from the
     * entire list no matter the status.
     * For example when the user wants to rename or remove a container.
     * It returns the id of the container that the user chose.
     * @return String
     */
    public static String chooseAContainer() {
        DockerInstance.listAllContainers();
        Scanner in = new Scanner(System.in);
        System.out.print("YOUR CHOICE---> ");
        try { //checking if the input is out of bounds
            int choice = in.nextInt();
            if (choice < 1 || choice > containerslist.size()) {
                System.out.println("Invalid choice.  Please choose on of the containers below.");
                return chooseAContainer(); // Showing available containers again
            }
            return containerslist.get(choice - 1).getContainerId();
        } catch (InputMismatchException e) {
            System.out.println("Invalid input.  Please choose on of the containers below.");
            in.nextLine(); // Consume the invalid input
            return chooseAContainer(); // Showing available containers again
        }
    }
    /**
     * This method is used to check in main if there are any active containers.
     * It returns TRUE if there are no active containers in the list
     * and FALSE otherwise.
     * @return boolean
     */
    public static boolean noActiveContainers() {
        boolean flag = true;
        for (DockerInstance c :containerslist) {
            if (c.getContainerStatus().startsWith("Up")) {
                //It means there is at least one active container
                flag = false;
                break;
            }
        }
        return flag;
    }
    // User presses UNPAUSE and there are no Paused Containers,
    // he can't choose from nothing --> Exception
    /**
     * This method is used to check in main if there are any paused containers.
     * It returns TRUE if there are no paused containers in the list
     * and FALSE otherwise.
     * @return boolean
     */
    public static boolean noPausedContainers() {
        boolean flag = true;
        for (DockerInstance c :containerslist) {
            if (c.getContainerStatus().endsWith("(Paused)")) {
                //It means there is at least one paused container
                flag = false;
                break;
            }
        }
        return flag;
    }
    // User presses PAUSE and there are no active unpaused Containers,
    // he can't choose from nothing --> Exception
    /**
     * This method is used to check in main
     * if there are any unpaused containers.
     * It returns TRUE if there are no unpaused containers in the list
     * and FALSE otherwise.
     * @return boolean
     */
    public static boolean noUnpausedContainers() {
        boolean flag = true;
        for (DockerInstance c :containerslist) {
            if (c.getContainerStatus().startsWith("Up")
                    && (!c.getContainerStatus().endsWith("(Paused)"))){
                flag = false;
                break;
            }
        }
        return flag;
    }

    public static void showContainerLogs(String containerId) {
        try (DockerClient client = DockerClientBuilder.getInstance().build()) {
            LogContainerCmd logContainerCmd = client.logContainerCmd(containerId)
                    .withStdOut(true)
                    .withStdErr(true)
                    .withFollowStream(true)
                    .withTailAll();

            client.logContainerCmd(containerId).withStdOut(true).withStdErr(true).exec(new LogCallback());
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error displaying logs for container: " + containerId);
        }
    }

    private static class LogCallback implements com.github.dockerjava.api.async.ResultCallback<Frame> {
        @Override
        public void onStart(Closeable closeable) {
            // Do nothing on start
        }

        @Override
        public void onNext(Frame frame) {
            // Print log frames to the console
            System.out.print(frame.toString());
        }

        @Override
        public void onError(Throwable throwable) {
            // Handle error
            throwable.printStackTrace();
        }

        @Override
        public void onComplete() {
            // Do nothing on completion
        }

        @Override
        public void close() {
            // Close resources
        }
    }
    /* !! TO AVOID REPETITIONS OF CODE
    public static String chooseAStoppedContainer() {
        return chooseBasedOnCondition("!(c.getContainerStatus()
                .startsWith("+"\"\\"+"Up"+"\"\\"+"))");
    }
    public static String chooseBasedOnCondition(String condition) {
        List<DockerInstance> mylist = new ArrayList<>();
        int i=1;
        for (DockerInstance c :containerslist) {
            if (Boolean.parseBoolean(condition)) {
                System.out.println(i + ") " + "Name: "
                        + c.getContainerName() + "  ID: " + c.getContainerId()
                        + "  Image: " + c.getContainerImage()
                        + "  STATUS: " + c.getContainerStatus());
                mylist.add(c);
                i++;
            }
        }
        Scanner in = new Scanner(System.in);
        System.out.print("YOUR CHOICE---> ");
        int answer = in.nextInt();
        return containerslist.get(containerslist
                .indexOf(mylist.get(answer-1))).getContainerId();
    }
    */
}
