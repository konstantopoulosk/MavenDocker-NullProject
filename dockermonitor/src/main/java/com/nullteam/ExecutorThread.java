package com.nullteam;

public class ExecutorThread extends Thread {
    /**
     * id represents container's id.
     */
    private String id; //container id
    /**
     * task field is used in order to,
     * switch between cases in run.
     */
    private TaskType task; // taskType
    /**
     * name2Rename field represents,
     * new Name to rename a container.
     */
    private String name2Rename = null; //gia to rename
    public enum TaskType {
        /**
         * START TaskType is used,
         * to execute the action to start,
         * the container.
         */
        START,
        /**
         * STOP TaskType is used,
         * to execute the action to stop,
         * the container.
         */
        STOP,
        /**
         * RENAME TaskType is used,
         * to execute the action to rename,
         * the container.
         */
        RENAME,
        /**
         * RESTART TaskType is used,
         * to execute the action to restart,
         * the container.
         */
        RESTART,
        /**
         * PAUSE TaskType is used,
         * to execute the action to pause,
         * the container.
         */
        PAUSE,
        /**
         * UNPAUSE TaskType is used,
         * to execute the action to unpause,
         * the container.
         */
        UNPAUSE,
        /**
         * REMOVE TaskType is used,
         * to execute the action to remove,
         * the container.
         */
        REMOVE,
        /**
         * KILL TaskType is used,
         * to execute the action to kill,
         * the container.
         */
        KILL,
        /**
         * IMPLEMENT TaskType is used,
         * to execute the action to implement,
         * the container.
         */
        IMPLEMENT
    }
    //Constructor for RENAME

    /**
     * Constructor is used only when user,
     * wants to rename a container.
     * @param iD
     * @param task1
     * @param nameToRename
     */
    public ExecutorThread(final String iD,
                          final TaskType task1,
                          final String nameToRename) {
        this.id = iD;
        this.task = task1;
        this.name2Rename = nameToRename;
    }

    /**
     * Constructor is used always except,
     * for rename.
     * @param iD
     * @param task1
     */
    public ExecutorThread(final String iD, final TaskType task1) {
        this.id = iD;
        this.task = task1;
    }

    /**
     * Method run is used to execute,
     * the thread based on a specific,
     * task.
     */

    @Override
    public void run() {
        switch (task) {
            case START: //start
                startContainer();
                break;
            case STOP: //stop
                stopContainer();
                break;
            case RENAME: //rename
                renameContainer();
                break;
            case REMOVE:
                removeContainer();
                break;
            case RESTART:
                restartContainer();
                break;
            case PAUSE:
                pauseContainer();
                break;
            case UNPAUSE:
                unpauseContainer();
                break;
            case KILL:
                killContainer();
                break;
            case IMPLEMENT:
                implementImage();
                break;
            default:
                System.out.println("Invalid action type");
        }
    }

    private DockerInstance findContainerInClient() {
        DockerInstance instance = null;
        for (DockerInstance c : DockerInstance.containerslist) {
            if (c.getContainerId().equals(this.id)) {
                instance = c;
            }
        }
        return instance;
    }
    private DockerImage findImageInClient() {
        DockerImage image = null;
        for (DockerImage i : DockerImage.imageslist) {
            if (i.getImageId().equals(this.id)) {
                image = i;
            }
        }
        return image;
    }
    //ALL THE EXECUTION METHODS
    private void startContainer() {
        findContainerInClient().startContainer();
    }
    private void stopContainer() {
        findContainerInClient().stopContainer();
    }
    private void renameContainer() {
        findContainerInClient().renameContainer(this.name2Rename);
    }
    private void removeContainer() {
        findContainerInClient().removeContainer();
    }
    private void restartContainer() {
        findContainerInClient().restartContainer();
    }
    private void pauseContainer() {
        findContainerInClient().pauseContainer();
    }
    private void unpauseContainer() {
        findContainerInClient().unpauseContainer();
    }
    private void killContainer() {
        findContainerInClient().killContainer();
    }
    private void implementImage() {
        findImageInClient().implementImage();
    }
}
