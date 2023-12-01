package com.nullteam;

public class ExecutorThread extends Thread {
    String id; //container id
    TaskType task; // taskType
    String name2Rename = null; //gia to rename
    public enum TaskType {
        START,
        STOP,
        RENAME,
        RESTART,
        PAUSE,
        UNPAUSE,
        REMOVE,
        KILL,
        INSPECT
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
            case INSPECT:
                inspectContainer();
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
    private void inspectContainer() {
        findContainerInClient().inspectContainer();
    }
}
