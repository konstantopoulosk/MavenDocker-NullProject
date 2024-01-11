package com.nullteam;

import com.github.dockerjava.api.command.InspectVolumeResponse;
import com.github.dockerjava.api.model.Container;
import java.io.FileWriter;
import java.io.IOException;

import com.github.dockerjava.api.model.Image;
import com.github.dockerjava.api.model.Network;
import com.opencsv.CSVWriter;
import java.util.List;
import java.util.Arrays;
import java.util.ArrayList;

public class DockerMonitor extends Thread {
    /**
     * List of all Container info,
     * in their last state.
     */
    private List<String[]> lastState = null;
    /**
     * List of all Container info,
     * currently.
     */
    private List<String[]> currentData = null;

    /**
     * This method executes Monitor Thread.
     */
    public void run() {
        try {
            while (!Thread.currentThread().isInterrupted()) {
                if (hasNewData()) {
                    writeCsv();
                }
            }
        } catch (RuntimeException e) {
            e.printStackTrace();
            // Handle other runtime exceptions
        }
    }

    /**
     * This method writes in containers.csv,
     * info of every container.
     * Container ID, Name, Image,
     * Status, Command, Created.
     */
    public void writeCsv() { //Write/update the csv file
        final String csvFilePath = "containers.csv";
        try (CSVWriter csvWriter = new CSVWriter(
                new FileWriter(csvFilePath, false))) {
            csvWriter.writeNext(new String[]{"Container ID", "Name",
                   "Image", "State", "Command", "Created", "Ports"}); // CSVFile header
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
    /**
     * This method checks if
     * container info has changed.
     * @return boolean
     */
    public boolean hasNewData() {
        //Check if there is any change inside the cluster
        List<Container> containers =
                ClientUpdater.getUpdatedContainersFromClient();
        currentData = new ArrayList<>();
        for (Container c : containers) {
            String[] csvData = new String[]{
                    c.getId(),
                    c.getNames()[0],
                    c.getImage(),
                    c.getState(),
                    c.getCommand().toString(),
                    c.getCreated().toString(),
                    c.getPorts().toString().split("@")[1].toString()
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
    /**
     * This method checks if
     * two lists are equal.
     * (Current Data and Last State)
     * @param list1 List&lt;String[]&gt;
     * @param list2 List&lt;String[]&gt;
     * @return boolean (true if the lists are equal, false otherwise).
     */
    public static boolean listsAreEqual(List<String[]> list1, List<String[]> list2) {

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
