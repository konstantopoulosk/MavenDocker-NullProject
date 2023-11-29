package com.nullteam;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.model.Container;
import com.github.dockerjava.core.DefaultDockerClientConfig;
import com.github.dockerjava.core.DockerClientBuilder;

import java.io.IOException;
import java.util.List;
import java.util.Scanner;


public class Main {
    public static void main(String[] args) throws InterruptedException {

        //Creating instances of DockerInstance and DockerImage using info from the DockerClient
        DefaultDockerClientConfig.Builder builder= DefaultDockerClientConfig.createDefaultConfigBuilder();
        builder.withDockerHost("tcp://localhost:2375");
        DockerClient dockerClient = DockerClientBuilder.getInstance(builder).build();
        dockerClient.versionCmd().exec();
        List<Container> containers; // instances
        containers = dockerClient.listContainersCmd().withShowAll(true).exec();
        for (Container c : containers) {
            new DockerInstance(c.getNames()[0], c.getId(), new DockerImage
                    (c.getImageId(), c.getImage(),"latest"), c.getStatus());
        }
        //Initialized the monitor thread
        DockerMonitor monitor = new DockerMonitor();
        monitor.start();
        //Initialized menu//
        System.out.println("Welcome null_team, boot-up menu *v.1* starting...");
        Scanner in = new Scanner(System.in);
        for(;;) {
            System.out.println("IMAGES MENU (1) / CONTAINERS MENU (2) / EXIT APP (3)");
            int menu = in.nextInt(); //Which menu to show
            switch (menu) {
                case 1:
                    //images menu
                    boolean flagI = true; //Flag to exit Images Menu
                    while (flagI) {
                        System.out.println(" \nIMAGES MENU\n");
                        System.out.println("Choose one: \n1)Show All Images\n2)EXIT THIS MENU\n3)EXIT APP");
                        System.out.print("Your Choice ---> ");
                        int ansI = in.nextInt();
                        switch (ansI) {
                            case 1:
                                System.out.println("Listing all Images ...");
                                break;
                            case 2:
                                System.out.println("EXITING THIS MENU ...");
                                flagI = false;
                                break;
                            case 3:
                                System.out.println("EXITING APP ...");
                                try {
                                    dockerClient.close();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                                System.exit(0);
                                break;
                            default:
                                throw new IllegalStateException("Unexpected value: " + ansI);
                        }
                    }
                    break;
                case 2:
                    //containers menu
                    boolean flagC = true; //Flag to exit Containers menu
                    while (flagC) {
                        System.out.println(" \nCONTAINERS MENU\n");
                        System.out.println("Choose one:\n1)Show ALL containers\n2)Show ACTIVE containers only" +
                                "\n3)Stop a container\n4)Start a container\n5)Rename a container\n6)Remove a container" +
                                "\n7)Restart a container\n8)Pause a container\n9)Unpause a container" +
                                "\n10)Kill a container\n11)EXIT THIS MENU\n12)EXIT APP");
                        System.out.print("Your Choice ---> ");
                        int ansC = in.nextInt();
                        switch (ansC) {
                            case 1:
                                System.out.println("--------------------------ALL CONTAINER INSTANCES--------------------------");
                                DockerInstance.listAllContainers();
                                System.out.println(" ");
                                break;
                            case 2:
                                System.out.println("------------------------- ACTIVE CONTAINER INSTANCES -----------------------");
                                DockerInstance.listActiveContainers();
                                System.out.println(" ");
                                break;
                            case 3:
                                //stop a container.
                                System.out.println("Choose one of the active containers bellow to STOP it.");
                                String containeridStop = DockerInstance.chooseAnActiveContainer();
                                ExecutorThread executor_stop = new ExecutorThread
                                        (containeridStop, ExecutorThread.TaskType.STOP, null);
                                executor_stop.start();
                                System.out.println(" ");
                                break;
                            case 4:
                                //start a container.
                                String containeridStart = DockerInstance.chooseAStoppedContainer();
                                ExecutorThread executor_start = new ExecutorThread
                                        (containeridStart, ExecutorThread.TaskType.START, null);
                                executor_start.start();
                                System.out.println(" ");
                                break;
                            case 5:
                                //rename a container. Eftiaksa methodo rename gia help epeidi zitaei kai neo onoma apo ton xristi.
                                rename();
                                break;
                            case 6:
                                //remove a container.
                                System.out.println("Choose one of the containers below to REMOVE it.");
                                String containerIdRemove = DockerInstance.chooseAContainer();
                                ExecutorThread executor_remove = new ExecutorThread(containerIdRemove, ExecutorThread.TaskType.REMOVE, null);
                                executor_remove.start();
                                System.out.println(" ");
                                break;
                            case 7:
                                //restart a container.
                                System.out.println("Choose one of the containers below to RESTART it.");
                                String containerIdRestart = DockerInstance.chooseAContainer();
                                ExecutorThread executor_restart = new ExecutorThread(containerIdRestart, ExecutorThread.TaskType.RESTART, null);
                                executor_restart.start();
                                break;
                            case 8:
                                //pause a container.
                                System.out.println("Choose one of the active containers below to PAUSE it.");
                                String containerIdPause = DockerInstance.chooseAnActiveContainer();
                                ExecutorThread executor_pause = new ExecutorThread(containerIdPause, ExecutorThread.TaskType.PAUSE, null);
                                executor_pause.start();
                                break;
                            case 9:
                                //unpause a container.
                                System.out.println("Choose one of the containers below to UNPAUSE it.");
                                String containerIdUnpause = DockerInstance.chooseAContainer();
                                ExecutorThread executor_unpause = new ExecutorThread(containerIdUnpause, ExecutorThread.TaskType.UNPAUSE, null);
                                executor_unpause.start();
                                break;
                            case 10:
                                //kill a container.
                                System.out.println("Choose one of the containers below to KILL it."); //KILL = FORCE STOP
                                String containerIdKill = DockerInstance.chooseAContainer();
                                ExecutorThread executor_kill = new ExecutorThread(containerIdKill, ExecutorThread.TaskType.KILL, null);
                                executor_kill.start();
                                break;
                            case 11:
                                System.out.println(" \nEXITING CONTAINERS MENU\n");
                                flagC = false;
                                break;
                            case 12:
                                System.out.println("EXITING APP ...");
                                try {
                                    dockerClient.close();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                                System.exit(0);
                                break;
                            default:
                                throw new IllegalStateException("Unexpected value: " + ansC);
                        }
                    }
                    break;
                case 3:
                    //exiting APP!!!
                    System.out.println("EXITING APP ...");
                    try {
                        dockerClient.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    System.exit(0);
                    break;
                default:
                    throw new IllegalStateException("Unexpected value: " + menu);
            }
        }
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