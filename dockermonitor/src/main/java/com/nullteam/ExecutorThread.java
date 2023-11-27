package com.nullteam;

import java.util.Scanner;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.model.Container;
import com.github.dockerjava.api.command.*;
import com.github.dockerjava.core.DefaultDockerClientConfig;
import com.github.dockerjava.core.DockerClientBuilder;

public class ExecutorThread extends Thread {
    final private Scanner in = new Scanner(System.in); //xreiazetai gia to rename
    String id; //container id
    TaskType task; // taskType
    public enum TaskType {
        START,
        STOP,
        RENAME,
        RESTART,
        PAUSE,
        UNPAUSE,
        REMOVE,
        KILL
    }

    public ExecutorThread(String id, TaskType task) {
        this.id = id;
        this.task = task;
    }

    @Override
    public void run() { //TOADD STRING ID KAI STRING TASK
        DefaultDockerClientConfig.Builder builder = DefaultDockerClientConfig.createDefaultConfigBuilder();
        builder.withDockerHost("tcp://localhost:2375");
        DockerClient dockerClient = DockerClientBuilder.getInstance(builder).build();
        dockerClient.versionCmd().exec();
        switch (task) {
            case START: //start
                startContainer(id);
                break;
            case STOP: //stop
                stopContainer(id);
                break;
            default:
                System.out.println("Invalid action type");
        }
    }

    //ALL THE EXECUTION METHODS
    private void startContainer(String containerId) {
        for (DockerInstance c : DockerInstance.containerslist) {
            if (c.getContainerId().equals(containerId)) {
                DockerInstance.containerslist.get(DockerInstance.containerslist.indexOf(c)).startContainer();
            }
        }
    }

    private void stopContainer(String containerId) {
        for (DockerInstance c : DockerInstance.containerslist) {
            if (c.getContainerId().equals(containerId)) {
                DockerInstance.containerslist.get(DockerInstance.containerslist.indexOf(c)).stopContainer();
            }
        }
    }
}
    /*
    private void renameContainer(String containerId, String newName) {
        RenameContainerCmd renameContainerCmd = m.getDockerClient().renameContainerCmd(containerId).withName(newName);
        renameContainerCmd.exec();
    }
    private void removeContainer(String containerId) {
        RemoveContainerCmd removeContainerCmd = m.getDockerClient().removeContainerCmd(containerId).withForce(true);
        removeContainerCmd.exec();
    }

    private void restartContainer(String containerId) {
        RestartContainerCmd restartContainerCmd = m.getDockerClient().restartContainerCmd(containerId);
        restartContainerCmd.exec();
    }

    private void pauseContainer(String containerId) {
        PauseContainerCmd pauseContainerCmd = m.getDockerClient().pauseContainerCmd(containerId);
        pauseContainerCmd.exec();
    }

    private void unpauseContainer(String containerId) {
        UnpauseContainerCmd unpauseContainerCmd = m.getDockerClient().unpauseContainerCmd(containerId);
        unpauseContainerCmd.exec();
    }

    private void killContainer(String containerId) {
        KillContainerCmd killContainerCmd = m.getDockerClient().killContainerCmd(containerId);
        killContainerCmd.exec();
    }
    case RENAME: //rename
                System.out.print("Give the new name :");
                renameContainer(id, in.nextLine());
                break;
            case REMOVE:
                removeContainer(id);
                break;
            case RESTART:
                restartContainer(id);
                break;
            case PAUSE:
                pauseContainer(id);
                break;
            case UNPAUSE:
                unpauseContainer(id);
                break;
            case KILL:
                killContainer(id);
                break;
            // Add more
     */
