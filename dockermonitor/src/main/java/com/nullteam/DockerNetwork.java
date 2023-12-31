package com.nullteam;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * @param networkId A field for the id of the network.
 * @param name      A field for the name of the network.
 * @param driver    A field for the network's driver,
 *                  which is a Software that activates the actual
 *                  transmission and receipt of data over the network.
 * @param scope     A field for the network's scope,
 *                  which is the extent or range of IP addresses
 *                  that a particular network or subnet
 *                  can encompass Network Scope.
 */
public record DockerNetwork(String networkId, String name, String driver, String scope) {
    /**
     * List of all the DockerNetwork objects,
     * all the networks in the DockerDesktop.
     */
    private static final List<DockerNetwork> networkslist = new ArrayList<>();

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
    @Override
    public String networkId() {
        return networkId;
    }

    /**
     * Gets the network name.
     *
     * @return String
     */
    @Override
    public String name() {
        return name;
    }

    /**
     * Gets the network driver.
     *
     * @return String
     */
    @Override
    public String driver() {
        return driver;
    }

    /**
     * Gets the network scope.
     *
     * @return String
     */
    @Override
    public String scope() {
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
        return "NetworkID: " + networkId() + " Name: "
                + name() + " Driver: " + driver()
                + " Scope: " + scope();
    }

    /**
     * This method prints all the networks inside the
     * user's Docker Desktop with their information
     * in a shorted list.
     */
    public static void showNetworks() {
        System.out.println("Listing all the networks...\n.\n.\n.");
        int num = 0; //Numbers to make the output more User Friendly
        for (DockerNetwork n : networkslist) {
            num++;
            System.out.println(num + ") " + n.toString());
        }
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
    public static StringBuilder formatSubnetsSettings(String inspectResult) {
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
