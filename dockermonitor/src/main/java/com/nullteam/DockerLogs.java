package com.nullteam;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class DockerLogs {
    public DockerLogs(){};

    /**
     * This method returns the result of the command:
     * docker logs [CONTAINER].
     *
     * @param containerId final String
     * @return String
     */
    public static String showAllLogsOfContainer(final String containerId) {
            String[] command = {"docker", "logs", containerId};
            return writingProcessOrExceptions(command);
    }
    /**
     * This method returns the result of the command:
     * docker logs --SINCE [DATE] [CONTAINER].
     * The date must have the following form:
     * YYY-MM-DD
     *
     * @param date String
     * @param containerId final String
     * @return String
     */
    public static String showLogsOfContainerSince(String date, final String containerId) {
        String[] command = {"docker", "logs", "--since", date, containerId};
        return writingProcessOrExceptions(command);
    }
    //to avoid repetition of code

    /**
     * This method is used to avoid repetition
     * of code in the previous two methods.
     * Its basically gives us the result of
     * each command and handles possible exceptions.
     *
     * @param command String[]
     * @return String
     */
    public static String writingProcessOrExceptions(String[] command) {
        try {
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
    /*
    public static StringBuilder formatLogList(String logs) {
        String[] logsList = logs.split(); //todo
    }
    */
}
