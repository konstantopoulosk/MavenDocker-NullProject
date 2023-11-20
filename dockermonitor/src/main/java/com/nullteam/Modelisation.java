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
    List<java.awt.Image> images;
    List<java.awt.Container> containers;

    //constructor for image methods
    Modelisation(DockerClient dockerClient, List<java.awt.Image> images) {
        this.dockerClient = dockerClient;
        this.images = images;
    }

    //constructor for container methods
    Modelisation(DockerClient dockerClient, List<java.awt.Container> containers) {
        this.dockerClient = dockerClient;
        this.containers = containers;
    }

    public DockerClient getDockerClient() {
        return this.dockerClient;
    }

    public List<java.awt.Image> getImages() {
        return this.images;
    }

    public List<java.awt.Container> getContainers() {
        return this.containers;
    }

    public void setContainers(List<java.awt.Container> containers) {
        this.containers = containers;
    }

    //WARNING! MPOROUME NA VALOYME METHODS GETID PX AN XRHSIMOPOIOUNTAI POLLES FORES STON KWDIKA. AYTH H KLASH EINAI SAN VASH MPOROUME NA PATAME PANW THS!

    //Images case 1 (Show all images like this *number.image:version*)
    public void showImages() {
        for (int i = 0; i < images.size(); i++) {
            String [] s = new String[2];
            s = images.get(i).toString().split(" ", 2 );
            System.out.println( (i+1) + ". " + s[0] + ":" + s[1]);  // It works!
        }
    }

    //this method shows the user's containers like this: <number>. <name>, Image: <image>
    public void showContainers(List<java.awt.Container> containers) {
        for (int i = 0; i < containers.size(); i++) {
            String [] s = new String[7];
            s = containers.get(i).toString().split(" ", 7);
            System.out.println((i+1) + ". " + s[6] + ", Image: " + s[1]);
        }
    }


    //Containers case 1 (Show all containers)
    public String[] allContainers() {
	Scanner in = new Scanner(System.in);
	String [] s = new String[2];
        System.out.println("--------------------------ALL CONTAINER INSTANCES--------------------------");
        containers = dockerClient.listContainersCmd().withShowAll(true).exec();

        showContainers(containers);

        System.out.println("Choose a container from the above (number) and an activity:");
        System.out.println("Start(S)\nRename(R)\nInspect(I)");
        System.out.println("Or press 0 to go back");
        int c = in.nextInt(); //user chooses a container by number.
        if (c != 0) {
	    String id = containers.get(c-1).getId();
            String a = in.nextLine(); //user chooses an activity for the container.
            s[0] = id;
	    s[1] = a;
	 } else {
	    s[0] = null;
	    s[1] = null;
        }
	return s;
    }
    
	//this method shows the user's containers like this: <number>. <name>, Image: <image>

    //Containers case 2 (Show active containers)
    public String[] activeContainers() {
	Scanner in = new Scanner(System.in);
	String [] s = new String[2];
        System.out.println("--------------------------ACTIVE CONTAINER INSTANCES------------------------");
        containers = dockerClient.listContainersCmd().withShowAll(false).exec();

        showContainers(containers);

        System.out.println("Choose a container from the above (number) and an activity:");
        System.out.println("Restart(R)\nPause(P)\nUnpause(U)\nStop(S)\nKill(K)");
        System.out.println("Or press 0 to go back");
        int c = in.nextInt(); // user chooses a container by number.
        if (c != 0) {
	    String id = containers.get(c-1).getId();
            String a = in.nextLine();
            s[0] = id;
	    s[1] = a;
	 } else {
	    s[0] = null;
	    s[1] = null;
        }
	return s;
   }

}





