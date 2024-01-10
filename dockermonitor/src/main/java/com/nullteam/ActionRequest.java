package com.nullteam;

public class ActionRequest {
    private String actionType;
    private String containerId;
    private String newName = "";

    public ActionRequest() {
    }

    public ActionRequest(String actionType, String containerId) {
        this.actionType = actionType;
        this.containerId = containerId;
    }
    //alternative contructor for rename usage
    public ActionRequest(String actionType, String containerId, String newName) {
        this.actionType = actionType;
        this.containerId = containerId;
        this.newName = newName;
    }

    public String getActionType() {
        return actionType;
    }

    public void setActionType(String actionType) {
        this.actionType = actionType;
    }

    public String getContainerId() {
        return containerId;
    }

    public void setContainerId(String containerId) {
        this.containerId = containerId;
    }

    public String getNewName() {
        return newName;
    }
// Additional methods, if needed
}
