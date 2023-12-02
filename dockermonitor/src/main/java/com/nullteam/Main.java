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
            System.out.println("        ~Main Menu~     \n");
            System.out.println("(1) Inspect Containers\n(2) Inspect Images\n(*) Exit"); //more functionality to be added
            System.out.print("YOUR CHOICE ---> ");
            String menu = in.nextLine(); //Which menu to show
            try {
                switch (menu) {
                    case "1":
                        //Containers Menu
                        flagCon: //Creating a loop to stay in the containers menu untill user chooses to leave
                        while (true) {
                            System.out.println("\n          ~Container Menu~" +
                                    "\n         ------------------");
                            System.out.println("(1) View ALL the containers\n(2) View ACTIVE containers only" +
                                    "\n(3) Container Tools\n(4) Go Back(Main Menu)\nPress (*) to EXIT THE APP");
                            System.out.print("YOUR CHOICE ---> ");
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
                                            System.out.println("\n          ~Container Tools~" +
                                                    "\n         -------------------");
                                            System.out.println("(1) Stop a container\n(2) Start a container\n" +
                                                    "(3) Rename a container\n(4) Remove a container" +
                                                    "\n(5) Restart a container\n(6) Pause a container\n(7) Unpause a container" +
                                                    "\n(8) Kill a container\n(9) INSPECT a container\n(0) Go Back(Container Menu)" +
                                                    "\nPress (*) to EXIT THE APP");
                                            System.out.print("Tool Number: ");
                                            String ansT = in.nextLine();
                                            try {
                                                switch (ansT) {
                                                    case "1": //STOP a container
                                                        System.out.println("You chose: 1) Stop a container\n");
                                                        if (DockerInstance.noActiveContainers()) { //there is no reason to continue without active containers
                                                            System.out.println("There are no active containers.");
                                                            Thread.sleep(3000); //to show the message before the app goes back to the ~Container Tools~
                                                        } else {
                                                            System.out.println("Choose one of the active containers bellow " +
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
                                                        System.out.println("Choose one of the exited containers bellow " +
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
                                                        System.out.println("Choose one of the containers bellow " +
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
                                                        System.out.println("Choose one of the containers bellow " +
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
                                                            Thread.sleep(3000);
                                                        } else {
                                                            System.out.println("Choose one of the active containers bellow " +
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
                                                        if (DockerInstance.noActiveContainers()) {
                                                            System.out.println("There are no active containers.");
                                                            Thread.sleep(3000);
                                                        } else {
                                                            System.out.println("Choose one of the active containers bellow " +
                                                                    "to PAUSE it.");
                                                            String containerIdPause = DockerInstance.chooseAnActiveContainer();
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
                                                        if (DockerInstance.noActiveContainers()) {
                                                            System.out.println("There are no active containers.");
                                                            Thread.sleep(3000);
                                                        } else {
                                                            System.out.println("Choose one of the active containers bellow " +
                                                                    "to UNPAUSE it.");
                                                            String containerIdUnpause = DockerInstance.chooseAnActiveContainer();
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
                                                            Thread.sleep(3000);
                                                        } else {
                                                            System.out.println("Choose one of the active containers bellow " +
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
                                                    case "9": //inspect a container
                                                        System.out.println("You chose: 9) Inspect a container\n");
                                                        System.out.println("Choose one of the containers bellow " +
                                                                "to RENAME it.");
                                                        String containerIdInspect = DockerInstance.chooseAContainer();
                                                        ExecutorThread executor_inspect = new ExecutorThread
                                                                (containerIdInspect, ExecutorThread.TaskType.INSPECT);
                                                        executor_inspect.start();
                                                        System.out.println("Inspecting the container...");
                                                        try {
                                                            executor_inspect.join();
                                                        } catch (InterruptedException e) {
                                                            e.printStackTrace();
                                                        }
                                                        break;
                                                    case "0": //going back to container menu...
                                                        System.out.println("You chose: 0) Go back to Container Menu\n");
                                                        System.out.println("Transferring you to the Container Menu ...");
                                                        break flagTools;
                                                    case "*": //Exiting the app
                                                        System.out.println("\nBye..." +
                                                                "\n*******EXITING APP*******");
                                                        System.exit(0);
                                                        break;
                                                    default:
                                                        throw new IllegalStateException("Unexpected value: " + ansT);
                                                }
                                            } catch (IllegalStateException e) {
                                                System.out.println("Please choose one of the valid options below.\n");
                                            }
                                        }
                                        break;
                                    case "4": //going back to main menu...
                                        System.out.println("You chose: 4) Go Back(Main Menu)\n");
                                        System.out.println("Transferring you to the Main Menu");
                                        break flagCon;
                                    case "*": //Exiting the APP
                                        System.out.println("\nBye..." +
                                                "\n*******EXITING APP*******");
                                        System.exit(0);
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
                        flagImage: //loop to stay in Image Menu until user chooses to exit
                        while (true) {
                            System.out.println("\n            ~Image Menu~" +
                                    "\n         ------------------");
                            System.out.println("(1) View available images\n(2) Implement an image(start a new container)" +
                                    "\n(3) Go Back(Main Menu)\nPress (*) to EXIT THE APP");
                            System.out.print("YOUR CHOICE ---> ");
                            String ansI = in.nextLine();
                            try {
                                switch (ansI) {
                                    case "1":
                                        System.out.println("You chose: 1) View available images\n");
                                        DockerImage.listAllImages();
                                        break;
                                    case "2":
                                        System.out.println("You chose: 2) Implement an image(start a new container)\n");
                                        //Method to implement an image through a new container ~ TO BE MADE INSIDE THE DockerIm
                                        break;
                                    case "3": //going back to main menu...
                                        System.out.println("You chose: 3) Go Back(Main Menu)\n");
                                        System.out.println("Transferring you to the Main Menu ...");
                                        break flagImage;
                                    case "*": //Exiting the app
                                        System.out.println("\nBye..." +
                                                "\n*******EXITING APP*******");
                                        System.exit(0);
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
                        System.out.println("\nAre you sure you want to exit? Answer Y or N (Yes/No)\n");
                        System.out.print("Answer: ");
                        String finalAnswer = in.nextLine();
                        if (finalAnswer.equals("Y")) {
                            System.out.println("\nBye..." +
                                    "\n*******EXITING APP*******");
                            System.exit(0);
                            break;
                        } else {
                            System.out.println("Going Back to Main Menu ...");
                            break;
                        }
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