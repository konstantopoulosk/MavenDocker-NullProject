package com.nullteam;

import com.github.dockerjava.api.command.PullImageCmd;
import com.github.dockerjava.api.command.PullImageResultCallback;
import com.github.dockerjava.api.model.Container;
import com.github.dockerjava.api.model.Image;
import com.github.dockerjava.api.model.PullResponseItem;

import java.util.List;
import java.util.Scanner;
import java.util.concurrent.Executor;

final class GetHelp {
    private GetHelp() {
    }
    public static void listImage() {
        //Creating instances of DockerInstance and
        //DockerImage using info from the DockerClient
        List<Image> images =
                ClientUpdater.getUpdatedImagesFromClient();
        //Updated Client from ClientUpdater
        for (Image i : images) { //For every one object in the images list ->
                                 // creating an object DockerImage
            new DockerImage(i.getRepoDigests()[0],
                    "latest", i.getId());
        }
    }
    public static void listContainers() {
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
        if (DockerInstance.noActiveContainers()) {
            //there is no reason to continue without active containers
            System.out.println("There are no active containers.");
            try {
                final int m = 2000;
                Thread.sleep(m);
                //to show the message before the app goes back to
                //the ~Container Tools~
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        } else {
            System.out.println("Choose one of the active containers bellow "
                   + "to STOP it.");
            String containerIdStop = DockerInstance.chooseAnActiveContainer();
            ExecutorThread executorStop = new ExecutorThread(
                    containerIdStop, ExecutorThread.TaskType.STOP);
            executorStop.start();
            System.out.println("Stopping the container...");
            try {
                executorStop.join(); // waiting for the thread to finish
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    } //end case 1: Stop
    public static void case2Start() {
        System.out.println("You chose: 2) Start a container\n");
        System.out.println("Choose one of the exited containers bellow "
               + "to START it.");
        String containerIdStart = DockerInstance.chooseAStoppedContainer();
        ExecutorThread executorStart = new ExecutorThread(
                containerIdStart, ExecutorThread.TaskType.START);
        executorStart.start();
        System.out.println("Starting the container...");
        try {
            executorStart.join(); // waiting for the thread to finish
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    } //end case 2: Start
    public static void case3Rename() {
        Scanner in = new Scanner(System.in);
        System.out.println("You chose: 3) Rename a container\n");
        System.out.println("Choose one of the containers bellow "
               + "to RENAME it.");
        String containerIdRename = DockerInstance.chooseAContainer();
        System.out.print("New name: ");
        String newName = in.nextLine();
        ExecutorThread executorRename = new ExecutorThread(
                containerIdRename, ExecutorThread.TaskType.RENAME, newName);
        executorRename.start();
        System.out.println("Renaming the container...");
        try {
            executorRename.join(); // waiting for the thread to finish
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    } //end case 3: Rename
    public static void case4Remove() {
        System.out.println("You chose: 4) Remove a container\n");
        System.out.println("Choose one of the containers bellow "
               + "to REMOVE it.");
        String containerIdRemove = DockerInstance.chooseAContainer();
        ExecutorThread executorRemove = new ExecutorThread(
                containerIdRemove, ExecutorThread.TaskType.REMOVE);
        executorRemove.start();
        System.out.println("Removing the container...");
        try {
            executorRemove.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    } //end case 4: Remove
    public static void case5Restart() {
        System.out.println("You chose: 5) Restart a container\n");
        if (DockerInstance.noActiveContainers()) {
            System.out.println("There are no active containers.");
            try {
                final int m = 2000;
                Thread.sleep(m);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        } else {
            System.out.println("Choose one of the active containers bellow "
                   + "to RESTART it.");
            String containerIdRestart =
                    DockerInstance.chooseAnActiveContainer();
            ExecutorThread executorRestart = new ExecutorThread(
                containerIdRestart, ExecutorThread.TaskType.RESTART);
            executorRestart.start();
            System.out.println("Restarting the container...");
            try {
                executorRestart.join(); // waiting for the thread to finish
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
                final int m = 2000;
                Thread.sleep(m);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        } else {
            System.out.println("Choose one of the active containers bellow "
                   + "to PAUSE it.");
            String containerIdPause =
                    DockerInstance.chooseAnUnpausedContainer();
            ExecutorThread executorPause = new ExecutorThread(
                    containerIdPause, ExecutorThread.TaskType.PAUSE);
            executorPause.start();
            System.out.println("Pausing the container...");
            try {
                executorPause.join(); // waiting for the thread to finish
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
                final int m = 2000;
                Thread.sleep(m);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        } else {
            System.out.println("Choose one of the paused containers bellow to UNPAUSE it.");
            String containerIdUnpause = DockerInstance.chooseAPausedContainer();
            ExecutorThread executorUnpause = new ExecutorThread(
                    containerIdUnpause, ExecutorThread.TaskType.UNPAUSE);
            executorUnpause.start();
            System.out.println("Unpausing the container...");
            try {
                executorUnpause.join(); // waiting for the thread to finish
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
                final int m = 2000;
                Thread.sleep(m);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        } else {
            System.out.println("Choose one of the active containers bellow "
                   + "to KILL it.");
            String containerIdKill = DockerInstance.chooseAnActiveContainer();
            ExecutorThread executorKill = new ExecutorThread(
                    containerIdKill, ExecutorThread.TaskType.KILL);
            executorKill.start();
            System.out.println("Killing the container...");
            try {
                executorKill.join();
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
    public static void case2Pull() {
        System.out.println("You chose: 2) Pull an image "
                + "from DockerHub\n");
        System.out.print("Type the name of the image you want to pull: ");
        Scanner in = new Scanner(System.in);
        String imageToPull = in.nextLine();
        try{
            PullImageCmd pullImageCmd = ClientUpdater.getUpdatedClient()
                    .pullImageCmd(imageToPull).withTag("latest");
            System.out.println("\nPulling image...");
            System.out.println("This may take a while...");
            pullImageCmd.exec(new PullImageResultCallback() {
                @Override
                public void onNext(PullResponseItem item) {
                    super.onNext(item);
                }
            }).awaitStarted();
            Thread.sleep(60000); //giati pairnei ligo xrono sto docker desktop na fortwsei to neo image
            //Now we have to create a new DockerImage object
            List<Image> images =
                    ClientUpdater.getUpdatedImagesFromClient();
            for (Image i : images) {
                String[] parts = i.getRepoDigests()[0].split("@");
                if (parts[0].equals(imageToPull)) {
                    new DockerImage(i.getRepoDigests()[0],
                            "latest", i.getId());
                }
            }
            System.out.println("Image pulled successfully");
        } catch (Exception e) {
            System.out.println("""
                    Exception due to one of these factors:
                    -No Internet
                    -Not signed in DockerHub
                    -Image does not exist
                    -Image already in your DockerCluster"""); //could be more reasons
        }
    }
    public static void case3Impl() {
        System.out.println("You chose: 3) Implement "
               + "an image(start a new container)\n");
        System.out.println("Choose one of the images below to IMPLEMENT it.");
        String imageIdImplement = DockerImage.chooseAnImage();
        ExecutorThread executorImplementImage = new ExecutorThread(
                imageIdImplement, ExecutorThread.TaskType.IMPLEMENT);
        executorImplementImage.start();
        System.out.println("Creating a new instance of this image...");
        try {
            executorImplementImage.join(); // waiting for the thread to finish
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    } //end Image menu case 2: Implement an image
    public static void case4RmvImg() {
        System.out.println("You chose: 4) Remove "
                + "an image\n");
        System.out.println("Choose one of the images below to REMOVE it.");
        String imageIdRemove = DockerImage.chooseAnImage();
        ExecutorThread executorRemoveImage = new ExecutorThread(imageIdRemove, ExecutorThread.TaskType.REMOVEIMAGE);
        executorRemoveImage.start();
        try {
            executorRemoveImage.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    } //end case 3 Image Menu: Remove an Image.
    public static void goToContTools() {
        System.out.println("You chose: "
               + "3) Container Tools\n");
        System.out.println("Transferring you to the "
               + "Container Tools Menu ...");
    }
    public static void repChoice() {
        System.out.println("Please choose one of the valid options below.\n");
    }
    public static void thr(final String a) throws IllegalStateException {
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
