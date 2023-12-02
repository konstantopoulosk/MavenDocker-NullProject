package com.nullteam;

import com.github.dockerjava.api.model.Container;
import com.github.dockerjava.api.model.Image;
import java.util.List;
import java.util.Scanner;


public class Main {
    public static void main(String[] args) throws InterruptedException {
        Scanner in = new Scanner(System.in);
        //Creating instances of DockerInstance and DockerImage using info from the DockerClient
        List<Image> images = ClientUpdater.getUpdatedImagesFromClient();
        for (Image i : images) {
            new DockerImage(i.getRepoDigests()[0], "latest", i.getId());
        }
        List<Container> containers = ClientUpdater.getUpdatedContainersFromClient();
        for (Container c : containers) {
            new DockerInstance(c.getNames()[0], c.getId(), c.getImage(), c.getStatus());
        }
        //Initialized the monitor thread
        DockerMonitor monitor = new DockerMonitor();
        monitor.start();
        //Initialized menu//
        System.out.println("Welcome!" +
                "\n          ~Main Menu~" +
                "\n          -----------");
        for (; ; ) {
            System.out.println("(1) Inspect Containers\n(2) Inspect Images\n(3) Exit"); //more functionality to be added
            System.out.print("---> ");
            String menu = in.nextLine(); //Which menu to show
            try {
                switch (menu) {
                    case "1":
                        //Containers Menu
                        flagCon:
                        while (true) {
                            System.out.println("\n          ~Container Menu~" +
                                    "\n         ------------------");
                            System.out.println("(1) View ALL the containers\n(2) View ACTIVE containers only" +
                                    "\n(3) Container Tools\n(4) Go Back(Main Menu)");
                            System.out.print("---> ");
                            String ansC = in.nextLine();
                            try {
                                switch (ansC) {
                                    case "1": //printing ALL containers
                                        DockerInstance.listAllContainers();
                                        break;
                                    case "2": //printing ACTIVE containers-only
                                        DockerInstance.listActiveContainers();
                                        break;
                                    case "3":
                                        flagTools:
                                        while (true) {
                                            System.out.println("\n          ~Container Tools~" +
                                                    "\n         -------------------");
                                            System.out.println("(1) Stop a container\n(2) Start a container\n" +
                                                    "(3) Rename a container\n(4) Remove a container" +
                                                    "\n(5) Restart a container\n(6) Pause a container\n(7) Unpause a container" +
                                                    "\n(8) Kill a container\n(9) INSPECT a container\n(0) Go Back(Container Menu)");
                                            System.out.print("---> ");
                                            String ansT = in.nextLine();
                                            try {
                                            switch (ansT) {
                                                case "1": //STOP a container
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
                                                case "9":
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
                                                    break flagTools;
                                                default:
                                                    throw new IllegalStateException("Unexpected value: " + ansT);
                                            }
                                        } catch (IllegalStateException e) {
                                                System.out.println("Please choose one of the valid options below.\n");
                                            }
                                        }
                                        break;
                                    case "4": //going back to main menu...
                                        break flagCon;
                                    default:
                                        throw new IllegalStateException("Unexpected value: " + ansC);
                                }
                            } catch (IllegalStateException e) {
                                System.out.println("Please choose one of the valid options below.\n");
                            }
                        }
                            break; //end of case 1
                    case "2": // Image menu
                        flagImage:
                        while (true) {
                            System.out.println("\n            ~Image Menu~" +
                                    "\n         ------------------");
                            System.out.println("(1) View available images\n(2) Implement an image(start a new container)" +
                                    "\n(3) Go Back(Main Menu)");
                            System.out.print("---> ");
                            String ansI = in.nextLine();
                            try {
                                switch (ansI) {
                                    case "1":
                                        DockerImage.listAllImages();
                                        break;
                                    case "2":
                                        //Method to implement an image through a new container ~ TO BE MADE INSIDE THE DockerIm
                                        break;
                                    case "3": //going back to main menu...
                                        break flagImage;
                                    default:
                                        throw new IllegalStateException("Unexpected value: " + ansI);
                                }
                            } catch (IllegalStateException e) {
                                System.out.println("Please choose one of the valid options below.\n");
                            }
                        }
                        break; //end of case 2
                    case "3"://exiting APP!!!
                        System.out.println("\nBye..." +
                                "\n*******EXITING APP*******");
                        System.exit(0);
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
