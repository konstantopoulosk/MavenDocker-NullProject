package com.nullteam;

import com.github.dockerjava.api.model.Container;
import com.github.dockerjava.api.model.ContainerPort;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;

public class DockerMonitor implements Runnable {
    Modelisation m;

        /**Constructor */
    public DockerMonitor (Modelisation m) {
        this.m = m;
    }
    public void run() {
        try{
            StringBuilder stbuilder = new StringBuilder();
            stbuilder.append("CONTAINER ID").append(",").append("IMAGE").append(",").append("COMMAND").append(",")
            .append("CREATED").append(",").append("STATUS").append(",").append("PORTS").append(",").append("NAMES").append("\n");            
            List<String> id = new ArrayList<String>();
            List<String> image = new ArrayList<String>();
            List<String> command = new ArrayList<String>();
            List<String[]> name = new ArrayList<String[]>();
            List<String> status = new ArrayList<String>();
            List<Long> timestamp = new ArrayList<Long>();
            List<ContainerPort[]> port = new ArrayList<ContainerPort[]>();
            /**Information for each container */
            for (Container c : m.getContainers() ) {
                id.add(c.getId());
                image.add(c.getImage());
                command.add(c.getCommand());
                timestamp.add(c.getCreated());
                status.add(c.getStatus()); 
                port.add(c.getPorts());
                name.add(c.getNames()); 
                stbuilder.append(c.getId()).append(",").append(c.getImage()).append(",").append(c.getCommand()).append(",")
                .append(c.getCreated()).append(",").append(c.getStatus()).append(",").append(c.getPorts()).append(",").append(c.getNames()).append("\n");
            }
            try(FileWriter writer = new FileWriter("C:/Users/ilian/MavenDocker-NullProject/dockermonitor/target/generated-sources/annotations")) {
                writer.write(stbuilder.toString());
            } catch(Exception e) {
                System.out.println("ERROR");
            }
        } catch (Exception e) {
            System.out.println("ERROR"); //Exception hundling
        }
    }
}
