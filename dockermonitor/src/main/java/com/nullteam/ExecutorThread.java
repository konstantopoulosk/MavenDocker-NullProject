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
    String name2Rename; //gia to rename
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
            case RENAME: //rename
                renameContainer(id, dockerClient);
                break;
            case REMOVE:
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

    private void renameContainer(String containerId, DockerClient m) {
        RenameContainerCmd renameContainerCmd = m.renameContainerCmd(containerId).withName(this.name2Rename);
        renameContainerCmd.exec();
    }

    private void removeContainer(String containerId, DockerClient m) {
        RemoveContainerCmd removeContainerCmd = m.removeContainerCmd(containerId).withForce(true);
        removeContainerCmd.exec();
    }

    private void restartContainer(String containerId, DockerClient m) {
        RestartContainerCmd restartContainerCmd = m.restartContainerCmd(containerId);
        restartContainerCmd.exec();
    }

    private void pauseContainer(String containerId, DockerClient m) {
        PauseContainerCmd pauseContainerCmd = m.pauseContainerCmd(containerId);
        pauseContainerCmd.exec();
    }

    private void unpauseContainer(String containerId, DockerClient m) {
        UnpauseContainerCmd unpauseContainerCmd = m.unpauseContainerCmd(containerId);
        unpauseContainerCmd.exec();
    }

    private void killContainer(String containerId, DockerClient m) {
        KillContainerCmd killContainerCmd = m.killContainerCmd(containerId);
        killContainerCmd.exec();
    }
/*
    private void inspectContainer(String containerId, DockerClient m) {
//        final ContainerInfo info = m.inspectContainer(containerId);
        InspectContainerCmd containerResponse = m.inspectContainerCmd(containerId);
        System.out.println(containerResponse);
    }

 */


}
