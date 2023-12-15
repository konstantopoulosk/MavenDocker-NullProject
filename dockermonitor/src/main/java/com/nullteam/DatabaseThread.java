package com.nullteam;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;
import java.io.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DatabaseThread extends  Thread {
    List<String[]> lastStateContainer = null;
    List<String[]> lastStateImage = null;
    List<String[]> currentStateContainer = null;
    List<String[]> currentStateImage = null;
    Connection connection;
    public DatabaseThread(Connection connection) { //Moved the connectToDatabase to ClientUpdater
        this.connection = connection; //Because connectivity methods
    }
    @Override
    public void run() {
        try {
            while (!Thread.currentThread().isInterrupted()) { //Runs until interrupted
                if (hasNewDataImages()) {
                    deleteAllRowsFromTable(connection, "dockerimage"); //solves the problem with primary keys.
                    readImagesFromCsv(connection); //No duplicate primary keys
                }
                if (hasNewDataContainers()) {
                    deleteAllRowsFromTable(connection, "dockerinstance");
                    readContainersFromCsv(connection);
                }
            }
        } catch (RuntimeException e) {
            e.printStackTrace();
        }
    }
    //Reads the images.csv and inserts everything into the table dockerimage
    public void readImagesFromCsv(Connection connection) {
        try {
            FileReader fr = new FileReader("images.csv");
            CSVReader csvReader = new CSVReader(fr);
            String[] image;
            while ((image = csvReader.readNext()) != null) {
                String querryImage = String.format("INSERT INTO dockerimage (id, repository, tag, timesUsed, size) VALUES ('%s', '%s', '%s', '%s', '%s')", image[0], image[1], image[2], image[3], image[4]);
                Statement statement = connection.createStatement();
                statement.executeUpdate(querryImage);
            }
            csvReader.close();
        } catch (SQLException | IOException | CsvValidationException e) {
            throw new RuntimeException(e);
        }
    }
    //Reads the containers.csv and inserts everything into the table dockerinstance.
    public void readContainersFromCsv(Connection connection) {
        try {
            FileReader fr = new FileReader("containers.csv");
            CSVReader csvReader = new CSVReader(fr);
            String[] container;
            while ((container = csvReader.readNext()) != null) {
                String querryContainer = String.format("INSERT INTO dockerinstance (id, name, image, state, command, created, ports) VALUES (\"%s\", \"%s\", \"%s\", \"%s\", \"%s\", \"%s\", \"%s\")", container[0], container[1], container[2], container[3], container[4], container[5], container[6]);
                Statement statement = connection.createStatement();
                statement.executeUpdate(querryContainer);
            }
            csvReader.close();
        } catch (IOException | SQLException e) {
            throw new RuntimeException(e);
        } catch (CsvValidationException e) {
            // This block will be executed if the CSV file contains a malformed line.
            System.out.println("CSV file contains a malformed line. The line number is: " + e.getLineNumber());
        }
    }
    //Checks if CSV has any new data since last checked
    public boolean hasNewDataImages() {
        try {
            FileReader fileReader = new FileReader("images.csv");
            CSVReader csvReader = new CSVReader(fileReader);
            currentStateImage = new ArrayList<>();
            String[] currentImage;
            while (( currentImage = csvReader.readNext()) != null) {
                currentStateImage.add(currentImage);
            }
            csvReader.close();
            if (!DockerMonitor.listsAreEqual(currentStateImage, lastStateImage)) {
                lastStateImage = new ArrayList<>(currentStateImage);
                return true;
            } else {
                return false;
            }
        } catch (CsvValidationException | IOException e) {
            throw new RuntimeException(e);
        }
    }
    //Checks if CSV has any new data since last checked
    public boolean hasNewDataContainers() {
        try {
            FileReader fr = new FileReader("containers.csv");
            CSVReader csvReader = new CSVReader(fr);
            currentStateContainer = new ArrayList<>();
            String[] currentContainer;
            while ((currentContainer = csvReader.readNext()) != null) {
                currentStateContainer.add(currentContainer);
            }
            csvReader.close();
            if (!DockerMonitor.listsAreEqual(currentStateContainer, lastStateContainer)) {
                lastStateContainer = new ArrayList<>(currentStateContainer);
                return true;
            } else {
                return false;
            }
        } catch (CsvValidationException | IOException e) {
            throw new RuntimeException(e);
        }
    }
    //This method is deleting every row from a table
    //This is because to write a change in the table for instance change status (Up -> Exited)
    //We have to rewrite the csv (at this moment)
    //If we create methods for UPDATING A COLUMN ON A TABLE WHEN A CHANGE HAPPENS
    //this is less useful.
    public static void deleteAllRowsFromTable(Connection connection, String tableName) {
        try {
            String querry = String.format("DELETE FROM %s", tableName);
            Statement statement = connection.createStatement();
            statement.executeUpdate(querry);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}



