package com.nullteam;
import java.io.*;
import java.net.ConnectException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DatabaseThread extends  Thread {
    boolean fC = false;
    boolean fI = false;
    @Override
    public void run() {
        Connection connection = connectToDatabase();
        if (fI = true) {
            deleteImage(connection);
            readImagesfromCsv(connection);
        } else {
            readImagesfromCsv(connection);
        }
        if (fC = true) {
            deleteContainers(connection);
            readContainersfromCsv(connection);
        } else {
            readContainersfromCsv(connection);
        }
    }

    public static Connection connectToDatabase() {
        Connection connection;
        String imageId, repository, tag, timesUsed, size;
        String url = "jdbc:mysql://localhost:3306/dockerdb";
        String user = "root";
        String password = "nullteamtsipouroVolos123456789";
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            connection = DriverManager.getConnection(url,user,password);
            System.out.println("Successful Connection to Database!");
        } catch (ClassNotFoundException | SQLException e) {
            throw new RuntimeException(e);
        }
        return connection; //Connected to Database.
    }
    public void readImagesfromCsv(Connection connection) {
        String imageId, repository, tag, timesUsed, size;
        try {
            File file = new File("images.csv");
            FileReader fr = new FileReader(file);
            BufferedReader br = new BufferedReader(fr);
            String line = " ";
            String[] image;
            List<String> s = new ArrayList<>();
            while ((line = br.readLine()) != null) {
                image = line.split(",");
                imageId = image[0];
                repository = image[1];
                tag = image[2];
                timesUsed = image[3];
                size = image[4];
                String querryImage = "Insert into dockerimage(id,repository,tag,timesUsed,size) values("
                        + imageId + ',' + repository + ',' + tag + ',' + timesUsed + ',' + size + ')';
                Statement statement = connection.createStatement();
                statement.executeUpdate(querryImage);
            }
            br.close();
            this.fI = true;
        } catch (SQLException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void deleteImage(Connection connection) {
        try {
            File file = new File("images.csv");
            FileReader fr = new FileReader(file);
            BufferedReader br = new BufferedReader(fr);
            String line = " ";
            String[] image;
            List<String> s = new ArrayList<>();
            while ((line = br.readLine()) != null) {
                image = line.split(",");
                String deleteImage = "delete from dockerimage where id ="
                        + image[0];
                PreparedStatement preparedStatement =
                        connection.prepareStatement(deleteImage);
                preparedStatement.execute();
            }
        } catch (IOException | SQLException e) {
            throw new RuntimeException(e);
        }
    }
    public void readContainersfromCsv(Connection connection) {
        String containerID, name, image, state, Command, Created, Ports;
        try {
            File file = new File("containers.csv");
            FileReader fr = new FileReader(file);
            BufferedReader br = new BufferedReader(fr);
            String line = " ";
            String[] container;
            List<String> s = new ArrayList<>();
            while ((line = br.readLine()) != null) {
                container = line.split(",");
                containerID = container[0];
                name = container[1];
                image = container[2];
                state = container[3];
                Command = container[4];
                Created = container[5];
                Ports = container[6];
                String querryContainer = "Insert into dockerinstance(id,name,image,state,command,created,ports)"
                        +"values(" + containerID + "," + name + "," + image + "," + state + "," + Command + "," + Created
                        + "," + Ports + ")";
                Statement statement = connection.createStatement();
                statement.executeUpdate(querryContainer);
            }
            br.close();
            this.fC = true;
        } catch (IOException | SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static void deleteContainers(Connection connection) {
        try {
            File file = new File("containers.csv");
            FileReader fr = new FileReader(file);
            BufferedReader br = new BufferedReader(fr);
            String line = " ";
            String[] container;
            List<String> s = new ArrayList<>();
            while ((line = br.readLine()) != null) {
                container = line.split(",");
                String deleteImage = "delete from dockerinstance where id ="
                        + container[0];
                PreparedStatement preparedStatement =
                        connection.prepareStatement(deleteImage);
                preparedStatement.execute();
            }
        } catch (IOException | SQLException e) {
            throw new RuntimeException(e);
        }
    }
}


