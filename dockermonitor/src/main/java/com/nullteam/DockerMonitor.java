import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.model.Container;
import com.github.dockerjava.core.DockerClientBuilder;

import java.io.FileWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

public class DockerMonitor implements Runnable {
    private DockerClient client;
    private List<java.awt.Container> containers;
    private List<Image> images;

        /**Constructor */
    public DockerMonitor (DockerClient client, List<java.awt.Container> containers, List<Image> images) {
        this.client = client;
        this.images = images;
        this.containers = containers;
    }
    public void run() {
        try{
            StringBuilder stbuilder = new StringBuilder();
            stbuilder.append("CONTAINER ID").append(",").append("IMAGE").append(",").append("COMMAND").append(",")
            .append("CREATED").append(",").append("STATUS").append(",").append("PORTS").append(",").append("NAMES").append("\n");
            //HashMap<String, String> id = new HashMap<String, String>();
            //Hashmap thelei key kai value
            
           List<String> id = new ArrayList<String>();
           List<String> image = new ArrayList<String>();
           List<String> command = new ArrayList<String>();
           List<String[]> name = new ArrayList<String[]>();
           List<String> status = new ArrayList<String>();
           List<Long> timestamp = new ArrayList<Long>();
           List<ContainerPort[]> port = new ArrayList<ContainerPort[]>();
        /**Information for each container */
        for (Container c : containers ) {
            id.add(containers.getId());
            image.add(containers.getImage());
            command.add(containers.getCommand());
            timestamp.add(containers.getCreated());
            status.add(containers.getStatus()); 
            port.add(containers.getPorts());
            name.add(containers.getNames()); 
             stbuilder.append(containers.getId()).append(",").append(containers.getImage()).append(",").append(containers.getCommand()).append(",")
            .append(containers.getCreated()).append(",").append(containers.getStatus()).append(",").append(containers.getPorts()).append(",").append(containers.getNames()).append("\n");
             }

        // memory csv txt



    } catch (InterruptedException e) {
        System.out.println("ERROR");
    }
    try(FileWriter writer = new FileWriter(null)) {
        writer.write(stbuilder.toString());

    } catch(Exception e) {
        System.out.println("ERROR");
    }
    }
}
