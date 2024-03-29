package com.nullteam;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class DockerVolume {
    /**
     * List of all the DockerVolume objects,
     * all the volumes in the DockerDesktop.
     */
    public static final List<DockerVolume> volumeslist = new ArrayList<>();
    /**
     * A field for the volume's driver.
     */
    private final String driver;
    /**
     * A field for the volume's name.
     */
    private final String name;
    /**
     * A field for the date and time
     * the volume was created at.
     */
    private final String created;
    /**
     * A field for the mountpoint where
     * the volume is stashed in the user's computer.
     */
    private final String mountpoint;
    /**
     * Constructor for Class DockerVolume.
     * It creates a new DockerImageVolume object
     * and adds it to the volumeslist.
     *
     * @param driver     String
     * @param name       String
     * @param created    String
     * @param mountpoint String
     */
    public DockerVolume(final String driver, final String name,
                        final String created, final String mountpoint) {
        this.driver = driver;
        this.name = name;
        this.created = created;
        this.mountpoint = mountpoint;
        volumeslist.add(this);
    }

    /**
     * Gets the driver of the volume.
     *
     * @return String
     */
    public String getDriver() {
        return driver;
    }

    /**
     * Gets the name of the volume.
     *
     * @return String
     */
    public String getName() {
        return name;
    }

    /**
     * Gets the datetime the volume
     * was created at.
     *
     * @return String
     */
    public String getCreated() {
        return created;
    }

    /**
     * Gets the mountpoint of the volume.
     *
     * @return String
     */
    public String getMountpoint() {
        return mountpoint;
    }

    /**
     * A classic toString method.
     * We use it to show every volume's information
     * (driver, name, created, mountpoint)
     *
     * @return String
     */
    @Override
    public String toString() {
        return "Driver: " + getDriver() + "\n    Name: "
                + getName() + "\n    CreatedAt: " + getCreated()
                + "    Mountpoint: " + getMountpoint();
    }

    /**
     * This method returns the result of the command:
     * docker volume inspect --format='{{json .CreatedAt}}' [VOLUME]
     * without the quotation marks and the letters T and Z
     * that appeared.
     *
     * @param volumeName String
     * @return String
     */
    public static String createdAt(final String volumeName) {
        try {
            String[] command = {"docker", "volume", "inspect",
                    "--format='{{json .CreatedAt}}'", volumeName};
            Process process = Runtime.getRuntime().exec(command);
            process.waitFor();

            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(process.getInputStream()));

            String line;
            StringBuilder sb = new StringBuilder();
            while ((line = reader.readLine()) != null) {
                sb.append(line).append("\n");
            }
            return sb.toString().replace("'\"", "")
                    .replace("\"'", "")
                    .replace("T", " ")
                    .replace("Z", " ");
        } catch (Exception e) {
            System.out.println("Caught Error: " + e.getMessage());
            return "Exception!";
        }
    }
}
