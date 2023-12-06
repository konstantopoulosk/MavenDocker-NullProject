package com.nullteam;

import com.github.dockerjava.api.command.*;
import com.github.dockerjava.api.exception.NotModifiedException;
import java.util.InputMismatchException;
import org.glassfish.jersey.internal.util.collection.StringIgnoreCaseKeyComparator;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;



public class DockerInstance {
    public static List<DockerInstance> containerslist = new ArrayList<>();
    //fields
    private final String containerId;
    // enas container afora mia mono eikona, h idia eikona mporei na xrisimopoietai se pollous diaforetikous containers

    private final String image; //An Image runs inside the container
    private String status; //Up or Exited.
    private String name; //Name of the container.
    //constructor
    public DockerInstance(String name, String containerId, String image, String status) {
        this.name = name;
        this.containerId = containerId;
        this.image = image;
        this.status = status;
        containerslist.add(this);
    }
    //getters
    public String getContainerId() { //Returns Container's ID
        return containerId;
    }
    public String getContainerName() { //Returns Container's Name
        return name;
    }
    public String getContainerImage() { //Returns Container's Image
        return image;
    }
    public String getContainerStatus() { //Returns Container's Status
        return status;
    }
    public void setContainerStatus(String status) { //Sets the status of the specific container: Up or Exited
        this.status = status;
    }
    @Override
    public String toString() { //Classic toString for an Object type DockerInstance
        return "Name: " + name + "  ID: " + containerId + "  Image: " + image + "  STATUS: " + status;
    } //We choose to show only the Name, ID, Image and Status of a Container

    //Container Menu: case 3 (Container tools)
    public void stopContainer() {
        try {
            StopContainerCmd stopContainerCmd = ClientUpdater.getUpdatedClient().stopContainerCmd(containerId)
                    .withTimeout(10);
            stopContainerCmd.exec(); //Changed the status from Up to Exited in the Docker Cluster
            System.out.println("Container stopped: " + containerId + "\n\n"); //Friendly Message to User
            this.setContainerStatus("Exited"); //Change the status of the object
        } catch (NotModifiedException e) {
            // Container was already stopped or in the process of stopping
            System.out.println("Container is already stopped or in the process of stopping: " + containerId + "\n\n");
            this.setContainerStatus(ClientUpdater.getUpdatedStatus(containerId));
        } catch (Exception e) {
            // Handle other exceptions
            e.printStackTrace();
        }
    }
    public void startContainer() {
        StartContainerCmd startContainerCmd = ClientUpdater.getUpdatedClient().startContainerCmd(containerId);
        startContainerCmd.exec(); //Starts Container in the Cluster
        System.out.println("Container started: " + containerId + "\n\n");
        this.setContainerStatus(ClientUpdater.getUpdatedStatus(containerId)); //changes the status of the object
    }
    public void renameContainer(String newName) {
        RenameContainerCmd renameContainerCmd = ClientUpdater.getUpdatedClient().renameContainerCmd
                (containerId).withName(newName);
        renameContainerCmd.exec(); //Renames in the Cluster
        System.out.println("Container with id: " + this.getContainerId() + "has been renamed to: " + newName + "\n\n");
        this.name = newName; //Renames the object
    }
    public void removeContainer() {
        RemoveContainerCmd removeContainerCmd = ClientUpdater.getUpdatedClient().removeContainerCmd(containerId);
        removeContainerCmd.exec(); //removes container from the cluster
        System.out.println("Container removed: " + containerId + "\n\n");
        containerslist.remove(this); //Removes container from our DockerInstance containers list
    }
    public void restartContainer() {
        RestartContainerCmd restartContainerCmd = ClientUpdater.getUpdatedClient().restartContainerCmd(containerId);
        restartContainerCmd.exec(); //restarts container in the cluster
        System.out.println("Container restarted: " + containerId + "\n\n");
        this.setContainerStatus(ClientUpdater.getUpdatedStatus(containerId)); //changes the status of the object
    }
    public void pauseContainer() {
        // Check if the container is already paused
        if (this.getContainerStatus().equalsIgnoreCase("paused")) {
            System.out.println("Container is already paused: " + containerId + "\n\n");
            return;
        }

        // If not paused, pause the container
        PauseContainerCmd pauseContainerCmd = ClientUpdater.getUpdatedClient().pauseContainerCmd(containerId);
        pauseContainerCmd.exec(); //Pauses the container in Docker Cluster
        System.out.println("Container paused: " + containerId + "\n\n");
        this.setContainerStatus(ClientUpdater.getUpdatedStatus(containerId)); //changes the status of the object
    }
    public void unpauseContainer() {
        // Check if the container is already unpaused
        if (this.getContainerStatus().equalsIgnoreCase("running")) {
            System.out.println("Container is already unpaused: " + containerId + "\n\n");
            return;
        }

        // If not unpaused, unpause the container
        UnpauseContainerCmd unpauseContainerCmd = ClientUpdater.getUpdatedClient().unpauseContainerCmd(containerId);
        unpauseContainerCmd.exec(); //Unpauses container in cluster
        System.out.println("Container unpaused: " + containerId + "\n\n");
        this.setContainerStatus(ClientUpdater.getUpdatedStatus(containerId)); //Unpauses our object by changing
        //the status.
    }
    public void killContainer() {
        KillContainerCmd killContainerCmd = ClientUpdater.getUpdatedClient().killContainerCmd(containerId);
        killContainerCmd.exec(); //Kills container in cluster
        System.out.println("Container killed: " + containerId + "\n\n");
        this.setContainerStatus(ClientUpdater.getUpdatedStatus(containerId)); //Changes the status
    }
    public void inspectContainer() { //this is wrong I will fix it
        InspectContainerCmd inspectContainerCmd = ClientUpdater.getUpdatedClient().inspectContainerCmd(containerId);
        inspectContainerCmd.exec();
        /* kai auto einai polu lathos alla ypo epejergasia
        String message = inspectContainerCmd.exec().toString();
        String[] parts = message.split("(?<=\\]),\\s");
        StringBuilder formattedMessage = new StringBuilder();
        for (String part : parts) {
            formattedMessage.append(part.replaceAll(",","\n")).append("/n");
        }
        System.out.println(formattedMessage.toString());
         */
    }

