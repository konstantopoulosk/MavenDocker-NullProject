package com.nullteam;

import com.github.dockerjava.api.model.Container;

import java.util.List;
import java.util.concurrent.BlockingQueue;

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
                System.out.println("Executor is running ...");
                // Dequeue the ActionRequest from the actionQueue
                ActionRequest actionRequest = actionQueue.take();
                // Extract the actionType and containerId from the ActionRequest
                String actionType = actionRequest.getActionType();
                String containerId = actionRequest.getContainerId();
                if(!actionRequest.getNewName().isEmpty()) { //checking if there is a new name, so we use rename instead
                    System.out.println("Rename Container: " + containerId);
                    renameContainer(containerId, actionRequest.getNewName());
                } else {
                    System.out.println("Everything Else But Rename.");
                    performDockerAction(actionType, containerId);
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                System.out.println(e.getMessage());
            }
        }
    }

    /**
     * This method performs an action based on an actionType
     * (Start, Stop, Rename...) and containerId or imageId.
     * @param actionType String
     * @param containerId String
     */
    private void performDockerAction(String actionType, String containerId) {
        System.out.println("Performing action: " + actionType + " on container: " + containerId);
        switch (actionType) {
            case "START": //start
                startContainer(containerId);
                System.out.println("Started container: " + containerId );
                break;
            case "STOP": //stop
                stopContainer(containerId);
                System.out.println("Stopped Container: " + containerId);
                break;
            case "RENAME":
                renameContainer(containerId,name2Rename);
                System.out.println("Renamed Container: " + containerId);
            case "REMOVE":
                removeContainer(containerId);
                System.out.println("Removed Container: " + containerId);
                break;
            case "RESTART":
                restartContainer(containerId);
                System.out.println("Restarted Container: " + containerId);
                break;
            case "PAUSE":
                pauseContainer(containerId);
                System.out.println("Paused Container: " + containerId);
                break;
            case "UNPAUSE":
                unpauseContainer(containerId);
                System.out.println("Unpaused Container: " + containerId);
                break;
            case "KILL":
                killContainer(containerId);
                System.out.println("Killed Container: " + containerId);
                break;
            case "IMPLEMENT":
                implementImage(containerId); //This is of course image id
                System.out.println("Implemented Image: " + containerId);
                break;
            case "REMOVEIMAGE":
                removeImage(containerId); //Also image id
                System.out.println("Removed Image: " + containerId);
                break;
            default:
                System.out.println("Invalid action type");
        }
    }
    /**
     * This method finds a Container in the DockerClient its id
     * and returns the DockerInstance object that represents it.
     * @param id String
     * @return DockerInstance
     */
     public static DockerInstance findContainerInClient(String id) {
        DockerInstance instance = null;
        for (DockerInstance c : DockerInstance.containerslist) {
            if (c.getContainerId().equals(id)) {
                instance = c;
            }
        }
        System.out.println(instance); //This somehow is null :(
        return instance;
    }
    /**
     * This method finds a Image in the DockerClient its id
     * and returns the DockerImage object that represents it.
     * @param id String
     * @return DockerImage
     */
    public static DockerImage findImageInClient(String id) {
        DockerImage image = null;
        for (DockerImage i : DockerImage.imageslist) {
            if (i.getImageId().equals(id)) {
                image = i;
            }
        }
        return image;
    }
    //ALL THE EXECUTION METHODS
    private void startContainer(String id) {
        findContainerInClient(id).startContainer();
    }
    private void stopContainer(String id) {
        findContainerInClient(id).stopContainer();
    }
    private void renameContainer(String id, String newName) {
        findContainerInClient(id).renameContainer(newName);
    }
    private void removeContainer(String id) {
        findContainerInClient(id).removeContainer();
    }
    private void restartContainer(String id) {
        findContainerInClient(id).restartContainer();
    }
    private void pauseContainer(String id) {
        System.out.println("Paused: " + id);
        findContainerInClient(id).pauseContainer();
    }
    private void unpauseContainer(String id) {
        findContainerInClient(id).unpauseContainer();
    }
    private void killContainer(String id) {
        findContainerInClient(id).killContainer();
    }
    private void implementImage(String id) {
        String containerId = findImageInClient(id).implementImage();
        startContainer(containerId);
    }
    private void removeImage(String id) {
        List<Container> containers = findImageInClient(id).findContainers();
        if (containers != null) {
            for (Container c : containers) {
                if (c.getStatus().startsWith("Up")) {
                    stopContainer(c.getId());
                }
                removeContainer(c.getId());
            }
        }
        findImageInClient(id).removeImage();
    }
}
