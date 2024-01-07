package com.nullteam;

public class ActionRequest {
    private String actionType;
    private String containerId;

    public ActionRequest() {
    }

    public ActionRequest(String actionType, String containerId) {
        this.actionType = actionType;
        this.containerId = containerId;
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

    // Additional methods, if needed
}
