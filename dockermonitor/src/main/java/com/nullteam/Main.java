package com.nullteam;

import com.github.dockerjava.api.model.Container;
import com.github.dockerjava.api.model.Image;
import java.util.List;
import java.util.Scanner;


public class Main {
    public static void main(String[] args) throws InterruptedException {
        Scanner in = new Scanner(System.in);
        //Creating instances of DockerInstance and DockerImage using info from the DockerClient
        List<Image> images = ClientUpdater.getUpdatedImagesFromClient(); //Updated Client from ClientUpdater
        for (Image i : images) { //For every one object in the images list -> creating an object DockerImage
            new DockerImage(i.getRepoDigests()[0], "latest", i.getId());
        }
        List<Container> containers = ClientUpdater.getUpdatedContainersFromClient(); //updated list with containers
        for (Container c : containers) { //for every one object in the containers list -> create an object DockerINstance
            new DockerInstance(c.getNames()[0], c.getId(), c.getImage(), c.getStatus());
        }
        //Initialized the monitor thread
        DockerMonitor monitor = new DockerMonitor();
        monitor.start();
        //Initialized menu//
        System.out.println("Welcome!");
        for (;;) {
            Messages.mainMenu();
            String menu = in.nextLine(); //Which menu to show
            try {
                switch (menu) {
                    case "1":
                        //Containers Menu
                        System.out.println("\nTransferring you to the Container Menu ...");
                        flagCon: //Creating a loop to stay in the containers menu until user chooses to leave
                        while (true) {
                            Messages.containersMenu();
                            String ansC = in.nextLine();
                            try {
                                switch (ansC) {
                                    case "1": //printing ALL containers
                                        System.out.println("You chose: 1) View ALL the containers\n");
                                        DockerInstance.listAllContainers();
                                        break;
                                    case "2": //printing ACTIVE containers-only
                                        System.out.println("You chose: 2) View ACTIVE containers only\n");
                                        DockerInstance.listActiveContainers();
                                        break;
                                    case "3":
                                        System.out.println("You chose: 3) Container Tools\n");
                                        System.out.println("Transferring you to the Container Tools Menu ...");
                                        flagTools: //Another loop to stay into Container Tools menu until user
                                        while (true) { //chooses to exit this menu
                                            Messages.containerTools();
                                            String ansT = in.nextLine();
                                            try {
                                                switch (ansT) {
                                                    case "1": //STOP a container
                                                        System.out.println("You chose: 1) Stop a container\n");
                                                        if (DockerInstance.noActiveContainers()) { //there is no reason to continue without active containers
                                                            System.out.println("There are no active containers.");
                                                            Thread.sleep(2000); //to show the message before the app goes back to the ~Container Tools~
                                                        } else {
                                                            System.out.println("Choose one of the active containers below " +
                                                                    "to STOP it.");
                                                            String containerIdStop = DockerInstance.chooseAnActiveContainer();
                                                            ExecutorThread executor_stop = new ExecutorThread
                                                                    (containerIdStop, ExecutorThread.TaskType.STOP);
                                                            executor_stop.start();
                                                            System.out.println("Stopping the container...");
                                                            try {
                                                                executor_stop.join(); // waiting for the thread to finish
                                                            } catch (InterruptedException e) {
                                                                e.printStackTrace();
                                                            }
                                                        }
                                                        break;
                                                    case "2": //START a container
                                                        System.out.println("You chose: 2) Start a container\n");
                                                        System.out.println("Choose one of the exited containers below " +
                                                                "to START it.");
                                                        String containerIdStart = DockerInstance.chooseAStoppedContainer();
                                                        ExecutorThread executor_start = new ExecutorThread
                                                                (containerIdStart, ExecutorThread.TaskType.START);
                                                        executor_start.start();
                                                        System.out.println("Starting the container...");
                                                        try {
                                                            executor_start.join(); // waiting for the thread to finish
                                                        } catch (InterruptedException e) {
                                                            e.printStackTrace();
                                                        }
                                                        break;
                                                    case "3": //RENAME a container
                                                        System.out.println("You chose: 3) Rename a container\n");
                                                        System.out.println("Choose one of the containers below " +
                                                                "to RENAME it.");
                                                        String containerIdRename = DockerInstance.chooseAContainer();
                                                        System.out.print("New name: ");
                                                        String newName = in.nextLine();
                                                        ExecutorThread executor_rename = new ExecutorThread
                                                                (containerIdRename, ExecutorThread.TaskType.RENAME, newName);
                                                        executor_rename.start();
                                                        System.out.println("Renaming the container...");
                                                        try {
                                                            executor_rename.join(); // waiting for the thread to finish
                                                        } catch (InterruptedException e) {
                                                            e.printStackTrace();
                                                        }
                                                        break;
                                                    case "4": //REMOVE a container
                                                        System.out.println("You chose: 4) Remove a container\n");
                                                        System.out.println("Choose one of the containers below " +
                                                                "to REMOVE it.");
                                                        String containerIdRemove = DockerInstance.chooseAContainer();
                                                        ExecutorThread executor_remove = new ExecutorThread
                                                                (containerIdRemove, ExecutorThread.TaskType.REMOVE);
                                                        executor_remove.start();
                                                        System.out.println("Removing the container...");
                                                        try {
                                                            executor_remove.join();
                                                        } catch (InterruptedException e) {
                                                            e.printStackTrace();
                                                        }
                                                        break;
                                                    case "5": //RESTART a container
                                                        System.out.println("You chose: 5) Restart a container\n");
                                                        if (DockerInstance.noActiveContainers()) {
                                                            System.out.println("There are no active containers.");
                                                            Thread.sleep(2000);
                                                        } else {
                                                            System.out.println("Choose one of the active containers below " +
                                                                    "to RESTART it.");
                                                            String containerIdRestart = DockerInstance.chooseAnActiveContainer();
                                                            ExecutorThread executor_restart = new ExecutorThread
                                                                    (containerIdRestart, ExecutorThread.TaskType.RESTART);
                                                            executor_restart.start();
                                                            System.out.println("Restarting the container...");
                                                            try {
                                                                executor_restart.join(); // waiting for the thread to finish
                                                            } catch (InterruptedException e) {
                                                                e.printStackTrace();
                                                            }
                                                        }
                                                        break;
                                                    case "6": //PAUSE a container
                                                        System.out.println("You chose: 6) Pause a container\n");
                                                        if (DockerInstance.noUnpausedContainers()) {
                                                            System.out.println("There are no active unpaused containers.");
                                                            Thread.sleep(2000);
                                                        } else {
                                                            System.out.println("Choose one of the active containers below " +
                                                                    "to PAUSE it.");
                                                            String containerIdPause = DockerInstance.chooseAnUnpausedContainer();
                                                            ExecutorThread executor_pause = new ExecutorThread
                                                                    (containerIdPause, ExecutorThread.TaskType.PAUSE);
                                                            executor_pause.start();
                                                            System.out.println("Pausing the container...");
                                                            try {
                                                                executor_pause.join(); // waiting for the thread to finish
                                                            } catch (InterruptedException e) {
                                                                e.printStackTrace();
                                                            }
                                                        }
                                                        break;
                                                    case "7": //UNPAUSE a container
                                                        System.out.println("You chose: 7) Unpause a container\n");
                                                        if (DockerInstance.noPausedContainers()) {
                                                            System.out.println("There are no paused containers.");
                                                            Thread.sleep(2000);
                                                        } else {
                                                            System.out.println("Choose one of the paused containers below " +
                                                                    "to UNPAUSE it.");
                                                            String containerIdUnpause = DockerInstance.chooseAPausedContainer();
                                                            ExecutorThread executor_unpause = new ExecutorThread
                                                                    (containerIdUnpause, ExecutorThread.TaskType.UNPAUSE);
                                                            executor_unpause.start();
                                                            System.out.println("Unpausing the container...");
                                                            try {
                                                                executor_unpause.join(); // waiting for the thread to finish
                                                            } catch (InterruptedException e) {
                                                                e.printStackTrace();
                                                            }
                                                        }
                                                        break;
                                                    case "8": //KILL a container
                                                        System.out.println("You chose: 8) Kill a container\n");
                                                        if (DockerInstance.noActiveContainers()) {
                                                            System.out.println("There are no active containers.");
                                                            Thread.sleep(2000);
                                                        } else {
                                                            System.out.println("Choose one of the active containers below " +
                                                                    "to KILL it.");
                                                            String containerIdKill = DockerInstance.chooseAnActiveContainer();
                                                            ExecutorThread executor_kill = new ExecutorThread
                                                                    (containerIdKill, ExecutorThread.TaskType.KILL);
                                                            executor_kill.start();
                                                            System.out.println("Killing the container...");
                                                            try {
                                                                executor_kill.join();
                                                            } catch (InterruptedException e) {
                                                                e.printStackTrace();
                                                            }
                                                        }
                                                        break;
                                                    case "..": //going back to container menu...
                                                        System.out.println("You chose: ..) Go back to Container Menu\n");
                                                        System.out.println("Transferring you to the Container Menu ...");
                                                        break flagTools;
                                                    case "*": //Exiting the app
                                                        Messages.exitApp();
                                                        break;
                                                    default:
                                                        throw new IllegalStateException("Unexpected value: " + ansT);
                                                }
                                            } catch (IllegalStateException e) {
                                                System.out.println("Please choose one of the valid options below.\n");
                                            }
                                        }
                                        break;
                                    case "..": //going back to main menu...
                                        System.out.println("You chose: ..) Go Back(Main Menu)\n");
                                        System.out.println("Transferring you to the Main Menu ...");
                                        break flagCon;
                                    case "*": //Exiting the APP
                                        Messages.exitApp();
                                        break;
                                    default:
                                        throw new IllegalStateException("Unexpected value: " + ansC);
                                }
                            } catch (IllegalStateException e) {
                                System.out.println("Please choose one of the valid options below.\n");
                            }
                        }
                        break; //end of case 1
                    case "2": // Image menu
                        System.out.println("\nTransferring you to the Image Menu ...");
                        flagImage: //loop to stay in Image Menu until user chooses to exit
                        while (true) {
                            Messages.imagesMenu();
                            String ansI = in.nextLine();
                            try {
                                switch (ansI) {
                                    case "1":
                                        System.out.println("You chose: 1) View available images\n");
                                        DockerImage.listAllImages();
                                        break;
                                    case "2":
                                        System.out.println("You chose: 2) Implement an image(start a new container)\n");
                                        System.out.println("Choose one of the images below to IMPLEMENT it.");
                                        String imageIdImplement = DockerImage.chooseAnImage();
                                        ExecutorThread executor_implementImage = new ExecutorThread
                                                (imageIdImplement, ExecutorThread.TaskType.IMPLEMENT);
                                        executor_implementImage.start();
                                        System.out.println("Creating a new instance of this image...");
                                        try {
                                            executor_implementImage.join(); // waiting for the thread to finish
                                        } catch (InterruptedException e) {
                                            e.printStackTrace();
                                        }
                                        break;
                                    case "..": //going back to main menu...
                                        System.out.println("You chose: ..) Go Back(Main Menu)\n");
                                        System.out.println("Transferring you to the Main Menu ...");
                                        break flagImage;
                                    case "*": //Exiting the app
                                        Messages.exitApp();
                                        break;
                                    default:
                                        throw new IllegalStateException("Unexpected value: " + ansI);
                                }
                            } catch (IllegalStateException e) {
                                System.out.println("Please choose one of the valid options below.\n");
                            }
                        }
                        break; //end of case 2
                    case "*"://exiting APP!!!
                        Messages.exitApp();
                        break;
                    default:
                        throw new IllegalStateException("Unexpected value: " + menu);
                }
            }
            catch(IllegalStateException e){
                System.out.println("Please choose one of the valid options below.\n");
            }
        }
    }
}
