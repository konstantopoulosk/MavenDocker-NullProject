package com.nullteam;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;
import org.bouncycastle.crypto.agreement.jpake.JPAKERound1Payload;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.sql.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
public class DatabaseThread extends Thread {

    /**
     * A field for the System Ip
     * to recognise the user.
     */
    static String ip;
    /**
     * A field for the auto-incremented
     * primary key of the measurements table.
     */
    static int containers = giveMeCount();
    /**
     * A Connection variable to connect to the database.
     */
    static Connection connection;
    /**
     * Constructor for DatabaseThread object
     * that makes the connection to the database.
     * @param connection Connection
     * @param ip String
     */
    public DatabaseThread(Connection connection, String ip) {
        DatabaseThread.connection = connection; //Because connectivity methods
        DatabaseThread.ip = ip;
    }

    /**
     * This method executes the thread.
     */
    @Override
    public void run() {
        if (containers != -1) {
            if (newUser(connection, ip)) { //Checks if he is a new user.
                System.out.println("New User!");
                //New user -> write his data.
                containers++;
                addToMeasurements(connection, containers);
                readContainersFromCsv(connection, containers);
            } else {
                updateDatabase();
            }
        } else {
            System.out.println("Exception happened according to the database!");
        }
    }
    /**
     * This method returns the last number of measurements,
     * to start from there.
     * @return int
     */
    public static int giveMeCount() {
         try {
             String query = "SELECT COUNT(*) FROM measurements";
             Connection c = takeCredentials();
             if (c != null) {
                 PreparedStatement preparedStatement = c.prepareStatement(query);
                 ResultSet resultSet = preparedStatement.executeQuery(); //execute the query
                 resultSet.next(); //get the number
                 int count = resultSet.getInt(1); // count rows = the last number of measurement
                 c.close();
                 return count;
             } else {
                 System.out.println("Connection is null. Please try again ...");
                 return -1;
             }
         } catch (SQLException e) {
             System.out.println("Caught Error: " + e.getMessage());
             return -1;
         } catch (Exception e) {
             System.out.println("Exception happened: " + e.getMessage());
             return -1;
         }
    }

