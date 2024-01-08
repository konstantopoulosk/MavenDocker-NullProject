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
    final String driver;
    final String name;
    final String created;
    final String mountpoint;
    private static final List<DockerVolume> volumeslist = new ArrayList<>();

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
     * We use it to show every volum's information
     * (driver, name, created, mountpoint)
     *
     * @return String
     */
    @Override
    public String toString() {
        return "Driver: " + getDriver() + " Name: "
                + getName() + " CreatedAt: " + getCreated()
                + "   Mountpoint: " + getMountpoint();
    }

    /**
     * This method prints all the volumes inside the
     * user's Docker Desktop with their information
     * in a shorted list.
     */
    public static void showVolumes() {
        System.out.println("Listing all the volumes...\n.\n.\n.");
        int num = 0; //Numbers to make the output more User Friendly
        for (DockerVolume v : volumeslist) {
            num++;
            System.out.println(num + ") "
                    + v.toString() + "\n"); //toString inside a for loop
        }
    }

    /**
     * This method returns the result of the command:
     * docker volume inspect --format='{{json .CreatedAt}}' [VOLUME]
     * without the quotation marks.
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
                    .replace("\"'", "");
        } catch (Exception e) {
            e.printStackTrace();
            return "Exception!";
        }
    }
}
