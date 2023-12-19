/**
 * Package for our .java files
 */
package com.nullteam;
import org.glassfish.jersey.client.ClientAsyncExecutor;

import javax.ws.rs.client.Client;
import javax.xml.crypto.Data;
import java.io.IOException;
import java.lang.ref.Cleaner;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Scanner;
final class Main { //Utility classes should not be defined public
    private Main() { //Just Doing what CheckStyle says
    }
    public static void main(final String[] args) {
        ClientUpdater.connectionAccomplished();
        Scanner in = new Scanner(System.in);
        GetHelp.listImage();
        GetHelp.listContainers();
        DockerMonitor monitor = new DockerMonitor();
        monitor.start(); //Initialized Monitor Thread.
        Connection connection = ClientUpdater.connectToDatabase();
        ClientUpdater.dropTables(connection);
        ClientUpdater.createTables(connection);
        DatabaseThread databaseThread = new DatabaseThread(connection);
        databaseThread.start(); //Initialized Database Thread.
        System.out.println("Welcome!");
        for (;;) { //Initialized menu//
            Messages.mainMenu();
            String menu = in.nextLine(); //Which menu to show
            try {
                switch (menu) {
                    case "1"://Containers Menu
                        System.out.println("\nTransferring you" + " to the Container Menu ...");
                        flagCon: //Creating a loop to stay in the containers menu until user chooses to leave
                        while (true) {
                            Messages.containersMenu();
                            String ansC = in.nextLine();
                            try {
                                switch (ansC) {
                                    case "1":
                                        //printing ALL containers
                                        System.out.println("You chose:"
                                                + "1) View ALL the "
                                                + "containers\n");
                                        DockerInstance.listAllContainers();
                                        break;
                                    case "2": //printing ACTIVE containers-only
                                        System.out.println("You chose: "
                                                + "2) View ACTIVE "
                                                + "containers only\n");
                                        DockerInstance.listActiveContainers();
                                        break;
                                    case "3":
                                        GetHelp.goToContTools(); //Loop to
                                        flagTools: //stay in Container Tool menu
                                        while (true) { //until user
                                            Messages.containerTools();
                                            String ansT = in.nextLine();
                                            try {
                                                switch (ansT) {
                                                    case "1"://STOP Container
                                                        GetHelp.case1Stop();
                                                        break;
                                                    case "2"://START Container
                                                        GetHelp.case2Start();
                                                        break;
                                                    case "3"://RENAME Container
                                                        GetHelp.case3Rename();
                                                        break;
                                                    case "4"://REMOVE Container
                                                        GetHelp.case4Remove();
                                                        break;
                                                    case "5"://RESTART Container
                                                        GetHelp.case5Restart();
                                                        break;
                                                    case "6"://PAUSE Container
                                                        GetHelp.case6Pause();
                                                        break;
                                                    case "7"://UNPAUSE Container
                                                        GetHelp.case7Unpause();
                                                        break;
                                                    case "8"://KILL Container
                                                        GetHelp.case8Kill();
                                                        break;
                                                    case ".."://GO BACK
                                                        GetHelp.goToContMenu();
                                                        break flagTools;
                                                    case "*": //Exiting the app
                                                        ClientUpdater.closeConnection(connection);
                                                        Messages.exitApp();
                                                        break;
                                                    default:
                                                        GetHelp.thr(ansT);
                                                }
                                            } catch (IllegalStateException e) {
                                                GetHelp.repChoice();
                                            }
                                        }
                                        break;
                                    case "4": //Inspect a container (volumes-subnets-logs)
                                        GetHelp.goToInspectContainer();
                                        flagInspect: //stay in Inspect Container menu
                                        while (true) {
                                            Messages.inspectContainer();
                                            String ansI = in.nextLine();
                                            try {
                                                switch (ansI) {
                                                    case "1":
                                                        GetHelp.showVolumes();
                                                        break;
                                                    case "2":
                                                        //show subnets to be done
                                                        break;
                                                    case "3":
                                                        //show logs to be done
                                                        break;
                                                    case ".."://GO BACK
                                                        GetHelp.goToContMenu();
                                                        break flagInspect;
                                                    case "*": //Exiting the app
                                                        ClientUpdater.closeConnection(connection);
                                                        Messages.exitApp();
                                                        break;
                                                    default:
                                                        GetHelp.thr(ansI);
                                                }
                                            } catch (IllegalStateException e) {
                                                GetHelp.repChoice();
                                            } catch (IOException | InterruptedException e) {
                                                throw new RuntimeException(e);
                                            }
                                        }
                                        break;
                                    case "..": //going back to main menu...
                                        GetHelp.goToMainMenu();
                                        break flagCon;
                                    case "*": //Exiting the APP
                                        ClientUpdater.closeConnection(connection);
                                        Messages.exitApp();
                                        break;
                                    default:
                                        GetHelp.thr(ansC);
                                }
                            } catch (IllegalStateException e) {
                                GetHelp.repChoice();
                            }
                        }
                        break; //end of case 1
                    case "2": // Image menu
                        GetHelp.goToImMenu(); //Loop to stay
                        flagImage: //until user NotTo
                        while (true) {
                            Messages.imagesMenu();
                            String ansI = in.nextLine();
                            try {
                                switch (ansI) {
                                    case "1": //Listing All Images
                                        GetHelp.imageCase1();
                                        break;
                                    case "2": //Listing Images in use
                                        GetHelp.imageCase2();
                                        break;
                                    case "3": //pull an image
                                        GetHelp.case3Pull();
                                        break;
                                    case "4":  //implement an image
                                        GetHelp.case4Impl();
                                        break;
                                    case "5": //Remove an Image
                                        GetHelp.case5RmvImg();
                                        break;
                                    case "..": //going back to main menu...
                                        GetHelp.goToMainMenu();
                                        break flagImage;
                                    case "*": //Exiting the app
                                        ClientUpdater.closeConnection(connection);
                                        Messages.exitApp();
                                        break;
                                    default:
                                        GetHelp.thr(ansI);
                                }
                            } catch (IllegalStateException e) {
                                GetHelp.repChoice();
                            }
                        }
                        break; //end of case 2
                    case "*"://exiting APP!!!
                        ClientUpdater.closeConnection(connection);
                        Messages.exitApp();
                        break;
                    default:
                        GetHelp.thr(menu);
                }
            } catch (IllegalStateException e) {
                GetHelp.repChoice();
            }
        }
    }
}
