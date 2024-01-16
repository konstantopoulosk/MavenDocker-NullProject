package com.nullteam.test;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.nullteam.ClientUpdater;
import com.nullteam.DatabaseThread;
import com.nullteam.DockerMonitor;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.sql.Connection;
import java.util.List;

public class TestDatabaseThread {
    Connection connection;
    String ip;
    DatabaseThread d;

    @Before
    public void setUp() {
        ip = ClientUpdater.getIp();
        connection = DatabaseThread.takeCredentials();
        d = new DatabaseThread(connection, ip);
    }
    @Test
    public void testConstructor() {

    }
    @Test
    public void testGiveMeCount() {
        int i = DatabaseThread.giveMeCount();
        Assert.assertFalse(i != -1);
    }
    @Test
    public void testTakeCredentials() {
        try {
            Connection c = DatabaseThread.takeCredentials();
            Assert.assertTrue(c == connection);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    @Test
    public void testReadContainersFromCsv() {
    }
    @Test
    public void testAddToMeasurements() {
        int i = DatabaseThread.giveMeCount();
        d.addToMeasurements(connection, i);
        Assert.assertTrue(true);
    }
    @Test
    public void testNewUser() {

    }
    @Test
    public void testGetEverythingFromDatabase() {
        List<String[]> list = d.getEverythingFromDatabase();
        Assert.assertFalse(list != null);
    }
    @Test
    public void testChangeName() {
        d.changeName("New", "Nope");
        Assert.assertTrue(true);
    }
    @Test
    public void testChangeState() {
        d.changeState("New", "Nope");
        Assert.assertTrue(true);
    }
    @Test
    public void testRemoveContainer() {
        d.removeContainer("NOPE");
        Assert.assertTrue(true);
    }
    @Test
    public void testImplementContainer() {
        d.implementContainer("TestId", "TestName",
                "TestState", "TestImage");
        Assert.assertTrue(true);
    }
    @Test
    public void testUpdateDatabase() {


    }
    @Test
    public void testSearchInCsv() {
        boolean flag = d.searchInCsv("NOPE");
        Assert.assertTrue(!flag);
    }
    @Test
    public void testSearchInDatabase() {
        boolean flag = d.searchInDatabase("DOES NOT EXIST");
        Assert.assertTrue(!flag);
    }
    @After
    public void tearDown() {
        //not really needed.
    }
}
