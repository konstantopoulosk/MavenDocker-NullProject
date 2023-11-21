//package com.nullproject;

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
        List<Image> images = dockerClient.listImagesCmd().exec();                 
        List<Container> containers = dockerClient.listContainersCmd().withShowAll(true).exec();

        // Initialized menu//
        System.out.println("Welcome null_team, boot-up menu *v.1* starting...");
        Scanner in = new Scanner(System.in);
        
        Modelisation mdl = new Modelisation(dockerClient, images, containers);        //create 2 objects from class Modelisation
       // ExecutorThread ex = new ExecutorThread<>(dockerClient, images, containers); //object to call method run

        do{
            System.out.print("Open Images menu(I) or Containers menu(C) or Exit the app(E): "); //exception if not I or C or E
            String menu = in.nextLine();
            if (menu == "I") {
                for(;;) {
                    System.out.println("Choose one:\n1)Show ALL images\n2)EXIT this menu"); //na baloume kialles epiloges opws inspect remove ktl
                    int ansI= in.nextInt();
                    switch (ansI) {
                        case 1: 
                            mdl.showImages();
                            break;                
                        case 2:
                            break;
                    }
                }   
            } else if (menu == "C"){
                for(;;) {
                    System.out.println("Choose one:\n1)Show ALL containers\n2)Show ACTIVE containers only\n3)EXIT this menu");
                    int ansC= in.nextInt();
                    String[] s = new String[2];
                    switch(ansC) {
                        case 1:
                            s = mdl.allContainers();
                            break;
                        case 2:
                            s = mdl.activeContainers();
                            break;
                        case 3:
                            break;
                    }
                    if (s[0] != null) {
                        ExecutorThread ex = new ExecutorThread<>(mdl, s[0], s[1]);
                        ex.run();
                    }
                }
            } else {
                System.out.println("Exit");
            }
        } while(ans != "E");
    }
}
