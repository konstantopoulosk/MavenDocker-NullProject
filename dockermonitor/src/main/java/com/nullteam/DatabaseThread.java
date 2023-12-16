package com.nullteam;
import java.time.format.DateTimeFormatter;
import java.time.LocalDateTime;
import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;
import java.io.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class DatabaseThread extends Thread {
    static int images = 0;
    static int containers = 0;
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
            //Those are temporary.

            deleteAllRowsFromTable(connection, "dockerimage");
            deleteAllRowsFromTable(connection,"dockerinstance");
            deleteAllRowsFromTable(connection, "measurementsofcontainers");
            deleteAllRowsFromTable(connection,"measurementsofimages");
            if (checkTableEmpty(connection, "dockerimage")) {
                readImagesFromCsv(connection); //No duplicate primary keys
            }
            if (checkTableEmpty(connection, "dockerinstance")) {
                readContainersFromCsv(connection);
            }
            while (!Thread.currentThread().isInterrupted()) { //Runs until interrupted
                 //Table not empty we should update the rows. not insert something
                if (hasNewDataImages()) { //if new data -> do sth, else do nth
                    updateImages(connection);
                    //deleteAllRowsFromTable(connection, "dockerimage");
                    //readImagesFromCsv(connection);
                }
                    //Table not empty -> Update not insert
                if (hasNewDataContainers()) { //New data -> do sth
                    updateContainers(connection);
                    //deleteAllRowsFromTable(connection, "dockerinstance");
                    //readContainersFromCsv(connection);
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
                images++;
                addToMeasurementsOf(connection, "measurementsofimages", "idmi",images);
                String queryImage = String.format("INSERT INTO dockerimage (id, repository, tag, timesUsed, size, idmi) VALUES ('%s', '%s', '%s', '%s', '%s', '%s')", image[0], image[1], image[2], image[3], image[4], images);
                Statement statement = connection.createStatement();
                statement.executeUpdate(queryImage);
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
                containers++;
                addToMeasurementsOf(connection,"measurementsofcontainers", "idmc", containers);
                String queryContainer = String.format("INSERT INTO dockerinstance (id, name, image, state, command, created, ports, idmc) VALUES (\"%s\", \"%s\", \"%s\", \"%s\", \"%s\", \"%s\", \"%s\", \"%s\")", container[0], container[1], container[2], container[3], container[4], container[5], container[6], containers);
                Statement statement = connection.createStatement();
                statement.executeUpdate(queryContainer);
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
        boolean flag = true;
         while (flag) {
             try {
                 FileReader fr = new FileReader("containers.csv");
                 CSVReader csvReader = new CSVReader(fr);
                 currentStateContainer = new ArrayList<>();
                 String[] currentContainer;
                 while ((currentContainer = csvReader.readNext()) != null) {
                     currentStateContainer.add(currentContainer);
                 }
                 csvReader.close();
                 flag = false;
                 if (!DockerMonitor.listsAreEqual(currentStateContainer, lastStateContainer)) {
                     lastStateContainer = new ArrayList<>(currentStateContainer);
                     return true;
                 } else {
                     return false;
                 }
             } catch (CsvValidationException | IOException e) {
                 System.out.println("Something happened while checking for new data! Trying again...");
             }
         }
         return false;
    }
    public static void deleteAllRowsFromTable(Connection connection, String tableName) {
        try {
            String query = String.format("DELETE FROM %s", tableName);
            Statement statement = connection.createStatement();
            statement.executeUpdate(query);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    public static void addToMeasurementsOf(Connection connection, String tableName, String tableColumn, int i) {
        try {
            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
            LocalDateTime now = LocalDateTime.now();
            String query = String.format("INSERT INTO `%s` (`%s`, `date`) VALUES (\"%s\", \"%s\")", tableName, tableColumn , i, dtf.format(now));
            Statement statement = connection.createStatement();
            statement.executeUpdate(query);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    public boolean checkTableEmpty(Connection connection, String tableName) {
        try {
            String query = String.format("SELECT * FROM %s", tableName);
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (!resultSet.next()) {
                return true; // IS EMPTY
            } else {
                return false; //NOT EMPTY
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    //In order for this method to be called, 2 Lists are not equal!
    public void updateImages(Connection connection) {
        try {
            FileReader fr = new FileReader("images.csv");
            CSVReader csvReader = new CSVReader(fr);
            String[] image;
            int row = -1;
            while ((image = csvReader.readNext()) != null) {
                row++;
                List<Integer> positions;
                //Problem: positions is empty!!!
                positions = findDifferentValue(currentStateImage, lastStateImage, row);
                for (int i : positions) { //Multiple Changes
                    String queryImage = null;
                    Statement statement = connection.createStatement();
                    if (i == 3) { //Change to Times Used of an Image
                        queryImage = String.format("UPDATE dockerimage SET timesUsed = '%s' where id = '%s'", image[3], image[0]);
                        statement.executeUpdate(queryImage);
                    } else if (i == 4) { //Change to Size of an Image
                        queryImage = String.format("UPDATE dockerimage SET size = '%s' where id = '%s'", image[4], image[0]);
                        statement.executeUpdate(queryImage);
                    }
                    images++;
                    addToMeasurementsOf(connection,"measurementsofimages" , "idmi", images);
                    queryImage = String.format("UPDATE dockerimage SET idmi = '%s' where id = '%s'", images, image[0]);
                    statement.executeUpdate(queryImage);
                }
            }
            csvReader.close();
        } catch (SQLException | IOException | CsvValidationException e) {
            throw new RuntimeException(e);
        }
    }
    /*
    public void updateContainers(Connection connection) {
        try {
            FileReader fr = new FileReader("containers.csv");
            CSVReader csvReader = new CSVReader(fr);
            String[] container;
            int row = 1;
            while ((container = csvReader.readNext()) != null) {
                List<Integer> positions;
                positions = findDifferentValue(currentStateContainer, lastStateContainer, row);
                row++;
                for (int i : positions) { //Multiple Changes
                    String queryContainer = null;
                    if (i == 1) { //Change to Name of a Container
                        queryContainer = String.format("UPDATE dockerinstance SET name = '%s' where id = '%s'", container[1], container[0]);
                    } else if (i == 3) { //Change to State of a Container
                        queryContainer = String.format("UPDATE dockerinstance SET state = '%s' where id = '%s'", container[3], container[0]);
                    } else if (i == 4) { //Change to Command ?
                        queryContainer = String.format("UPDATE dockerinstance SET command = '%s' where id = '%s'", container[4], container[0]);
                    } else if (i == 6) { //Change to Ports ?
                        queryContainer = String.format("UPDATE dockerinstance SET ports = '%s' where id = '%s'", container[6], container[0]);
                    }
                    Statement statement = connection.createStatement();
                    statement.executeUpdate(queryContainer);
                    containers++;
                    addToMeasurementsOf(connection, "measurementsofcontainers","idmc" , containers);
                    queryContainer = String.format("UPDATE dockerinstance SET idmc = '%s' where id = '%s'", containers, container[0]);
                    statement.executeUpdate(queryContainer);
                }
            }
            csvReader.close();
        } catch (SQLException | IOException | CsvValidationException e) {
            throw new RuntimeException(e);
        }
    }

     */
    public void updateContainers(Connection connection) {
        try {
            FileReader fr = new FileReader("containers.csv");
            CSVReader csvReader = new CSVReader(fr);
            String[] container;
            int i = 0;
            while ((container = csvReader.readNext()) != null) {
                String[] last = lastStateContainer.get(i);
                for (String s : last) {
                    if (!last[Integer.parseInt(s)].equals(container[Integer.parseInt(s)])) {

                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public static List<Integer> findDifferentValue(List<String[]> current, List<String[]> last, int row) {
        List<Integer> positions = new ArrayList<>();

            String[] array1 = current.get(row);
            String[] array2 = last.get(row);
            for (int i = 0; i < array1.length; i++) {
                if (!array1[i].equals(array2[i])) {
                    positions.add(i);
                    System.out.println(i);
                }
            }

        return positions;
    }
}



