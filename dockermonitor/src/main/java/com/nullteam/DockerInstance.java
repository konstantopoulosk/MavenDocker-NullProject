package com.nullteam;

import com.github.dockerjava.api.command.StartContainerCmd;
import com.github.dockerjava.api.command.StopContainerCmd;
import com.github.dockerjava.api.command.RenameContainerCmd;
import java.util.Scanner;
import java.util.ArrayList;
import java.util.List;

public class DockerInstance {
    public static List<DockerInstance> containerslist = new ArrayList<>();
    //fields
    private String containerId;
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
        StopContainerCmd stopContainerCmd = ClientUpdater.getUpdatedClient().stopContainerCmd(containerId)
                .withTimeout(1);
        stopContainerCmd.exec();
        System.out.println("Container stopped: " + containerId);
        this.setContainerStatus("Exited");
    }
    public void startContainer() {
        StartContainerCmd startContainerCmd = ClientUpdater.getUpdatedClient().startContainerCmd(containerId);
        startContainerCmd.exec();
        System.out.println("Container started: " + containerId);
        this.setContainerStatus("Up");
    }
    public void renameContainer(String newName) {
        RenameContainerCmd renameContainerCmd = ClientUpdater.getUpdatedClient().renameContainerCmd
                (containerId).withName(newName);
        renameContainerCmd.exec();
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
            if(c.getContainerStatus().substring(0,2).equals("Up")) {
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
    public static void rename() {
        System.out.println("Choose one of the containers below to RENAME it.");
        String containerIdRename = DockerInstance.chooseAContainer();
        System.out.println("Give me the new name.");
        System.out.print("New Name: ");
        Scanner in = new Scanner(System.in);
        String newName = in.nextLine();
        ExecutorThread executor_rename = new ExecutorThread(containerIdRename, ExecutorThread.TaskType.RENAME, newName);
        executor_rename.start();
    }
}