    /**
     * This method gets the credentials of our database
     * from the README.md file and returns the connection.
     * @return Connection
     */
    public static Connection takeCredentials() {
         List<String> list = new ArrayList<>();
         try {
             File file = new File("README.md").getAbsoluteFile();
             Scanner reader = new Scanner(file);
             while (reader.hasNextLine()) {
                 String data = reader.nextLine();
                 list.add(data);
             }
             String driver = list.get(5);
             String url = list.get(7);
             String user = list.get(9);
             String password = list.get(11);
             return ClientUpdater.connectToDatabase(driver, url, user, password);
         } catch (FileNotFoundException e1) {
             System.out.println("Caught Error: " + e1.getMessage());
            return null;
         }
    }
    /**
     * This method reads the containers.csv and
     * inserts everything into the table containers.
     * @param connection Connection
     * @param containers int
     */
    public void readContainersFromCsv(Connection connection, int containers) {
        try {
            FileReader fr = new FileReader("containers.csv");
            CSVReader csvReader = new CSVReader(fr); //opens the csv
            String[] container;
            while ((container = csvReader.readNext()) != null) {
                if (!container[0].equals("Container ID")) { // Does not insert the header.
                    if (!searchInDatabase(container[0])) {
                        System.out.println("Do not exist");
                        String query = "INSERT INTO containers (containerId, name, image, state, SystemIp, id) " +
                                "VALUES (?, ?, ?, ?, ?, ?)";
                        PreparedStatement preparedStatement = connection.prepareStatement(query);
                        for (int i = 0; i < 4; i++) {
                            preparedStatement.setString(i + 1, container[i]); //easier with for loop.
                        } //fewer lines.
                        preparedStatement.setString(5, ip);
                        preparedStatement.setInt(6, containers);
                        //sets the ? with actual values.
                        preparedStatement.executeUpdate(); //executes the query
                    } else {
                        changeSystemIp(container[0]);
                    }
                }
            }
            csvReader.close(); //closes the csv
        } catch (SQLException s) {
            System.out.println("Caught Error: " + s.getMessage());
        } catch (FileNotFoundException e1) {
            System.out.println("File Not Found: " + e1.getMessage());
        } catch (CsvValidationException e2) {
            System.out.println("CSV Exception: " + e2.getMessage());
        } catch (IOException e) {
            System.out.println("IOException: " + e.getMessage());
        }
    }
    /**
     * This method adds measurements into the table.
     * @param connection Connection
     * @param containers int
     */
    public void addToMeasurements(Connection connection, int containers) {
        try {
            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd");
            LocalDate today = LocalDate.now(); // LOCAL DATE (NO TIME)
            String query = String.format("INSERT INTO measurements (`id`, `date`, `SystemIp`) " +
                    "VALUES (\"%s\", \"%s\", \"%s\")", containers, dtf.format(today), ip);
            Statement statement = connection.createStatement();
            statement.executeUpdate(query); //EXECUTING THE QUERY
        } catch (SQLException e) {
            System.out.println("SQL Exception happened: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("Exception Happened: " + e.getMessage());
        }
    }
    /**
     * This method checks if the user is
     * new or not with his System Ip.
     * @param connection Connection
     * @param ip String
     * @return boolean
     */
    public boolean newUser(Connection connection, String ip) {
         try {
             String query = "SELECT COUNT(*) FROM containers WHERE SystemIp = ?";
             PreparedStatement preparedStatement = connection.prepareStatement(query);
             preparedStatement.setString(1, ip);
             ResultSet resultSet = preparedStatement.executeQuery();
             resultSet.next();
             int count = resultSet.getInt(1);
             //count of the rows with SystemIp = ip
             return count <= 0; //not a new user. -> false
         } catch (Exception e) {
             System.out.println("Caught Error: " + e.getMessage());
             return false;
         }
    }
    /**
     * This method returns a list with the rows
     * from containers table but only the columns
     * containerId, name, image and state.
     * @return List&lt;String[]&gt;
     */
    public List<String[]> getEverythingFromDatabase() {
        try {
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
            System.out.println("Caught Error: " + e.getMessage());
            return null;
        }
    }
    /**
     * This method returns a list with the index of the .csv
     * file but only columns ID, Name, Image and State.
     * @return List&lt;String[]&gt;
     */
    public List<String[]> getEverythingFromCsv() {
        try {
            List<String[]> containersInCsv = new ArrayList<>();
            FileReader fr = new FileReader("containers.csv");
            CSVReader csvReader = new CSVReader(fr);
            String[] container;
            while ((container = csvReader.readNext()) != null) {
                // Do not include the header from csv into database table.
                if (!container[0].equals("Container ID")) {
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
            System.out.println("Exception Happened: " + e.getMessage());
            return null;
        }
    }
    /**
     * javadoc
     */
    public void changeSystemIp(String containerId) {
        try {
            String query = "UPDATE containers SET SystemIp = ? WHERE containerId = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, ip);
            preparedStatement.setString(2, containerId);
            preparedStatement.executeUpdate();
        } catch (Exception e) {
            System.out.println("Caught Error: " + e.getMessage());
        }
    }
    /**
     * This method changes the name of a specific container and
     * adds to table measurements because Rename is a measurement.
     * @param newName String
     * @param containerId String
     */
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
            System.out.println("Caught Error: " + e.getMessage());
        }
    }
    /**
     * This method changes the name of a specific container
     * adds to measurements because we consider it a measurement.
     * @param state String
     * @param containerId String
     */
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
            System.out.println("Caught Error: " + e.getMessage());
        }
    }
    /**
     * This method removes a container from the table
     * because user either selected remove container or remove image.
     * @param containerId String
     */
    public void removeContainer(String containerId) {
        try {
            String queryRemove = "DELETE FROM containers WHERE SystemIp = ? AND containerId = ?";
            PreparedStatement preparedStatementRemove = connection.prepareStatement(queryRemove);
            preparedStatementRemove.setString(1, ip);
            preparedStatementRemove.setString(2, containerId);
            preparedStatementRemove.executeUpdate(); //REMOVES FROM DATABASE IF CONTAINER
            //DOES NOT EXIST IN CSV.
        } catch (Exception e) {
            System.out.println("Caught Error: " + e.getMessage());
        }
    }
    /**
     * This method inserts a new container into the table
     * adds to measurements because it's a measurement
     * because user selected implement image.
     * @param id String
     * @param name String
     * @param state String
     * @param image String
     */
    public void implementContainer(String id, String name, String state, String image) {
        try {
            //THIS IS A MEASUREMENT ADD TO TABLE
            containers++;
            addToMeasurements(connection, containers);
            //WRITE CONTAINER IN DATABASE IMPLEMENT IMAGE -> START NEW CONTAINER
            String queryImplement = "INSERT INTO containers (containerId, name, image, state, SystemIp, id)" +
                    "VALUES (?, ?, ?, ?, ?, ?)";
            PreparedStatement preparedStatementImplement = connection.prepareStatement(queryImplement);
            preparedStatementImplement.setString(1, id); //ID
            preparedStatementImplement.setString(2, name); //NAME
            preparedStatementImplement.setString(3, image); //IMAGE
            preparedStatementImplement.setString(4, state); //STATE
            preparedStatementImplement.setString(5, ip);
            preparedStatementImplement.setInt(6, containers);
            preparedStatementImplement.executeUpdate();
        } catch (Exception e) {
            System.out.println("Caught Error: " + e.getMessage());
        }
    }
    /**
     * This method updates the old values of an
     * old User into new ones if there are any.
     */
    public void updateDatabase() {
        containers++;
        addToMeasurements(connection,containers);
        //Lists containing containers in DB and containers in CSV
        List<String[]> containersInDatabase = new ArrayList<>(getEverythingFromDatabase());
        List<String[]> containersInCsv = new ArrayList<>(getEverythingFromCsv());
         try {
             for (String[] containers : containersInCsv) {
                 String id = containers[0];
                 String name = containers[1];
                 String image = containers[2];
                 String state = containers[3];
                 if (searchInDatabase(id)) {
                     //EXISTS IN DATABASE
                     changeName(name, id);
                     changeState(state, id);
                 } else {
                     //NOT EXIST
                     implementContainer(id, name, state, image);
                 }
             }
             for (String[] c : containersInDatabase) {
                 String id = c[0];
                 if (!searchInCsv(id)) {
                     removeContainer(id);
                 }
             }
         } catch (Exception e) {
             System.out.println("Caught Error: " + e.getMessage());
         }
    }
    /**
     * This method searches if a container
     * exists in the .csv file based on its id.
     * @param id String
     * @return boolean
     */
    public boolean searchInCsv(String id) {
        try {
            boolean flag = false;
            List<String[]> containersInCsv = getEverythingFromCsv();
            for (String[] strings : containersInCsv) {
                String containerId = strings[0];
                if (id.equals(containerId)) {
                    flag = true;
                    break;
                }
            }
            return flag;
        } catch (Exception e) {
            System.out.println("Caught Error: " + e.getMessage());
            return false;
        }
    }
    /**
     * This method searches if a container
     * exists in the database based on its id.
     * @param id String
     * @return boolean
     */
    public boolean searchInDatabase(String id) {
         try {
             String query = "SELECT COUNT(*) FROM containers WHERE containerId = ? AND SystemIp = ?";
             PreparedStatement p = connection.prepareStatement(query);
             p.setString(1,id);
             p.setString(2, ip);
             ResultSet resultSet = p.executeQuery();
             resultSet.next();
             int count = resultSet.getInt(1);
             if (count > 0) {
                 return true; //Exists
             } else {
                 return false;
             }
         } catch (Exception e) {
             System.out.println("Caught Error: " + e.getMessage());
             return false;
         }
    }
}
