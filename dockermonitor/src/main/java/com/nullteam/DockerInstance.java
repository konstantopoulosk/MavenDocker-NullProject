package com.nullteam;

import com.github.dockerjava.api.command.StartContainerCmd;
import com.github.dockerjava.api.command.StopContainerCmd;
import com.github.dockerjava.api.command.RenameContainerCmd;
import java.util.Scanner;
import java.util.ArrayList;
import java.util.List;
import com.github.dockerjava.api.exception.NotModifiedException;



public class DockerInstance {
    public static List<DockerInstance> containerslist = new ArrayList<>();
    //fields
    private final String containerId;
    // enas container afora mia mono eikona, h idia eikona mporei na xrisimopoietai se pollous diaforetikous containers
    private DockerImage dockerImage; // to image pou afora o container
    private String status;
    private String name;
    //constructor
    public DockerInstance(String name, String containerId, DockerImage dockerImage, String status) {
        this.name = name;
        this.containerId = containerId;
        this.dockerImage = dockerImage;
        this.status = status;
        containerslist.add(this);
    }
    //getters
    public String getContainerId() {
        return containerId;
    }
    public String getContainerName() {
        return name;
    }
    public DockerImage getContainerImage() {
        return dockerImage;
    }
    public String getContainerStatus() {
        return status;
    }
    public void setContainerStatus(String status) {
        this.status = status;
    }
    //tools
    public void stopContainer() {
        try {
        StopContainerCmd stopContainerCmd = ClientUpdater.getUpdatedClient().stopContainerCmd(containerId)
                .withTimeout(10);
        stopContainerCmd.exec();
        System.out.println("Container stopped: " + containerId + "\n\n");
        this.setContainerStatus("Exited");
    } catch (NotModifiedException e) {
        // Container was already stopped or in the process of stopping
        System.out.println("Container is already stopped or in the process of stopping: " + containerId + "\n\n");
        this.setContainerStatus("Exited");
    } catch (Exception e) {
        // Handle other exceptions
        e.printStackTrace();
    }
    }
    public void startContainer() {
        StartContainerCmd startContainerCmd = ClientUpdater.getUpdatedClient().startContainerCmd(containerId);
        startContainerCmd.exec();
        System.out.println("Container started: " + containerId + "\n\n");
        this.setContainerStatus("Up");
    }
    public void renameContainer(String newName) {
        RenameContainerCmd renameContainerCmd = ClientUpdater.getUpdatedClient().renameContainerCmd
                (containerId).withName(newName);
        renameContainerCmd.exec();
        System.out.println("Container with id: " + this.getContainerId() + "has been renamed to: " + newName + "\n\n");
        this.name = newName;
    }


    //aid methods
    public static void listAllContainers() {
        System.out.println("Listing all the containers...\n.\n.\n.");
        int i = 0;
        for (DockerInstance c : containerslist) {
            i++;
            System.out.println(i + ") Name: " + c.getContainerName() + "  ID: " + c.getContainerId()
                    + "  Image: " + c.getContainerImage().getImageName() + "  STATUS: " + c.getContainerStatus());
        }
    }
    public static void listActiveContainers() {
        System.out.println("Listing active containers...\n.\n.\n.");
        int i = 0;
        for (DockerInstance c : containerslist) {
            if(c.getContainerStatus().startsWith("Up")) {
                i++;
                System.out.println(i+") Name: " + c.getContainerName() + "  ID: " + c.getContainerId()
                        + "  Image: " + c.getContainerImage().getImageName() + "  STATUS: " + c.getContainerStatus());
            }
        }
    }
    public static String chooseAnActiveContainer() { //returns container's id
        List<DockerInstance> actives = new ArrayList<>();
        int i=1;
        for (DockerInstance c :containerslist) {
            if (c.getContainerStatus().startsWith("Up")) {
                System.out.println(i + ") " + "Name: " + c.getContainerName() + "  ID: " + c.getContainerId()
                        + "  Image: " + c.getContainerImage().getImageName() + "  STATUS: " + c.getContainerStatus());
                actives.add(c);
                i++;
            }
        }
        Scanner in = new Scanner(System.in);
        System.out.print("YOUR CHOICE---> ");
        int answer = in.nextInt();
        return containerslist.get(containerslist.indexOf(actives.get(answer-1))).getContainerId();
    }
    public static String chooseAStoppedContainer() {
        System.out.println("Choose one of the containers bellow to START it.");
        List<DockerInstance> stopped = new ArrayList<>();
        int i=1;
        for (DockerInstance c :containerslist) {
            if (!(c.getContainerStatus().startsWith("Up"))) {
                System.out.println(i + ") " + "Name: " + c.getContainerName() + "  ID: " + c.getContainerId()
                        + "  Image: " + c.getContainerImage().getImageName() + "  STATUS: " + c.getContainerStatus());
                stopped.add(c);
                i++;
            }
        }
        Scanner in = new Scanner(System.in);
        System.out.print("YOUR CHOICE---> ");
        int answer = in.nextInt();
        return containerslist.get(containerslist.indexOf(stopped.get(answer-1))).getContainerId();
    }
    public static String chooseAContainer() {
        DockerInstance.listAllContainers();
        Scanner in = new Scanner(System.in);
        int choice = in.nextInt();
        return containerslist.get(choice - 1).getContainerId();
    }
}
