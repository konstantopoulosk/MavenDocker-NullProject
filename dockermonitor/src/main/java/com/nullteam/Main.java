package com.nullteam;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.model.Container;
import com.github.dockerjava.core.DefaultDockerClientConfig;
import com.github.dockerjava.core.DockerClientBuilder;
import java.io.IOException;
import java.util.List;
import java.util.Scanner;


public class Main {
    public static void main(String[] args) throws InterruptedException {

        //Creating instances of DockerInstance and DockerImage using info from the DockerClient
        DefaultDockerClientConfig.Builder builder= DefaultDockerClientConfig.createDefaultConfigBuilder();
        builder.withDockerHost("tcp://localhost:2375");
        DockerClient dockerClient = DockerClientBuilder.getInstance(builder).build();
        dockerClient.versionCmd().exec();
        List<Container> containers; // instances
        containers = dockerClient.listContainersCmd().withShowAll(true).exec();
        for (Container c : containers) {
            new DockerInstance(c.getNames()[0], c.getId(), new DockerImage
                    (c.getImageId(), c.getImage(),"latest"), c.getStatus());
        }
        //Initialized the monitor thread
        DockerMonitor monitor = new DockerMonitor();
        monitor.start();
        //Initialized menu//
        System.out.println("Welcome null_team, boot-up menu *v.1* starting...");
        Scanner in = new Scanner(System.in);
        for(;;) {
            System.out.println("Choose one:\n1)Show ALL containers\n2)Show ACTIVE containers only" +
                    "\n3)Stop a container\n4)Start a container\n5)EXIT APP");
            int ans = in.nextInt();
            switch(ans) {
                case 1:
                    System.out.println("--------------------------ALL CONTAINER INSTANCES--------------------------");
                    DockerInstance.listAllContainers();
                    
                    break;
                case 2:
                    System.out.println("------------------------- ACTIVE CONTAINER INSTANCES -----------------------");
                    DockerInstance.listActiveContainers();
                    break;
                case 3:
                    String containeridStop = DockerInstance.chooseAnActiveContainer();
                    ExecutorThread executor_stop = new ExecutorThread
                            (containeridStop, ExecutorThread.TaskType.STOP);
                    executor_stop.start();
                    break;
                case 4:
                    String containeridStart = DockerInstance.chooseAStoppedContainer();
                    ExecutorThread executor_start = new ExecutorThread
                            (containeridStart, ExecutorThread.TaskType.START);
                    executor_start.start();
                    break;
                case 5:
                    try {
                        dockerClient.close();
                    } catch(IOException e) {
                        e.printStackTrace();
                    }
                    System.exit(0);
                    break;
                default:
                    throw new IllegalStateException("Unexpected value: " + ans);
            }
        } 
    }
}