package com.nullproject;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.model.Container;
import com.github.dockerjava.core.DefaultDockerClientConfig;
import com.github.dockerjava.core.DockerClientBuilder;
import java.util.List;

public class Main {
    public static void main(String[] args) throws InterruptedException {
        DefaultDockerClientConfig.Builder builder= DefaultDockerClientConfig.createDefaultConfigBuilder();
        builder.withDockerHost("tcp://localhost:2375");
        DockerClient dockerClient = DockerClientBuilder.getInstance(builder).build();
        dockerClient.versionCmd().exec();
        List<Container> containers;
        System.out.println("-----------------ALL CONTAINER INSTANCES-----------------");
        containers = dockerClient.listContainersCmd().withShowAll(true).exec();
        containers.forEach(c -> System.out.println(c.getId() + " " + c.getState()));
        String id = containers.get(0).getId();
        dockerClient.stopContainerCmd(id).exec();
        Thread.sleep(1000);
        System.out.println("------------------------- ACTIVE CONTAINER INSTANCES -----------------------");
        containers = dockerClient.listContainersCmd().withShowAll(false).exec();
        containers.forEach(c -> System.out.println(c.getId() + " " + c.getState()));
        System.out.println("--------------------------- ALL CONTAINER INSTANCES----------------------------");
        containers = dockerClient.listContainersCmd().withShowAll(true).exec();
        containers.forEach(c -> System.out.println(c.getId() + " " + c.getState()));
        dockerClient.startContainerCmd(id).exec();
        System.out.println("-----------------------------------------------");
        containers = dockerClient.listContainersCmd().withShowAll(false).exec();
        containers.forEach(c -> System.out.println(c.getId() + " " + c.getState()));
    }
}