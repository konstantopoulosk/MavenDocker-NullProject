package com.nullteam;

import com.github.dockerjava.api.command.InspectVolumeResponse;
import com.github.dockerjava.api.command.PullImageCmd;
import com.github.dockerjava.api.command.PullImageResultCallback;
import com.github.dockerjava.api.model.Container;
import com.github.dockerjava.api.model.Image;
import com.github.dockerjava.api.model.Network;
import com.github.dockerjava.api.command.LogContainerCmd;
import com.github.dockerjava.api.model.Frame;
import com.github.dockerjava.api.model.PullResponseItem;

import java.io.IOException;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.Executor;

final class GetHelp {
    private GetHelp() {
    }
    public static void listImage() {
        List<Image> images =
                ClientUpdater.getUpdatedImagesFromClient();
        //Updated Client from ClientUpdater
        for (Image i : images) { //For every one object in the images list
                                 //creating an object DockerImage
            new DockerImage(i.getRepoDigests()[0],
                    "latest", i.getId());
        }
    }
    public static void listContainers() {
        List<Container> containers =
                ClientUpdater.getUpdatedContainersFromClient();
        //updated list with containers
        for (Container c : containers) {
            //for every one object in the containers list
            //create an object DockerInstance
            new DockerInstance(c.getNames()[0], c.getId(),
                    c.getImage(), c.getStatus());
        }
    }
    public static void listVolumes() {
        List<InspectVolumeResponse> volumes =
                ClientUpdater.getUpdatedVolumesFromClient();
        //Updated Client from ClientUpdater
        for (InspectVolumeResponse v : volumes) {
            //creating an object DockerVolume
            //we need to get the CreatedAt from inspect
            new DockerVolume(v.getDriver(), v.getName(),
                    DockerVolume.createdAt(v.getName()), v.getMountpoint());
        }
    }
    public static void listNetworks() {
        List<Network> networks =
                ClientUpdater.getUpdatedNetworksFromClient();
        //Updated Client from ClientUpdater
        for (Network n : networks) {
            //creating an object DockerNetwork
            new DockerNetwork(n.getId(), n.getName(),
                    n.getDriver(), n.getScope());
        }
    }

    public static void case1Stop() {
        System.out.println("You chose: (1) Stop a container\n");
        if (DockerInstance.noActiveContainers()) {
            //there is no reason to continue without active containers
            System.out.println("There are no active containers.");
            wait2seconds();
        } else {
            System.out.println("Choose one of the active containers bellow "
                   + "to STOP it.");
            String containerIdStop = DockerInstance.chooseAnActiveContainer();
            ExecutorThread executorStop = new ExecutorThread(
                    containerIdStop, ExecutorThread.TaskType.STOP);
            executorStop.start();
            System.out.println("Stopping the container...");
            avoidInterruption(executorStop);
        }
    } //end case 1: Stop
    public static void case2Start() {
        System.out.println("You chose: (2) Start a container\n");
        System.out.println("Choose one of the exited containers bellow "
               + "to START it.");
        String containerIdStart = DockerInstance.chooseAStoppedContainer();
        ExecutorThread executorStart = new ExecutorThread(
                containerIdStart, ExecutorThread.TaskType.START);
        executorStart.start();
        System.out.println("Starting the container...");
        avoidInterruption(executorStart);
    } //end case 2: Start
    public static void case3Rename() {
        Scanner in = new Scanner(System.in);
        System.out.println("You chose: (3) Rename a container\n");
        System.out.println("Choose one of the containers bellow "
               + "to RENAME it.");
        String containerIdRename = DockerInstance.chooseAContainer();
        System.out.print("New name: ");
        String newName = in.nextLine();
        ExecutorThread executorRename = new ExecutorThread(
                containerIdRename, ExecutorThread.TaskType.RENAME, newName);
        executorRename.start();
        System.out.println("Renaming the container...");
        avoidInterruption(executorRename);
    } //end case 3: Rename
    public static void case4Remove() {
        System.out.println("You chose: (4) Remove a container\n");
        System.out.println("Choose one of the containers bellow "
               + "to REMOVE it.");
        String containerIdRemove = DockerInstance.chooseAStoppedContainer();
        //only from exited because we can't remove an active container
        ExecutorThread executorRemove = new ExecutorThread(
                containerIdRemove, ExecutorThread.TaskType.REMOVE);
        executorRemove.start();
        System.out.println("Removing the container...");
        avoidInterruption(executorRemove);
    } //end case 4: Remove
    public static void case5Restart() {
        System.out.println("You chose: (5) Restart a container\n");
        if (DockerInstance.noActiveContainers()) {
            System.out.println("There are no active containers.");
            wait2seconds();
        } else {
            System.out.println("Choose one of the active containers bellow "
                   + "to RESTART it.");
            String containerIdRestart =
                    DockerInstance.chooseAnActiveContainer();
            ExecutorThread executorRestart = new ExecutorThread(
                containerIdRestart, ExecutorThread.TaskType.RESTART);
            executorRestart.start();
            System.out.println("Restarting the container...");
            avoidInterruption(executorRestart);
        }
    } //end case 5: Restart
    public static void case6Pause() {
        System.out.println("You chose: (6) Pause a container\n");
        if (DockerInstance.noUnpausedContainers()) {
            System.out.println("There are no active unpaused containers.");
            wait2seconds();
        } else {
            System.out.println("Choose one of the active containers bellow "
                   + "to PAUSE it.");
            String containerIdPause =
                    DockerInstance.chooseAnUnpausedContainer();
            ExecutorThread executorPause = new ExecutorThread(
                    containerIdPause, ExecutorThread.TaskType.PAUSE);
            executorPause.start();
            System.out.println("Pausing the container...");
            avoidInterruption(executorPause);
        }
    } //end case 6: Pause
    public static void case7Unpause() {
        System.out.println("You chose: (7) Unpause a container\n");
        if (DockerInstance.noPausedContainers()) {
            System.out.println("There are no paused containers.");
            wait2seconds();
        } else {
            System.out.println("Choose one of the paused containers bellow to UNPAUSE it.");
            String containerIdUnpause = DockerInstance.chooseAPausedContainer();
            ExecutorThread executorUnpause = new ExecutorThread(
                    containerIdUnpause, ExecutorThread.TaskType.UNPAUSE);
            executorUnpause.start();
            System.out.println("Unpausing the container...");
            avoidInterruption(executorUnpause);
        }
    } //end case 7: Unpause
    public static void case8Kill() {
        System.out.println("You chose: (8) Kill a container\n");
        if (DockerInstance.noActiveContainers()) {
            System.out.println("There are no active containers.");
            wait2seconds();
        } else {
            System.out.println("Choose one of the active containers bellow "
                   + "to KILL it.");
            String containerIdKill = DockerInstance.chooseAnActiveContainer();
            ExecutorThread executorKill = new ExecutorThread(
                    containerIdKill, ExecutorThread.TaskType.KILL);
            executorKill.start();
            System.out.println("Killing the container...");
            avoidInterruption(executorKill);
        }
    } //end case 8: Kill
    public static void case0Subnets() {
        if (DockerInstance.noActiveContainers()) {
            System.out.println("There are no active containers.\n"
                    + "This means that no container is connected to a network.");
            wait2seconds();
        } else {
            System.out.println("\nWhich container's subnets would you like to see?");
            //only running containers are connected to a subnet
            //therefore we show the user the active container list
            StringBuilder d = DockerNetwork.formatSubnetsSettings(
                    DockerNetwork.inspectContainersForSubnet(
                            DockerInstance.chooseAnActiveContainer()));
            System.out.println(d);
        }
    }

