package com.nullteam;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.model.Container;
import java.io.FileWriter;
import java.io.IOException;

import com.github.dockerjava.core.DefaultDockerClientConfig;
import com.github.dockerjava.core.DockerClientBuilder;
import com.opencsv.CSVWriter;
import java.util.List;
import java.util.Arrays;
import java.util.ArrayList;

public class DockerMonitor extends Thread {
    private final String csvFilePath = "containers.csv";
    private List<String[]> lastState = null;

    public void run() {
        try(CSVWriter csvWriter = new CSVWriter(new FileWriter(csvFilePath, false))) {
            // Write CSV header
            csvWriter.writeNext(new String[]{"Container ID", "Image", "Status", "Command", "Created"}); //o header sto csv file
            while (!Thread.currentThread().isInterrupted()) {
                monitorContainers(csvWriter);
                Thread.sleep(500);
            }
        } catch (InterruptedException | IOException e) {
            Thread.currentThread().interrupt();
            e.printStackTrace();
        } //ISWS XRISTASTEI SE FINALLY NA KLEINW TON DOCKER CLIENT AN TO THREAD GINEI INTERRUPTED
    }
    private void monitorContainers(CSVWriter csvWriter) {
        DefaultDockerClientConfig.Builder builder= DefaultDockerClientConfig.createDefaultConfigBuilder();
        builder.withDockerHost("tcp://localhost:2375");
        DockerClient dockerClient = DockerClientBuilder.getInstance(builder).build();
        dockerClient.versionCmd().exec();
        List<Container> containers = dockerClient.listContainersCmd().withShowAll(true).exec();
        List<String[]> currentData = new ArrayList<>();
        for (Container c : containers) {
            String[] csvData = new String[]{
                    c.getId(),
                    c.getImage(),
                    c.getState(),
                    c.getCommand(),
                    c.getCreated().toString()
            };
            currentData.add(csvData);
        }
        // Check if the data has changed
        if (!listsAreEqual(currentData, lastState)) {

            // Write the current data to the CSV file
            for (String[] csvData : currentData) {
                csvWriter.writeNext(csvData);
            }
            try {
                csvWriter.flush(); // To immediately write to the fill
            } catch (IOException e) {
                e.printStackTrace();
            }
            // Update the lastState with the currentData
            lastState = new ArrayList<>(currentData);
        }
    }
    private boolean listsAreEqual(List<String[]> list1, List<String[]> list2) {
        // Check for null references
        if (list1 == null && list2 == null) {
            return true;
        }
        if (list1 == null || list2 == null) {
            return false;
        }
        if (list1.size() != list2.size()) {
            return false;
        }
        for (int i = 0; i < list1.size(); i++) {
            if (!Arrays.equals(list1.get(i), list2.get(i))) {
                return false;
            }
        }
        return true;
    }
}
