package com.nullteam;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
public class DockerNetwork {
    /**
     * List of all the DockerNetwork objects,
     * all the networks in the DockerDesktop.
     */
    public static final List<DockerNetwork> networkslist = new ArrayList<>();
    /**
     * A field for the network's ID.
     */
    private final String networkId;
    /**
     * A field for the network's name.
     */
    private final String name;
    /**
     * A field for the network's driver.
     */
    private final String driver;
    /**
     * A field for the network's scope.
     */
    private final String scope;

    /**
     * Constructor of Class DockerNetwork.
     * It creates a new DockerNetwork object
     * and adds it to the networkslist.
     *
     * @param networkId String
     * @param name      String
     * @param driver    String
     * @param scope     String
     */
    public DockerNetwork(final String networkId, final String name,
                         final String driver, final String scope) {
        this.networkId = networkId;
        this.name = name;
        this.driver = driver;
        this.scope = scope;
        networkslist.add(this);
    }
    /**
     * Gets the network ID.
     *
     * @return String
     */
    public String getNetworkId() {
        return networkId;
    }

    /**
     * Gets the network name.
     *
     * @return String
     */
    public String getName() {
        return name;
    }

    /**
     * Gets the network driver.
     *
     * @return String
     */
    public String getDriver() {
        return driver;
    }

    /**
     * Gets the network scope.
     *
     * @return String
     */
    public String getScope() {
        return scope;
    }

    /**
     * A classic toString method.
     * We use it to show every network's information
     * (id, name, driver, scope)
     *
     * @return String
     */
    @Override
    public String toString() {
        return "NetworkID: " + getNetworkId() + "\n    Name: "
                + getName() + "   Driver: " + getDriver()
                + "   Scope: " + getScope();
    }

    /**
     * This method returns the result of the command:
     * docker inspect --format='{{json .NetworkSettings}}' [CONTAINER].
     *
     * @param containerId String
     * @return String
     */
    public static String inspectContainersForSubnet(final String containerId) {
        try {
            String[] command = {"docker", "inspect",
                    "--format='{{json .NetworkSettings}}'", containerId};
            Process process = Runtime.getRuntime().exec(command);
            process.waitFor();

            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(process.getInputStream()));

            String line;
            StringBuilder sb = new StringBuilder();
            while ((line = reader.readLine()) != null) {
                sb.append(line).append("\n");
            }
            return sb.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return "Exception!";
        }
    }

    /**
     * This method takes the output of the command:
     * docker inspect --format='{{json .NetworkSettings}}' [CONTAINER]
     * and with some changes makes it more readable
     * and user-friendly.
     *
     * @param inspectResult String
     * @return StringBuilder
     */
    public static StringBuilder formatSubnetsSettings(
            final String inspectResult) {
        //we don't need all the network settings, just the network info
        //we also remove the outer quotation marks ''
        //and the last bracket } because it is not needed
        String[] s1 = inspectResult.replace("'", "")
                .replace("}}}", "\n   }\n}")
                .split("\"Networks\":");
        //now we split the lines for a more readable result
        String[] s2 = s1[1].split(",");
        StringBuilder result = new StringBuilder("Networks:");
        for (String s : s2) {
            if (s.contains("\"\"")) { //to show it's null
                s = s.replace("\"\"", "null");
            }
            if (s.contains("{")) { //to change line
                s = s.replace("{", "{\n   ");
                result.append(s).append("\n");
            } else {
                result.append("   ").append(s).append("\n");
            }
        }
        return result;
    }
}
