package com.nullteam;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvMalformedLineException;
import com.opencsv.exceptions.CsvValidationException;

import java.io.FileReader;
import java.io.IOException;
import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class DatabaseThread extends Thread {
    final String ip = ClientUpdater.getIp();
    static int images = 0;
    static int containers = 0;
    List<String[]> lastStateContainer = null;
    List<String[]> currentStateContainer = null;

    Connection connection;
     public DatabaseThread(Connection connection) { //Moved the connectToDatabase to ClientUpdater
        this.connection = connection; //Because connectivity methods
    }
    @Override
    public void run() {
        try {
            containers++;
            addToMeasurements(connection, containers);
            readContainersFromCsv(connection, containers);
        } catch (RuntimeException e) {
            e.printStackTrace();
        }
    }
    //Reads the containers.csv and inserts everything into the table containers.
    public void readContainersFromCsv(Connection connection, int containers) {
        try {
            FileReader fr = new FileReader("containers.csv");
            CSVReader csvReader = new CSVReader(fr);
            String[] container;
            while ((container = csvReader.readNext()) != null) {
                if (!container[0].equals("Container ID")) {
                    String queryContainer = String.format("INSERT INTO containers (containerId, name, image, state, SystemIp, id) " +
                            "VALUES (\"%s\", \"%s\", \"%s\", \"%s\", \"%s\", \"%s\")",
                            container[0], container[1], container[2], container[3], ip, containers);
                    Statement statement = connection.createStatement();
                    statement.executeUpdate(queryContainer);
                }
            }
            csvReader.close();
        } catch (IOException | SQLException | CsvValidationException | ArrayIndexOutOfBoundsException e) {

        }
    }
    public void addToMeasurements(Connection connection,int containers) {
        try {
            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
            LocalDateTime now = LocalDateTime.now();
            String query = String.format("INSERT INTO measurements (`id`, `date`, `SystemIp`) " +
                    "VALUES (\"%s\", \"%s\", \"%s\")", containers, dtf.format(now), ip);
            Statement statement = connection.createStatement();
            statement.executeUpdate(query);
        } catch (SQLException e) {
            //throw new RuntimeException(e);
        }
    }
}



