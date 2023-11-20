package com.nullteamproject;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.command.listImagesCmd;
import com.github.dockerjava.api.model.Container;
import com.github.dockerjava.api.model.Image;
import com.github.dockerjava.core.DockerClientBuilder;
import java.util.List;
import java.util.Scanner;

public class Modelisation {
    private DockerClient dockerClient;
    List<Image> images;
    List<java.awt.Container> containers;

    //constructor for image methods
    Modelisation(DockerClient dockerClient, List<Image> images) {
        this.dockerClient = dockerClient;
        this.images = images;
    }

    //constructor for container methods
    Modelisation(DockerClient dockerClient, List<Container> containers) {
        this.dockerClient = dockerClient;
        this.containers = containers;
    }

    //Images case 1 (Show all images like this *number.image:version*)
    public void showImages() {
        for (int i = 0; i < images.size(); i++) {
            String [] s = new String[2];
            s = images.get(i).toString().split(" ", 2 );
            System.out.println( (i+1) + ". " + s[0] + ":" + s[1]);  // It works!
        }
    }

    //Containers case 1 (Show all containers)
    public void allContainers() {
        System.out.println("--------------------------ALL CONTAINER INSTANCES--------------------------");
        containers = dockerClient.listContainersCmd().withShowAll(true).exec();

        showContainers(containers);

        System.out.println("Choose a container from the above (number) and an activity:");
        System.out.println("Start(S)\nRename(R)\nInspect(I)");
        System.out.println("Or press 0 to go back");
        int c = in.nextInt();
        if (c != 0) {
            String id = containers.get(c-1).getId(); //container's id, parameter in executor
            char a = in.next().charAt();
            switch (a) {
                case 'S':
                    //executor
                    break;
                case 'R':
                    //executor
                    break;
                case 'I':
                    //executor
                    break;
            }
        }
    }

    public void showContainers(List<java.awt.Container> containers) {
        for (int i = 0; i < containers.size(); i++) {
            String [] s = new String[7];
            s = containers.get(i).toString().split(" ", 7);
            System.out.println((i+1) + ". " + s[6] + ", Image: " + s[1]);
        }
    }

    //Containers case 2 (Show active containers)
    public void activeContainers() {
        System.out.println("--------------------------ACTIVE CONTAINER INSTANCES------------------------");
        containers = dockerClient.listContainersCmd().withShowAll(false).exec();

        showContainers(containers);

        System.out.println("Choose a container from the above (number) and an activity:");
        System.out.println("Restart(R)\nPause(P)\nUnpause(U)\nStop(S)\nKill(K)");
        System.out.println("Or press 0 to go back");
        int c = in.nextInt();
        if (c != 0) {
            char a = in.next().charAt();
            String id = containers.get(c-1).getId(); //container's id, parameter in executor
            switch (a) {
                case 'R':
                    //executor
                    break;
                case 'P':
                    //executor
                    break;
                case 'U':
                    //executor
                    break;
                case 'S':
                    //executor
                    break;
                case 'K':
                    //executor
                    break;                    
            }
        }        
    }
}
