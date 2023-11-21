package com.nullteam;
import java.util.Scanner;


public class ExecutorThread implements Runnable {
    Modelisation m;
    String id;
    String task;

    public ExecutorThread(Modelisation m, String id, String task) {
        this.m = m;
        this.id = id;
        this.task = task;
    }
    Scanner in = new Scanner(System.in); //xreiazetai gia to rename
    @Override
    public void run() { //TOADD STRING ID KAI STRING TASK
        switch(this.task) {
            case "ST": //start
                this.m.getDockerClient().startContainerCmd(this.id);
          //    this.m.getDockerClient().startContainerCmd(this.id).exec();   TA VRHKA KAI ME TO .EXEC() STO GOOGLE ... LOGIKA DEN XREIAZETAI
                break;
            case "RN": //rename
                System.out.print("New container ID: ");
                //String newId = in.nextLine();//
                this.m.getDockerClient().renameContainerCmd(id);
                break;
            case "RM": //remove
                this.m.getDockerClient().removeContainerCmd(id);
                break;           
            case "RS": //restart
                this.m.getDockerClient().restartContainerCmd(id);
                break;
            case "P": //pause
                this.m.getDockerClient().pauseContainerCmd(id);
                break;            
            case "U": //unpause
                this.m.getDockerClient().unpauseContainerCmd(id);
                break;
            case "SP": //stop
                this.m.getDockerClient().stopContainerCmd(id); // kill after 10 seconds
                break;           
            case "K": //kill
                this.m.getDockerClient().killContainerCmd(id);
                break;
        }
    }
}
