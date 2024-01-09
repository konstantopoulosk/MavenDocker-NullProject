package com.nullteam;

import java.util.concurrent.BlockingQueue;

import com.google.gson.Gson;

public class ExecutorThread extends Thread {

    private final BlockingQueue<ActionRequest> actionQueue;

    public ExecutorThread(BlockingQueue<ActionRequest> actionQueue) {
        this.actionQueue= actionQueue;
    }
    /**
     * id represents container's id.
     */
    private String name2Rename = null; //gia to rename
    /**
     * Method run is used to execute,
     * the thread based on a specific,
     * task.
     */

    @Override
    public void run() {
        while (true) {
            try {
                //System.out.println("Executor is running..");
                // Dequeue the ActionRequest from the actionQueue
                ActionRequest actionRequest = actionQueue.take();

                // Extract the actionType and containerId from the ActionRequest
                String actionType = actionRequest.getActionType();
                String containerId = actionRequest.getContainerId();

                // Perform the corresponding Docker action based on actionType and containerId
                performDockerAction(actionType, containerId);

            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }
    private void performDockerAction(String actionType, String containerId) {
        // Logic to perform the Docker action based on actionType and containerId
        System.out.println("Performing action: " + actionType + " on container: " + containerId);
        switch (actionType) {
            case "START": //start
                startContainer(containerId);
                System.out.println("Started container: " + containerId );
                break;
            case "STOP": //stop
                stopContainer(containerId);
                break;
            case "RENAME": //rename
                renameContainer(containerId);
                break;
            case "REMOVE":
                removeContainer(containerId);
                break;
            case "RESTART":
                restartContainer(containerId);
                break;
            case "PAUSE":
                pauseContainer(containerId);
                break;
            case "UNPAUSE":
                unpauseContainer(containerId);
                break;
            case "KILL":
                killContainer(containerId);
                break;
            /*
            case "IMPLEMENT":
                implementImage(containerId);
                break;
            case "REMOVEIMAGE":
                removeImage(containerId);
                break;
             */
            default:
                System.out.println("Invalid action type");
        }
    }

    private DockerInstance findContainerInClient(String id) {
        DockerInstance instance = null;
        for (DockerInstance c : DockerInstance.containerslist) {
            if (c.getContainerId().equals(id)) {
                instance = c;
            }
        }
        System.out.println(instance); //This somehow is null :(
        return instance;
    }
    /*
    private DockerImage findImageInClient() {
        DockerImage image = null;
        for (DockerImage i : DockerImage.imageslist) {
            if (i.getImageId().equals(this.id)) {
                image = i;
            }
        }
        return image;
    }
     */
    //ALL THE EXECUTION METHODS
    private void startContainer(String id) {
        findContainerInClient(id).startContainer();
    }
    private void stopContainer(String id) {
        findContainerInClient(id).stopContainer();
    }
    private void renameContainer(String id) {
        findContainerInClient(id).renameContainer(this.name2Rename);
    }
    private void removeContainer(String id) {
        findContainerInClient(id).removeContainer();
    }
    private void restartContainer(String id) {
        findContainerInClient(id).restartContainer();
    }
    private void pauseContainer(String id) {
        findContainerInClient(id).pauseContainer();
    }
    private void unpauseContainer(String id) {
        findContainerInClient(id).unpauseContainer();
    }
    private void killContainer(String id) {
        findContainerInClient(id).killContainer();
    }
    /*
    private void implementImage() {
        findImageInClient().implementImage();
    }
    private void removeImage() {
        findImageInClient().removeImage();
    }
     */
}
