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
     * in last state.
     */
    private List<String[]> lastState = null;
    /**
     * List of all Container info,
     * currently.
     */
    private List<String[]> currentData = null;
    private List<String[]> lastStateImages = null;
    private List<String[]> currentDataImages = null;
    private List<String[]> lastStateVolumes = null;
    private List<String[]> currentDataVolumes = null;
    private List<String[]> lastStateNetworks = null;
    private List<String[]> currentDataNetworks = null;

    /**
     * Method run to execute Monitor Thread.
     */
    public void run() {
        try {
            while (!Thread.currentThread().isInterrupted()) {
                if (hasNewData()) {
                    writeCsv();
                }
                if (hasNewDataImage()) {
                    writeImageCsv();
                }
                if (hasNewDataVolumes()) {
                    writeVolumeCsv();
                }
                if (hasNewDataNetworks()) {
                    writeNetworkCsv();
                }
            }
        } catch (RuntimeException e) {
            e.printStackTrace();
            // Handle other runtime exceptions
        }
    }

    /**
     * Method writeCsv writes in containers.csv,
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
    public void writeImageCsv() {
        final String csvFilePath = "images.csv";
        CSVWriter csvWriter = null;
        try {
            csvWriter = new CSVWriter(
                    new FileWriter(csvFilePath, false));
            csvWriter.writeNext(new String[]{"Image ID", "Repository Name",
                    "Image Tag", "Times Used", "Size"}); //HEADER
            for (String[] csvData : currentDataImages) {
                csvWriter.writeNext(csvData);
            }
            try {
                csvWriter.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
            lastStateImages = new ArrayList<>(currentDataImages);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (csvWriter != null) {
                try {
                    csvWriter.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void writeVolumeCsv() { //Write/update the csv file
        final String csvFilePath = "Volumes.csv";
        try (CSVWriter csvWriter = new CSVWriter(
                new FileWriter(csvFilePath, false))) {
            csvWriter.writeNext(new String[]{ "Name", "Driver",
                    "Created", "Size", "Mountpoint" }); // CSVFile header
            for (String[] csvData : currentData) {
                csvWriter.writeNext(csvData);
            }
            try {
                csvWriter.flush(); // To immediately write to the fill
            } catch (IOException e) {
                e.printStackTrace();
            }
            // Update the lastState with the currentData
            lastStateVolumes = new ArrayList<>(currentDataVolumes);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void writeNetworkCsv() { //Write/update the csv file
        final String csvFilePath = "Networks.csv";
        try (CSVWriter csvWriter = new CSVWriter(
                new FileWriter(csvFilePath, false))) {
            csvWriter.writeNext(new String[]{ "NetworkID", "Name",
                   "Scope", "Driver" }); // CSVFile header
            for (String[] csvData : currentData) {
                csvWriter.writeNext(csvData);
            }
            try {
                csvWriter.flush(); // To immediately write to the fill
            } catch (IOException e) {
                e.printStackTrace();
            }
            // Update the lastState with the currentData
            lastStateNetworks = new ArrayList<>(currentDataNetworks);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Method hasNewData checks if,
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
    public boolean hasNewDataImage() {
        List<Image> images = ClientUpdater.getUpdatedImagesFromClient();
        currentDataImages = new ArrayList<>();
        for (Image image : images) {
            String[] csvData1 = new String[] {
                    image.getId(),
                    image.getRepoDigests()[0].split("@")[0],
                    image.getRepoTags()[0].split(":")[1],
                    timesUsed(image),
                    image.getSize().toString()
            };
            currentDataImages.add(csvData1);
        }
        if (!listsAreEqual(currentDataImages, lastStateImages)) {
            lastStateImages = new ArrayList<>(currentDataImages);
            return  true;
        } else {
            return  false;
        }
    }
    public boolean hasNewDataVolumes() {
        //Check if there is any change inside the cluster
        List<InspectVolumeResponse> volumes =
                ClientUpdater.getUpdatedVolumesFromClient();
        currentDataVolumes = new ArrayList<>();
        for (InspectVolumeResponse v : volumes) {
            String[] csvData2 = new String[]{
                    v.getName(),
                    v.getDriver(),
                    DockerVolume.createdAt(v.getName()), v.getMountpoint()
            };
            currentDataVolumes.add(csvData2);
        }
        if (!listsAreEqual(currentDataVolumes, lastStateVolumes)) {
            lastStateVolumes = new ArrayList<>(currentDataVolumes);
            return true;
        } else {
            return false;
        }
    }

    public boolean hasNewDataNetworks() {
        //Check if there is any change inside the cluster
        List<Network> networks =
                ClientUpdater.getUpdatedNetworksFromClient();
        currentDataNetworks = new ArrayList<>();
        for (Network n : networks) {
            String[] csvData3 = new String[]{
                    n.getId(),
                    n.getName(),
                    n.getDriver(),
                    n.getScope()
            };
            currentDataNetworks.add(csvData3);
        }
        if (!listsAreEqual(currentDataNetworks, lastStateNetworks)) {
            lastStateNetworks = new ArrayList<>(currentDataNetworks);
            return true;
        } else {
            return false;
        }
    }


    /**
     * Method listsAreEqual checks if,
     * two lists are equal.
     * Current Data and Last State.
     * @param list1 The Current Data-list.
     * @param list2 The Last State-list.
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
    public String timesUsed(Image image) {
        int times= 0;
        List<Container> containers = ClientUpdater.getUpdatedContainersFromClient();
        for (Container container : containers) {
            if (image.getId().equals(container.getImageId())) {
                times++;
            }
        }
        return String.valueOf(times);
    }
}
