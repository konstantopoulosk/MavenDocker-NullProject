package com.nullteam;

import com.github.dockerjava.api.model.Container;
import java.util.List;
import java.util.Scanner;


public class Main {
    public static void main(String[] args) {
        Scanner in = new Scanner(System.in);
        //Creating instances of DockerInstance and DockerImage using info from the DockerClient
        List<Container> containers = ClientUpdater.getUpdatedContainersFromClient();
        for (Container c : containers) {
            new DockerInstance(c.getNames()[0], c.getId(), new DockerImage
                    (c.getImageId(), c.getImage(),"latest"), c.getStatus());
        }
        //Initialized the monitor thread
        DockerMonitor monitor = new DockerMonitor();
        monitor.start();
        //Initialized menu//
        System.out.println("Welcome!" +
                "\n          ~Main Menu~" +
                "\n          -----------");
        for(;;) {
            System.out.println("(1) Inspect Containers\n(2) Inspect Images\n(3) Exit"); //more functionality to be added
            System.out.print("---> ");
            String menu = in.nextLine(); //Which menu to show
            switch (menu) {
                case "1":
                    //Containers Menu
                    flagCon:while(true) {
                        System.out.println("          ~Container Menu~" +
                                           "\n         ------------------");
                        System.out.println("(1) View ALL the containers\n(2) View ACTIVE containers only" +
                                "\n(3) Container Tools\n(4) Go Back(Main Menu)");
                        System.out.print("---> ");
                        String ansC = in.nextLine();
                        switch (ansC) {
                            case "1": //printing ALL containers
                                DockerInstance.listAllContainers();
                                break;
                            case "2": //printing ACTIVE containers-only
                                DockerInstance.listActiveContainers();
                                break;
                            case "3":
                                flagTools:while(true) {
                                    System.out.println("          ~Container Tools~" +
                                                       "\n         -------------------");
                                    System.out.println("\n(1)Stop a container\n(2)Start a container\n" +
                                            "(3)Rename a container\n(4)Remove a container" +
                                            "\n(5)Restart a container\n(6)Pause a container\n(7)Unpause a container" +
                                            "\n(8)Kill a container\n(9)Go Back(Container Menu)");
                                    System.out.print("--->");
                                    String ansT = in.nextLine();
                                    switch (ansT) {
                                        case "1": //STOP a container
                                            System.out.println("Choose one of the active containers bellow " +
                                                    "to STOP it.");
                                            String containerIdStop = DockerInstance.chooseAnActiveContainer();
                                            ExecutorThread executor_stop = new ExecutorThread
                                                    (containerIdStop, ExecutorThread.TaskType.STOP);
                                            executor_stop.start();
                                            break;
                                        case "2": //START a container
                                            System.out.println("Choose one of the exited containers bellow " +
                                                    "to START it.");
                                            String containerIdStart = DockerInstance.chooseAStoppedContainer();
                                            ExecutorThread executor_start = new ExecutorThread
                                                    (containerIdStart, ExecutorThread.TaskType.START);
                                            executor_start.start();
                                            System.out.println(" ");
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
                                            break;
                                        case "4": //REMOVE a container
                                            break;
                                        case "5": //RESTART a container
                                            break;
                                        case "6": //PAUSE a container
                                            break;
                                        case "7": //UNPAUSE a container
                                            break;
                                        case "8": //KILL a container
                                            break;
                                        case "9": //going back to container menu...
                                            break flagTools;
                                    }
                                }
                                break;
                            case "4": //going back to main menu...
                                break flagCon;
                            default:
                                throw new IllegalStateException("Unexpected value: " + ansC);
                        }
                    }
                    break; //end of case 1
                case "2": // Image menu
                    flagImage:while (true) {
                        System.out.println("          ~Image Menu~" +
                                "\n         ------------------");
                        System.out.println("(1) View available images\n(2) Implement an image(start a new container)" +
                                "\n(3) Go Back(Main Menu)");
                        System.out.print("---> ");
                        String ansI = in.nextLine();
                        switch (ansI) {
                            case "1":
                                //Method to view available images ~ TO BE MADE INSIDE THE DockerImage CLASS
                                break;
                            case "2":
                                //Method to implement an image through a new container ~ TO BE MADE INSIDE THE DockerIm
                                break;
                            case "3": //going back to main menu...
                                break flagImage;
                        }
                    }
                    break;
                case "3":
                    //exiting APP!!!
                    System.out.println("\nBye..." +
                            "\n*******EXITING APP*******");
                    System.exit(0);
                    break;
                default:
                    throw new IllegalStateException("Unexpected value: " + menu);
            }
        }
    }
}