import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.model.Container;
import java.util.List;
import java.util.Scanner;

public class ExecutorThread<DockerClient, Image, Container> implements Runnable {
    DockerClient dockerClient;
    List<Image> images;
    List<java.awt.Container> containers;
    public <DockerClient> ExecutorThread(DockerClient dockerClient, List<Image> images, List<java.awt.Container> containers) {
        this.dockerClient = dockerClient;
        this.images = images;
        this.containers = containers;
    }
    Scanner in = new Scanner(System.in); //xreiazetai gia to rename
    <ContainerInfo> void run(String id, String task){
        switch(task) {
            case "ST": //start
                this.dockerClient.startContainer(id);
                break;
            case "RN": //rename
                System.out.print("New container ID: ");
                String newId = in.nextLine();
                this.dockerClient.renameContainer(id, newId);
                break;
            case "I": //inspect (mallon afora to monitor)
                final ContainerInfo info = this.dockerClient.inspectContainer("containerID");
                break; 
            case "RM": //remove
                this.dockerClient.removeContainer(id);
                break;           
            case "RS": //restart
                this.dockerClient.restartContainer(id);
                break;
            case "P": //pause
                this.dockerClient.pauseContainer(id);
                break;            
            case "U": //unpause
                this.dockerClient.unpauseContainer(id);
                break;
            case "SP": //stop
                this.dockerClient.stopContainer(id, 10); // kill after 10 seconds
                break;           
            case "K": //kill
                this.dockerClient.killContainer(id);
                break;
        }
    }
}
