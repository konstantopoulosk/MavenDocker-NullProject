package com.nullteam;
import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.model.Container;
import com.github.dockerjava.api.model.Image;
import com.github.dockerjava.core.DefaultDockerClientConfig;
import com.github.dockerjava.core.DockerClientBuilder;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import javax.ws.rs.ProcessingException;
import java.io.File;
import java.awt.Desktop;
import java.util.Properties;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;
final class ClientUpdater {

    /**
     * Checks if the connection with the Docker Client was Accomplished
     * else an exception is thrown so that the program can handle it and
     * open the Docker Desktop App
     */
    public static void connectionAccomplished() {
        boolean connected = true;
        try {
            getUpdatedClient();
        } catch(ProcessingException e) {
            System.out.println("ERROR!: Couldn't connect to the client...");
            System.out.println("\n.\n.\n.WAITING FOR DOCKER DESKTOP.EXE");
            File file = new File("C:\\Program Files\\Docker\\Docker\\Docker Desktop.exe");
            try {
                Desktop.getDesktop().open(file); // opening the docker.exe
                Process process = Runtime.getRuntime().exec("cmd /c start \"\" \"" +
                        "C:\\Program Files\\Docker\\Docker\\Docker Desktop.exe");
                int exitCode = process.waitFor();
                Thread.sleep(30000);
                if (exitCode == 0) {
                    System.out.println("File or application opened successfully.");
                } else {
                    System.out.println("Failed to open file or application.");
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    /**
     * This method gets a list of updated containers from
     * the updated Docker Client in order to always have
     * the correct list after any change made by the user.
     * @return List&lt;Container&gt;
     */

    public static List<Container> getUpdatedContainersFromClient() {
        DockerClient dockerClient = getUpdatedClient(); //Method Below
        List<Container> containers; // instances
        containers = dockerClient.listContainersCmd()
                .withShowAll(true).exec(); //all containers from cluster
        try {
            dockerClient.close();
        } catch (IOException e) {
            System.out.println("Failed to close the client");
        }
        return containers; //Updated Containers
    }
    /**
     * This method gets a list of updated images from
     * the updated Docker Client in order to always have
     * the correct list after any change made by the user.
     * @return List&lt;Image&gt;
     */
    public static List<Image> getUpdatedImagesFromClient() {
        DockerClient client = getUpdatedClient(); //Method Below
        List<Image> images
                = client.listImagesCmd().exec(); //Images from the cluster
        try {
            client.close();
        } catch (IOException e) {
            System.out.println("Failed to close the client");
        }
        return images; //Updated Images
    }
    /**
     * This method gets the Updated Docker Client.
     * It is used after a change, for example Start / Stop / Remove
     * @return DockerClient
     */
    public static DockerClient getUpdatedClient() {
        DefaultDockerClientConfig.Builder builder
                = DefaultDockerClientConfig.createDefaultConfigBuilder();
        builder.withDockerHost("tcp://localhost:2375");
        DockerClient dockerClient = DockerClientBuilder
                .getInstance(builder).build();
        dockerClient.versionCmd().exec();
        return dockerClient; //The same docker client but UPDATED!
    }
    /**
     * This method updates the status every time in order
     * to monitor the time the container has been Up or Exited.
     * @param containerId String
     * @return String
     */
    public static String getUpdatedStatus(final String containerId) {
        String status = null;
        List<Container> containers
                = getUpdatedContainersFromClient(); //updated containers
        for (Container c : containers) {
            if (c.getId().equals(containerId)) { //finds the desired container
                status = c.getStatus(); //changes the status
            }
        }
        return status;
    }
    public static Connection connectToDatabase() {
        Connection connection = null;
        // Load environment variables
        /*
        String dbHost = System.getenv("DATABASE_HOST");
        String dbUsername = System.getenv("DATABASE_USERNAME");
        String dbPassword = System.getenv("DATABASE_PASSWORD");
        String dbName = System.getenv("DATABASE");

        // JDBC connection properties
        Properties props = new Properties();
        props.setProperty("user", dbUsername);
        props.setProperty("password", dbPassword);
        props.setProperty("useSSL", "true"); // Enable SSL
        */
        try {
            // Connect to the database
           // String url = "jdbc:mysql://" + dbHost + "/" + dbName;
            Class.forName("com.mysql.cj.jdbc.Driver");
            connection = DriverManager.getConnection(
                    "jdbc:mysql://gcp.connect.psdb.cloud/dockerdb?sslMode=VERIFY_IDENTITY",
                    "19ngi5chkrn64cuca1kb",
                    "pscale_pw_ewro2owyRnOB9mRiThiMveV4V8xUmC0F0kMjWtWoUtc");
            System.out.println("Successful connection to the Database!");
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        return connection;
    }
    public static void createTables(Connection connection) {
        try {
            Statement statement = connection.createStatement();
            String query = String.format("CREATE TABLE measurementsofcontainers ("
                    + "idmc INT NOT NULL,"
                    + "date DATETIME NOT NULL,"
                    + "PRIMARY KEY(idmc))");
            statement.executeUpdate(query);
            query = String.format("CREATE TABLE measurementsofimages ("
                    + "idmi INT NOT NULL,"
                    + "date DATETIME NOT NULL,"
                    + "PRIMARY KEY(idmi))");
            statement.executeUpdate(query);
            query = String.format("CREATE TABLE dockerinstance (" +
                    "id VARCHAR(64) NOT NULL," +
                    "name VARCHAR(100)," +
                    "image VARCHAR(100) NOT NULL," +
                    "state VARCHAR(20) NOT NULL," +
                    "command VARCHAR(100) NOT NULL," +
                    "created VARCHAR(20) NOT NULL," +
                    "ports VARCHAR(10) NOT NULL," +
                    "idmc INT," +
                    "PRIMARY KEY (id)," +
                    "CONSTRAINT fk_idmc FOREIGN KEY (idmc) REFERENCES measurementsofcontainers (idmc))");
            statement.executeUpdate(query);
            query = String.format("CREATE TABLE dockerimage (" +
                    "    id VARCHAR(71) NOT NULL PRIMARY KEY," +
                    "    repository VARCHAR(100) NOT NULL," +
                    "    tag VARCHAR(100)," +
                    "    timesUsed VARCHAR(15)," +
                    "    size VARCHAR(10)," +
                    "    idmi INT NOT NULL," +
                    "CONSTRAINT j FOREIGN KEY (idmi) REFERENCES measurementsofimages (idmi))");
            statement.executeUpdate(query);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    public static void dropTables(Connection connection) {
        try {
            Statement statement = connection.createStatement();
            String query = String.format("DROP TABLE measurementsofcontainers");
            statement.executeUpdate(query);
            query = String.format("DROP TABLE measurementsofimages");
            statement.executeUpdate(query);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
