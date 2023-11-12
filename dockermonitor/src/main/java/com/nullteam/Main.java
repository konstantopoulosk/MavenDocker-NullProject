package com.nullproject;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.model.Container;
import com.github.dockerjava.core.DefaultDockerClientConfig;
import com.github.dockerjava.core.DockerClientBuilder;
import java.util.List;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) throws InterruptedException {

        DefaultDockerClientConfig.Builder builder= DefaultDockerClientConfig.createDefaultConfigBuilder();
        builder.withDockerHost("tcp://localhost:2375");
        DockerClient dockerClient = DockerClientBuilder.getInstance(builder).build();
        dockerClient.versionCmd().exec();
        List<Container> containers;
        containers = dockerClient.listContainersCmd().withShowAll(true).exec();

        // Initialized menu//
        System.out.println("Welcome nullteam, bootup menu *v.1* starting...");
        Scanner in = new Scanner(System.in);
        for(;;) {
            System.out.println("Choose one:\n1)Show ALL containers\n2)Show ACTIVE containers only\n3)Stop a container\n4)EXIT APP");
            int ans = in.nextInt();
            switch(ans) {
                case 1:
                    System.out.println("--------------------------ALL CONTAINER INSTANCES--------------------------");
                    containers = dockerClient.listContainersCmd().withShowAll(true).exec();
                    containers.forEach(c -> System.out.println(c.getId() + " " + c.getState()));
                    break;
                case 2:
                    System.out.println("------------------------- ACTIVE CONTAINER INSTANCES -----------------------");
                    containers = dockerClient.listContainersCmd().withShowAll(false).exec();
                    containers.forEach(c -> System.out.println(c.getId() + " " + c.getState()));
                    break;
                case 3:
                    String id = containers.get(0).getId();
                    dockerClient.stopContainerCmd(id).exec(); //STOPS CONTAINER #1
                    Thread.sleep(1000); 
                    break;
                case 4:
                    System.exit(0);
                    break;
            }
        }  
    }
}