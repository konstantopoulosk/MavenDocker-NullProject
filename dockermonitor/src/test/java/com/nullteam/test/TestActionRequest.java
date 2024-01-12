package com.nullteam.test;

import com.nullteam.*;
import com.google.gson.Gson;
import com.nullteam.*;
import com.sun.net.httpserver.HttpServer;
import org.junit.*;

import javax.swing.*;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static org.junit.Assert.assertEquals;

public class TestActionRequest {
    private String actionType;
    private String containerId;
    private String newName;
    @Before
    public void setUp() {
        actionType = "Action Type";
        containerId = "Container Id";
        newName = "New Name";
    }
    @Test
    public void testConstructor() {
        ActionRequest a2 = new ActionRequest(actionType, containerId);
        Assert.assertTrue(a2.getActionType().equals(actionType));
        Assert.assertTrue(Objects.equals(a2.getNewName(), ""));
        Assert.assertTrue(a2.getContainerId().equals(containerId));
        ActionRequest a1 = new ActionRequest();
        Assert.assertTrue(a1.getContainerId() == null);
        Assert.assertTrue(a1.getNewName().equals(""));
        Assert.assertTrue(a1.getActionType() == null);
        ActionRequest a3 = new ActionRequest(actionType, containerId, newName);
        Assert.assertTrue(a3.getNewName().equals(newName));
    }
    @Test
    public void testGetActionType() {
        ActionRequest a2 = new ActionRequest(actionType, containerId);
        Assert.assertTrue(a2.getActionType().equals(actionType));
    }
    @Test
    public void testSetActionType() {
        ActionRequest a2 = new ActionRequest(actionType, containerId);
        a2.setActionType("NULL");
        Assert.assertTrue(a2.getActionType().equals("NULL"));
    }
    @Test
    public void testGetContainerId() {
        ActionRequest a2 = new ActionRequest(actionType, containerId);
        Assert.assertTrue(a2.getContainerId().equals(containerId));
    }
    @Test
    public void testSetContainerId() {
        ActionRequest a2 = new ActionRequest(actionType, containerId);
        a2.setContainerId("NULL");
        Assert.assertTrue(a2.getContainerId().equals("NULL"));
    }
    @Test
    public void testGetNewName() {
        ActionRequest a3 = new ActionRequest(actionType, containerId, newName);
        Assert.assertTrue(a3.getNewName().equals(newName));
    }
    @After
    public void tearDown() {

    }
}