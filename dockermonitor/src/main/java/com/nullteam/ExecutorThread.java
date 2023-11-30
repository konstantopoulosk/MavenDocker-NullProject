package com.nullteam;

public class ExecutorThread extends Thread {
    String id; //container id
    TaskType task; // taskType
    String name2Rename = null; //gia to rename
    public enum TaskType {
        START,
        STOP,
        RENAME,
        /*
        RESTART,
        PAUSE,
        UNPAUSE,
        REMOVE,
        KILL,
        INSPECT
         */
    }

    public ExecutorThread(String id, TaskType task, String name2Rename) {
        this.id = id;
        this.task = task;
        this.name2Rename = name2Rename;
    }
    public ExecutorThread(String id, TaskType task) {
        this.id = id;
        this.task = task;
    }

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
            /*
            /*case REMOVE:
                removeContainer(id, dockerClient);
                break;
            case RESTART:
                restartContainer(id, dockerClient);
                break;
            case PAUSE:
                pauseContainer(id, dockerClient);
                break;
            case UNPAUSE:
               unpauseContainer(id, dockerClient);
                break;
            case KILL:
                killContainer(id, dockerClient);
                break;
            case INSPECT:
                //inspect a container
                break;
             */
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
    /*
    !!!KAPOIOS NA ANALABEI NA YLOPOIHSEI TIS PARAKATW ME PAROMOIO TROPO OPWS EKANA TA PANW!!!
    private void removeContainer() {
    }

    private void restartContainer() {
    }

    private void pauseContainer() {
    }

    private void unpauseContainer() {
    }

    private void killContainer() {
    }

    private void inspectContainer() {
    }

 */


}
