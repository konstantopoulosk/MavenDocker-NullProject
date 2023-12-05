package com.nullteam;

import com.github.dockerjava.api.model.Container;
import com.github.dockerjava.api.model.Image;
import java.util.List;
import java.util.Scanner;

public class GetHelp {
    public static void startImage() {
        //Creating instances of DockerInstance and
        //DockerImage using info from the DockerClient
        List<Image> images =
                ClientUpdater.getUpdatedImagesFromClient();
        //Updated Client from ClientUpdater
        for (Image i : images) { //For every one object in the images list ->
                                 // creating an object DockerImage
            new DockerImage(i.getRepoDigests()[0],
                    "Latest", i.getId());
        }
    }
    public static void startContainers() {
        List<Container> containers =
                ClientUpdater.getUpdatedContainersFromClient();
        //updated list with containers
        for (Container c : containers) {
            //for every one object in the containers list ->
            // create an object DockerInstance
            new DockerInstance(c.getNames()[0], c.getId(),
                    c.getImage(), c.getStatus());
        }
    }
    public static void case1Stop() {
        System.out.println("You chose: 1) Stop a container\n");
        if (DockerInstance.noActiveContainers()) { //there is no reason to continue without active containers
            System.out.println("There are no active containers.");
            try {
                Thread.sleep(2000); //to show the message before the app goes back to the ~Container Tools~
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
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
    } //end case 1: Stop
    public static void case2Start() {
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
    } //end case 2: Start
    public static void case3Rename() {
        Scanner in = new Scanner(System.in);
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
    } //end case 3: Rename
    public static void case4Remove() {
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
    } //end case 4: Remove
    public static void case5Restart() {
        System.out.println("You chose: 5) Restart a container\n");
        if (DockerInstance.noActiveContainers()) {
            System.out.println("There are no active containers.");
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
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
    } //end case 5: Restart
    public static void case6Pause() {
        System.out.println("You chose: 6) Pause a container\n");
        if (DockerInstance.noUnpausedContainers()) {
            System.out.println("There are no active unpaused containers.");
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        } else {
            System.out.println("Choose one of the active containers bellow " +
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
    } //end case 6: Pause
    public static void case7Unpause() {
        System.out.println("You chose: 7) Unpause a container\n");
        if (DockerInstance.noPausedContainers()) {
            System.out.println("There are no paused containers.");
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        } else {
            System.out.println("Choose one of the paused containers bellow " +
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
    } //end case 7: Unpause
    public static void case8Kill() {
        System.out.println("You chose: 8) Kill a container\n");
        if (DockerInstance.noActiveContainers()) {
            System.out.println("There are no active containers.");
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
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
    } //end case 8: Kill
    public static void goToContMenu() {
        System.out.println("You chose: ..) "
               + "Go back to Container Menu\n");
        System.out.println("Transferring you "
               + "to the Container Menu ...");
    }
    public static void goToMainMenu() {
        System.out.println("You chose: ..) "
               + "Go Back(Main Menu)\n");
        System.out.println("Transferring you "
               + "to the Main Menu ...");
    }
    public static void case2Implement() {
        System.out.println("You chose: 2) Implement an image(start a new container)\n");
        System.out.println("Choose one of the images bellow to IMPLEMENT it.");
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
    } //end Image menu case 2: Implement an image
    public static void goToContTools() {
        System.out.println("You chose: "
               + "3) Container Tools\n");
        System.out.println("Transferring you to the "
               + "Container Tools Menu ...");
    }
    public static void repChoice() {
        System.out.println("Please choose one of the valid options below.\n");
    }
    public static void thr(String a) throws IllegalStateException {
        throw new IllegalStateException("Unexpected value: " + a);
    } //Method for Unexpected Values
    public static void goToImMenu() {
        System.out.println("\nTransferring you to the Image Menu ...");
    }
    public static void imageCase1() {
        System.out.println("You chose: 1) View available images\n");
        DockerImage.listAllImages();
    }
}
