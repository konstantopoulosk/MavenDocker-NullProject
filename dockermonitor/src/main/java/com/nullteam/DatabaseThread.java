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
    static int containers = 0;
    Connection connection;
     public DatabaseThread(Connection connection) { //Moved the connectToDatabase to ClientUpdater
        this.connection = connection; //Because connectivity methods
    }
    @Override
    public void run() {
         try {
             if (newUser(connection, ip)) {
                 containers++;
                 addToMeasurements(connection, containers);
                 readContainersFromCsv(connection, containers);
             } else {
                 //Not a new User, so update name, state, id (foreign key).
                 //also container from csv may not exist in database (implement) so insert.
                 //also container could be deleted (remove).
                if (csvEqualsDatabase(connection, ip)) {
                    //no remove, no implement. JUST UPDATE.
                    containers++;
                    addToMeasurements(connection, containers);
                    updateDatabase(connection, ip, containers);
                } else {
                    //remove or implement image.
                    if (csvHasMoreContainers(connection, ip)) {
                        //implement image, start new container.
                        addContainer(connection, ip, containers);
                    } else {
                        //container removed or image removed -> delete all containers.
                        removeContainerFromDatabase(connection, ip);
                    }
                }
             }
        } catch (RuntimeException e) {
            e.printStackTrace();
        }
    }
    public CSVReader openMyCSV() {
         try {
             FileReader fr = new FileReader("containers.csv");
             return new CSVReader(fr);
         } catch (Exception e) {
             e.printStackTrace();
             return null;
         }
    }
    //Reads the containers.csv and inserts everything into the table containers.
    public void readContainersFromCsv(Connection connection, int containers) {
        try {
            CSVReader csvReader = openMyCSV();
            String[] container;
            while ((container = csvReader.readNext()) != null) {
                if (!container[0].equals("Container ID")) {
                    String query = "INSERT INTO containers (containerId, name, image, state, SystemIp, id) " +
                            "VALUES (?, ?, ?, ?, ?, ?)";
                    PreparedStatement preparedStatement = connection.prepareStatement(query);
                    preparedStatement.setString(1, container[0]);
                    preparedStatement.setString(2, container[1]);
                    preparedStatement.setString(3, container[2]);
                    preparedStatement.setString(4, container[3]);
                    preparedStatement.setString(5, ip);
                    preparedStatement.setInt(6, containers);
                }
            }
            csvReader.close();
        } catch (IOException | SQLException | CsvValidationException | ArrayIndexOutOfBoundsException e) {
            e.printStackTrace();
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
            e.printStackTrace();
        }
    }
    public boolean newUser(Connection connection, String ip) {
         try {
             String query = "SELECT COUNT(*) FROM containers WHERE SystemIp = ?";
             PreparedStatement preparedStatement = connection.prepareStatement(query);
             preparedStatement.setString(1, ip);
             ResultSet resultSet = preparedStatement.executeQuery();
             resultSet.next();
             int count = resultSet.getInt(1);
             if (count > 0) {
                 return false; //not a new user.
             } else {
                 return true; //new user.
             }
         } catch (Exception e) {
             e.printStackTrace();
             return false;
         }
    }
    public void updateDatabase(Connection connection, String ip, int containers) {
         try {
             CSVReader csvReader = openMyCSV();
             String[] container;
             while ((container = csvReader.readNext()) != null) {
                 if (!container[0].equals("Container ID")) {
                     String query = "UPDATE containers SET name = ?, state = ?, id = ? WHERE SystemIp = ?";
                     PreparedStatement preparedStatement = connection.prepareStatement(query);
                     preparedStatement.setString(1, container[1]);
                     preparedStatement.setString(2, container[3]);
                     preparedStatement.setInt(3, containers);
                     preparedStatement.setString(4, ip);
                     ResultSet resultSet = preparedStatement.executeQuery();
                 }
             }
             csvReader.close();
         } catch (Exception e) {
             e.printStackTrace();
         }
    }
    public void addContainer(Connection connection, String ip, int containers) {
         try {
             CSVReader csvReader = openMyCSV();
             String[] container;
             while ((container = csvReader.readNext()) != null) {
                 if (!container[0].equals("Container ID")) {
                    if (!containerExistsInDatabase(connection, ip, container[0])) {
                        String query = "INSERT INTO containers (containerId, name, image, state, SystemIp, id)" +
                                "VALUES (?, ?, ?, ?, ?, ?)";
                        PreparedStatement preparedStatement = connection.prepareStatement(query);
                        for (int i = 0; i < 4; i++) {
                            preparedStatement.setString(i + 1, container[i]);
                        }
                        preparedStatement.setString(5, ip);
                        preparedStatement.setInt(6, containers);
                        ResultSet resultSet = preparedStatement.executeQuery();
                    }
                 }
             }
             csvReader.close();
         } catch (Exception e) {
             e.printStackTrace();
         }
    }
    public List<String> getListContainersCsv() {
        try {
            CSVReader csvReader = openMyCSV();
            String[] container;
            List<String> csvContainers = new ArrayList<>();
            while ((container = csvReader.readNext()) != null) {
                if (!container[0].equals("Container ID")) {
                    csvContainers.add(container[0]);
                }
            }
            return csvContainers;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    public List<String> getListContainersDatabase() {
         try {
             String query = "SELECT containerId FROM containers WHERE SystemIp = ?";
             PreparedStatement preparedStatement = connection.prepareStatement(query);
             preparedStatement.setString(1, ip);
             ResultSet resultSet = preparedStatement.executeQuery();
             List<String> databaseContainers = new ArrayList<>();
             while (resultSet.next()) {
                 databaseContainers.add(resultSet.getString("containerId"));
             }
             return databaseContainers;
         } catch (Exception e) {
             e.printStackTrace();
             return null;
         }
    }
    public void finallyRemove(String ip, String id) {
         try {
             String queryDelete = "DELETE FROM containers WHERE SystemIp = ? AND containerId = ?";
             PreparedStatement preparedStatement = connection.prepareStatement(queryDelete);
             preparedStatement.setString(1, ip);
             preparedStatement.setString(2, id);
             ResultSet resultSet = preparedStatement.executeQuery();
         } catch (Exception e) {
             e.printStackTrace();
         }
    }
    public void removeContainerFromDatabase(Connection connection, String ip) {
         try {
             List<String> csvContainers = new ArrayList<>(getListContainersCsv());
             List<String> databaseContainers = new ArrayList<>(getListContainersDatabase());
             boolean containerShouldBeRemoved = true;
             for (int i = 0; i < databaseContainers.size(); i++) {
                 for (int j = 0; j < csvContainers.size(); j++) {
                     if (csvContainers.get(j).equals(databaseContainers.get(i))) {
                         containerShouldBeRemoved = false;
                     }
                 }
                 if (containerShouldBeRemoved) {
                     finallyRemove(ip, databaseContainers.get(i));
                 }
             }
         } catch (Exception e) {
             e.printStackTrace();
         }
    }
    public boolean containerExistsInDatabase(Connection connection,
                                             String ip, String containerId) {
         try {
             String query = "SELECT COUNT(*) FROM containers WHERE SystemIp = ? AND containerId = ?";
             PreparedStatement preparedStatement = connection.prepareStatement(query);
             preparedStatement.setString(1, ip);
             preparedStatement.setString(2, containerId);
             ResultSet resultSet = preparedStatement.executeQuery();
             resultSet.next();
             int count = resultSet.getInt(1);
             if (count > 0) {
                 return true; //Container already exists.
             } else {
                 return false; //Container does not exist.
             }
         } catch (Exception e) {
             e.printStackTrace();
             return false;
         }
    }
    public boolean csvHasMoreContainers(Connection connection, String ip) {
         try {
             CSVReader csvReader = openMyCSV();
             String[] container;
             int containersInCSV = 0;
             while ((container = csvReader.readNext()) != null) {
                 if (!container[0].equals("Container ID")) {
                     containersInCSV++;
                 }
             }
             String query = "SELECT COUNT(*) FROM containers WHERE SystemIp = ?";
             PreparedStatement preparedStatement = connection.prepareStatement(query);
             preparedStatement.setString(1, ip);
             ResultSet resultSet = preparedStatement.executeQuery();
             resultSet.next();
             int containersInDatabase = resultSet.getInt(1);
             if (containersInDatabase < containersInCSV) {
                 return true;
             } else {
                 return false;
             }
         } catch (Exception e) {
             e.printStackTrace();
             return false;
         }
    }
    public boolean csvEqualsDatabase(Connection connection, String ip) {
         try {
             CSVReader csvReader = openMyCSV();
             String[] container;
             int containersInCSV = 0;
             while ((container = csvReader.readNext()) != null) {
                 if (!container[0].equals("Container ID")) {
                     containersInCSV++;
                 }
             }
             String query = "SELECT COUNT(*) FROM containers WHERE SystemIp = ?";
             PreparedStatement preparedStatement = connection.prepareStatement(query);
             preparedStatement.setString(1, ip);
             ResultSet resultSet = preparedStatement.executeQuery();
             resultSet.next();
             int containersInDatabase = resultSet.getInt(1);
             if (containersInDatabase == containersInCSV) {
                 return true;
             } else {
                 return false;
             }
         } catch (Exception e) {
             e.printStackTrace();
             return false;
         }
    }
}