    //aid methods

    //Container Menu: Case 1: Show All Containers
    public static void listAllContainers() {
        System.out.println("Listing all the containers...\n.\n.\n.");
        int i = 0; //Numbers to make the output more user-friendly
        for (DockerInstance c : containerslist) {
            i++;
            c.setContainerStatus(ClientUpdater.getUpdatedStatus(c.getContainerId()));
            System.out.println(i + ") " + c);  //ennoeitai c.toSrting()
        }
    }
    //Container Menu: Case 2: Show Active Containers Only
    public static void listActiveContainers() {
        System.out.println("Listing active containers...\n.\n.\n.");
        int i = 0; //Numbers to make the output more User Friendly
        for (DockerInstance c : containerslist) {
            if(c.getContainerStatus().startsWith("Up")) { //Starts with because the status can be Up a second ago
                i++;
                c.setContainerStatus(ClientUpdater.getUpdatedStatus(c.getContainerId()));
                System.out.println(i + ") " + c); //Ennoeitai c.toString()
            }
        }
    }

    //This method is used when user wants to Stop, Pause, Kill an Active Container
    public static String chooseAnActiveContainer() { //returns container's id
        List<DockerInstance> actives = new ArrayList<>();
        int i = 1; //Output User Friendly
        for (DockerInstance c : containerslist) {
            if (c.getContainerStatus().startsWith("Up")) {
                System.out.println(i + ") " + c);
                actives.add(c);
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
        }  catch (InputMismatchException e) {
        System.out.println("Invalid input. Please choose on of the containers below.");
        in.nextLine(); // Consume the invalid input
        return chooseAnActiveContainer(); // Showing active containers again
    }
    }

    //chooseAStoppedContainer is used when user wants to Start, Restart, Unpause a Stopped Container
    public static String chooseAStoppedContainer() {
        List<DockerInstance> stopped = new ArrayList<>();
        int i=1; //Output more friendly and easier to the user to choose a container
        for (DockerInstance c :containerslist) {
            if (!(c.getContainerStatus().startsWith("Up"))) {
                System.out.println(i + ") " + c);
                stopped.add(c);
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

    //chooseAContainer when user wants to choose a container from all the containers
    //for example he wants to rename
    public static String chooseAContainer() {
        DockerInstance.listAllContainers();
        Scanner in = new Scanner(System.in);
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
    //this method is used to check in main if there are any active containers
    //giati otan epelega STOP a container kai den eixa kanena active to menu ebgaze na epilejw apo to tipota
    //User presses STOP and there are no Active Containers, He can't choose from nothing --> Exception
    public static boolean noActiveContainers() {
        boolean flag = true;
        for (DockerInstance c :containerslist) {
            if (c.getContainerStatus().startsWith("Up")) { //It means there is at least one active container
                flag = false;
                break;
            }
        }
        return flag;
    }

    /* den einai swsto alla nystaza opote avrio pali
    !! TO AVOID REPETITIONS OF CODE BECAUSE WE ALSE NEED chooseAnUnpausedContainer() and chooseAPausedContainer() !!
        public static String chooseBasedOnCondition(String condition) {
        List<DockerInstance> mylist = new ArrayList<>();
        int i=1;
        for (DockerInstance c :containerslist) {
            if (Boolean.parseBoolean(condition)) {
                System.out.println(i + ") " + "Name: " + c.getContainerName() + "  ID: " + c.getContainerId()
                        + "  Image: " + c.getContainerImage() + "  STATUS: " + c.getContainerStatus());
                mylist.add(c);
                i++;
            }
        }
        Scanner in = new Scanner(System.in);
        System.out.print("YOUR CHOICE---> ");
        int answer = in.nextInt();
        return containerslist.get(containerslist.indexOf(mylist.get(answer-1))).getContainerId();
    }
     */
}
