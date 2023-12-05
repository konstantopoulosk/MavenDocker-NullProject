package com.nullteam;
import java.util.Scanner;
final class Messages {
    private Messages() {
    }
    public static void mainMenu() {
        System.out.println("        ~Main Menu~     \n");
        System.out.println("(1) Inspect Containers\n"
               + "(2) Inspect Images"
               + "\n(*) Exit"); //more functionality to be added
        System.out.print("YOUR CHOICE ---> ");
    }
    public static void containersMenu() {
        System.out.println("\n          ~Container Menu~"
               + "\n         ------------------");
        System.out.println("(1) View ALL the containers\n"
               + "(2) View ACTIVE containers only"
               + "\n(3) Container Tools\nPress (..) "
               + "to go Back(Main Menu)\nPress (*) to EXIT THE APP");
        System.out.print("YOUR CHOICE ---> ");
    }
    public static void containerTools() {
        System.out.println("\n          ~Container Tools~"
               + "\n         -------------------");
        System.out.println("(1) STOP a container\n(2) START a container\n"
               + "(3) RENAME a container\n(4) REMOVE a container"
               + "\n(5) RESTART a container\n(6) PAUSE a "
               + "container\n(7) UNPAUSE a container"
               + "\n(8) KILL a container\nPress (..) to go Back(Container Menu)"
               + "\nPress (*) to EXIT THE APP");
        System.out.print("YOUR CHOICE---> ");
    }
    public static void imagesMenu() {
        System.out.println("\n            ~Image Menu~"
               + "\n         ------------------");
        System.out.println("(1) View available images\n"
               + "(2) Implement an image(start a new container)"
               + "\nPress (..) to go Back(Main Menu)\n"
               + "Press (*) to EXIT THE APP");
        System.out.print("YOUR CHOICE ---> ");
    }
    public static void exitApp() {
        Scanner in = new Scanner(System.in);
        System.out.println("\nAre you sure you want to "
               + "exit? Answer Y or N (Yes/No)\n");
        System.out.print("Answer: ");
        String finalAnswer = in.nextLine();
        if (finalAnswer.equals("Y")) {
            System.out.println("\nBye..."
                   + "\n*******EXITING APP*******");
            System.exit(0);
        } else {
            System.out.println("Going Back to Main Menu ...");
        }
    }
}
