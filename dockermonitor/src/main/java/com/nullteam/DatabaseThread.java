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
    final String ip = ClientUpdater.getIp(); //System Ip to recognize the user.
    static int containers = giveMeCount(); //Auto increment primary key for measurements table.
    Connection connection; //Connection to database.
     public DatabaseThread(Connection connection) { //Moved the connectToDatabase to ClientUpdater
        this.connection = connection; //Because connectivity methods
    }
    @Override
    public void run() {
         try {
             if (newUser(connection, ip)) { //Checks if he is a new user.
                 //New user -> write his data.
                 containers++;
                 addToMeasurements(connection, containers);
                 readContainersFromCsv(connection, containers);
             } else {
                 updateDatabase();
             }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public static int giveMeCount() { //gives the last number of measurements, to start from there.
         try {
             String query = "SELECT COUNT(*) FROM measurements";
             Connection connection1 = ClientUpdater.connectToDatabase(); //new connection
             PreparedStatement preparedStatement = connection1.prepareStatement(query);
             ResultSet resultSet = preparedStatement.executeQuery(); //execute the query
             resultSet.next(); //get the number
             int count = resultSet.getInt(1); // count rows = the last number of measurement
             connection1.close(); //close connection
             return count;
         } catch (Exception e) {
             e.printStackTrace();
             return -1;
         }
    }
    //Reads the containers.csv and inserts everything into the table containers.
    public void readContainersFromCsv(Connection connection, int containers) {
        try {
            FileReader fr = new FileReader("containers.csv");
            CSVReader csvReader = new CSVReader(fr); //opens the csv
            String[] container;
            while ((container = csvReader.readNext()) != null) {
                if (!container[0].equals("Container ID")) { // Does not insert the header.
                    String query = "INSERT INTO containers (containerId, name, image, state, SystemIp, id) " +
                            "VALUES (?, ?, ?, ?, ?, ?)";
                    PreparedStatement preparedStatement = connection.prepareStatement(query);
                    for (int i = 0; i < 4; i++) {
                        preparedStatement.setString(i+1, container[i]); //easier with for loop.
                    } //fewer lines.
                    preparedStatement.setString(5, ip);
                    preparedStatement.setInt(6, containers);
                    //sets the ? with actual values.
                    preparedStatement.executeUpdate(); //executes the query
                }
            }
            csvReader.close(); //closes the csv
        } catch (IOException | SQLException | CsvValidationException | ArrayIndexOutOfBoundsException e) {
            e.printStackTrace();
        }
    }
    //adds measurement into table
    public void addToMeasurements(Connection connection,int containers) {
        try {
            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
            LocalDateTime now = LocalDateTime.now(); //DATETIME NOW
            String query = String.format("INSERT INTO measurements (`id`, `date`, `SystemIp`) " +
                    "VALUES (\"%s\", \"%s\", \"%s\")", containers, dtf.format(now), ip);
            Statement statement = connection.createStatement();
            statement.executeUpdate(query); //executes the query
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    //checks if user is new or not with his System Ip.
    public boolean newUser(Connection connection, String ip) {
         try {
             String query = "SELECT COUNT(*) FROM containers WHERE SystemIp = ?";
             PreparedStatement preparedStatement = connection.prepareStatement(query);
             preparedStatement.setString(1, ip);
             ResultSet resultSet = preparedStatement.executeQuery();
             resultSet.next();
             int count = resultSet.getInt(1);
             if (count > 0) { //count of the rows with SystemIp = ip
                 return false; //not a new user.
             } else {
                 return true; //new user.
             }
         } catch (Exception e) {
             e.printStackTrace();
             return false;
         }
    }
    public List<String[]> getEverythingFromDatabase() { //Returns a list with String[]
        try { //with the rows from containers table but only the containerId, name, image, state columns
            List<String[]> containersInDatabase = new ArrayList<>();
            String query = "SELECT * FROM containers WHERE SystemIp = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, ip);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                String[] c = new String[] {
                        resultSet.getString("containerId"),
                        resultSet.getString("name"),
                        resultSet.getString("image"),
                        resultSet.getString("state")
                };
                containersInDatabase.add(c);
            }
            return containersInDatabase;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    //returns a list with String[] with the index of csv but only Container ID, Name, Image, State columns
    public List<String[]> getEverythingFromCsv() {
        try {
            List<String[]> containersInCsv = new ArrayList<>();
            FileReader fr = new FileReader("containers.csv");
            CSVReader csvReader = new CSVReader(fr);
            String[] container;
            while ((container = csvReader.readNext()) != null) {
                if (!container[0].equals("Container ID")) { // Do not include the header from csv into database table.
                    String[] data = new String[] {
                            container[0], //containerId
                            container[1], //Name
                            container[2], //Image
                            container[3], //state
                    };
                    containersInCsv.add(data);
                }
            }
            csvReader.close();
            return containersInCsv;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    //This method changes the name of a specific container and
    //adds to measurements because Rename is a function
    public void changeName(String newName, String containerId) {
        try {
            //THIS IS A MEASUREMENT SO ADD TO MEASUREMENTS
            containers++;
            addToMeasurements(connection, containers);
            //QUERY to UPDATE the name and id (foreign key)
            String queryName = "UPDATE containers SET name = ?, id = ? WHERE SystemIp = ? AND containerId = ?";
            PreparedStatement preparedStatementName = connection.prepareStatement(queryName);
            preparedStatementName.setString(1, newName); //NEW NAME
            preparedStatementName.setInt(2, containers);
            preparedStatementName.setString(3, ip);
            preparedStatementName.setString(4, containerId);
            //Set the values.
            preparedStatementName.executeUpdate(); //CHANGES THE NAME according to CSV
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    //This method changes the name of a specific container
    //adds to measurements because we consider it a function
    public void changeState(String state, String containerId) {
        try {
            containers++;
            addToMeasurements(connection, containers);
            String queryState = "UPDATE containers SET state = ?, id = ? WHERE SystemIp = ? AND containerId = ?";
            PreparedStatement preparedStatementState = connection.prepareStatement(queryState);
            preparedStatementState.setString(1, state); //NEW STATE
            preparedStatementState.setInt(2, containers);
            preparedStatementState.setString(3, ip);
            preparedStatementState.setString(4, containerId);
            preparedStatementState.executeUpdate(); //CHANGES THE STATE.
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    //this method removes a container from the table because
    //user either selected remove container or remove image
    public void removeContainer(String containerId) {
        try {
            String queryRemove = "DELETE FROM containers WHERE SystemIp = ? AND containerId = ?";
            PreparedStatement preparedStatementRemove = connection.prepareStatement(queryRemove);
            preparedStatementRemove.setString(1, ip);
            preparedStatementRemove.setString(2, containerId);
            preparedStatementRemove.executeUpdate(); //REMOVES FROM DATABASE IF CONTAINER
            //DOES NOT EXIST IN CSV.
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    //this method inserts a new container into the table
    //adds to measurements because it's a function
    //User selected implement image
    public void implementContainer(List<String[]> containersInCsv,int i) {
        try {
            //THIS IS A MEASUREMENT ADD TO TABLE
            containers++;
            addToMeasurements(connection, containers);
            //WRITE CONTAINER IN DATABASE IMPLEMENT IMAGE -> START NEW CONTAINER
            String queryImplement = "INSERT INTO containers (containerId, name, image, state, SystemIp, id)" +
                    "VALUES (?, ?, ?, ?, ?, ?)";
            PreparedStatement preparedStatementImplement = connection.prepareStatement(queryImplement);
            preparedStatementImplement.setString(1, containersInCsv.get(i)[0]); //ID
            preparedStatementImplement.setString(2, containersInCsv.get(i)[1]); //NAME
            preparedStatementImplement.setString(3, containersInCsv.get(i)[2]); //IMAGE
            preparedStatementImplement.setString(4, containersInCsv.get(i)[3]); //STATE
            preparedStatementImplement.setString(5, ip);
            preparedStatementImplement.setInt(6, containers);
            preparedStatementImplement.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    //When it's not a new User, we have to update the old values into new values
    //if there is any new values.
    public void updateDatabase() {
         //It's a measurement because
        containers++;
        addToMeasurements(connection,containers);
         try {
             //Lists containing containers in DB and containers in CSV
             List<String[]> containersInDatabase = new ArrayList<>(getEverythingFromDatabase());
             List<String[]> containersInCsv = new ArrayList<>(getEverythingFromCsv());
            for (int i = 0; i < containersInDatabase.size(); i++) {
                boolean exists = false; //Container in database does NOT exist in CSV
                for (int j = 0; j < containersInCsv.size(); j++) {
                    if (containersInDatabase.get(i)[0].equals(containersInCsv.get(j)[0])) {
                        exists = true; //Container in database EXISTS in CSV
                        //Checking with Container ID.
                        if (!containersInDatabase.get(i)[1].equals(containersInCsv.get(j)[1])) {
                            //Different Name -> RENAME HAPPENED.
                            changeName(containersInCsv.get(j)[1], containersInDatabase.get(i)[0]);
                        }
                        if (!containersInDatabase.get(i)[3].equals(containersInCsv.get(j)[3])) {
                            //Different STATE -> START STOP RESTART PAUSE KILL UNPAUSE ... happened
                            changeState(containersInCsv.get(j)[3], containersInDatabase.get(i)[0]);
                        }
                    }
                    String idImplement = containersInCsv.get(j)[0];
                    List<String> c = new ArrayList<>(getListContainersDatabase());
                    if(!c.contains(idImplement)) {
                        System.out.println("SHOULD IMPLEMENT");
                        implementContainer(containersInCsv, j);
                    }
                }
                if (!exists) {
                    System.out.println("SHOULD REMOVE");
                    //Removed happened REMOVE or REMOVE IMAGE.
                    removeContainer(containersInDatabase.get(i)[0]);
                }
            }
         } catch (Exception e) {
             e.printStackTrace();
         }
    }
    //takes in List the containers from the database.
    public List<String> getListContainersDatabase() {
         try {
             String query = "SELECT containerId FROM containers WHERE SystemIp = ?"; //query to get them
             PreparedStatement preparedStatement = connection.prepareStatement(query);
             preparedStatement.setString(1, ip);
             ResultSet resultSet = preparedStatement.executeQuery(); // execute the query
             List<String> databaseContainers = new ArrayList<>(); //List to return
             while (resultSet.next()) {
                 databaseContainers.add(resultSet.getString("containerId")); //adds element to the list
             }
             return databaseContainers; //return the list.
         } catch (Exception e) {
             e.printStackTrace();
             return null;
         }
    }
}