    public static void case9ViewLogs() {
        if (DockerInstance.noActiveContainers()) {
            System.out.println("There are no active containers.\n"
                    + "Logs are available only for active containers.");
            wait2seconds();
        } else {
            System.out.println("\nWhich container's logs would you like to view?");
            String containerId = DockerInstance.chooseAnActiveContainer();
            DockerInstance.showContainerLogs(containerId);
        }
    }

    /**
     * This method is used to wait for an object
     * ExecutorThread, which is a thread, to finish.
     * @param et ExecutorThread
     */
    public static void avoidInterruption(ExecutorThread et){
        try {
            et.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * This method pauses the programm
     * for 2 seconds.
     */
    public static void wait2seconds() {
        try {
            final int m = 2000;
            Thread.sleep(m);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
    public static void goToContMenu() {
        System.out.println("You chose: (..) "
               + "Go back to Container Menu\n");
        System.out.println("Transferring you "
               + "to the Container Menu ...");
    }
    public static void goToMainMenu() {
        System.out.println("You chose: (..) "
               + "Go Back(Main Menu)\n");
        System.out.println("Transferring you "
               + "to the Main Menu ...");
    }
    public static void imageCase1() {
        System.out.println("You chose: (1) View available images\n");
        DockerImage.listAllImages();
    }
    public static void imageCase2() {
        System.out.println("You chose: (2) View images in use\n");
        DockerImage.listUsedImages();
    }
    /**
     * This method pulls an image from DockerHub
     * and adds it to the imagelist
     */
    public static void case3Pull() {
        System.out.println("You chose: (3) Pull an image "
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
    public static void case4Impl() {
        System.out.println("You chose: (4) Implement "
               + "an image(start a new container)\n");
        System.out.println("Choose one of the images below to IMPLEMENT it.");
        String imageIdImplement = DockerImage.chooseAnImage();
        ExecutorThread executorImplementImage = new ExecutorThread(
                imageIdImplement, ExecutorThread.TaskType.IMPLEMENT);
        executorImplementImage.start();
        System.out.println("Creating a new instance of this image...");
        avoidInterruption(executorImplementImage);
    } //end Image menu case 2: Implement an image
    public static void case5RmvImg() {
        System.out.println("You chose: (5) Remove "
                + "an image\n");
        System.out.println("Choose one of the images below to REMOVE it.");
        String imageIdRemove = DockerImage.chooseAnImage();
        System.out.println("If you remove this image, its instances will be removed with it" +
                "\nAre you sure you want to remove it? Answer Y or N (Yes/No)");
        System.out.print("\nAnswer: ");
        Scanner in = new Scanner(System.in);
        String ans = in.nextLine();
        if (ans.equals("Y")) {
            ExecutorThread executorRemoveImage =
                    new ExecutorThread(imageIdRemove,
                            ExecutorThread.TaskType.REMOVEIMAGE);
            executorRemoveImage.start();
            System.out.println("Removing image and image's instances...");
            avoidInterruption(executorRemoveImage);
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
}
