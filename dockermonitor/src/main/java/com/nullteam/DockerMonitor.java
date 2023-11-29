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
    private List<String[]> currentData = null;

    public void run() {
        while (!Thread.currentThread().isInterrupted()) {
            if(hasNewData()) {
                writeCsv();
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    private void writeCsv() { //Write/update the csv file
        try(CSVWriter csvWriter = new CSVWriter(new FileWriter(csvFilePath, false))){
            csvWriter.writeNext(new String[]{"Container ID", "Image", "Status", "Command", "Created"}); // CSVFile header
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
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private boolean hasNewData(){ // Check if there is any change inside the cluster
        DefaultDockerClientConfig.Builder builder= DefaultDockerClientConfig.createDefaultConfigBuilder();
        builder.withDockerHost("tcp://localhost:2375");
        DockerClient dockerClient = DockerClientBuilder.getInstance(builder).build();
        dockerClient.versionCmd().exec();
        List<Container> containers = dockerClient.listContainersCmd().withShowAll(true).exec();
        currentData = new ArrayList<>();
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
        if (!listsAreEqual(currentData, lastState)) {
            lastState = new ArrayList<>(currentData);
            return true;
        } else {
            return false;
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
