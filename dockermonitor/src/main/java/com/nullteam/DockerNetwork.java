package com.nullteam;

import com.github.dockerjava.api.model.Container;
import com.github.dockerjava.api.model.Network;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DockerNetwork {
    private static List<DockerNetwork> networkslist = new ArrayList<>();
    private final String networkId;
    private final String name;
    private final String driver;
    private final String scope;
    public DockerNetwork(final String networkId, final String name,
                         final String driver, final String scope) {
        this.networkId = networkId;
        this.name = name;
        this.driver = driver;
        this.scope = scope;
        networkslist.add(this);
    }
    public String getNetworkId() {
        return networkId;
    }
    public String getName() {
        return name;
    }
    public String getDriver() {
        return driver;
    }
    public String getScope() {
        return scope;
    }

    @Override
    public String toString() {
        return "NetworkID: " + getNetworkId() + " Name: "
                + getName() + " Driver: " + getDriver()
                + " Scope: " + getScope();
    }
    public static void showNetworks() {
        System.out.println("Listing all the networks...\n.\n.\n.");
        int num = 0; //Numbers to make the output more User Friendly
        for (DockerNetwork n : networkslist) {
            num++;
            System.out.println(num + ") " + n.toString());
        }
    }
    public static String inspectNetworkForContainers(final String networkName) {
        try {
            String[] command = {"docker", "inspect",
                    "--format='{{json .Containers}}'", networkName};
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
    public static StringBuilder formatSubnetsSettings(String inspectResult){
        //we don't need all the network settings, just the network info
        //we also remove the outer quotation marks ''
        //and the last bracket } because it is not needed
        String[] s1 = inspectResult.replace("'","")
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
                s = s.replace("{","{\n   ");
                result.append(s).append("\n");
            } else {
                result.append("   ").append(s).append("\n");
            }
        }
        return result;
    }
}

