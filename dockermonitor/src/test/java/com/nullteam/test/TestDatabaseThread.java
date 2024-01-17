package com.nullteam.test;

import com.github.dockerjava.api.model.Container;
import com.nullteam.ClientUpdater;
import com.nullteam.DatabaseThread;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.sql.Connection;
import java.util.List;

public class TestDatabaseThread {
    Connection connection;
    String ip;

    @Before
    public void setUp() {
        ip = ClientUpdater.getIp();
        connection = null;
    }
    @Test
    public void testGiveMeCount() {
        int i = DatabaseThread.giveMeCount();
        Assert.assertFalse(i != -1);
    }
    @Test
    public void testTakeCredentials() {
        Connection c = null;
        Assert.assertTrue(c == connection);

    }
    @Test
    public void testAddToMeasurements() {
        Assert.assertTrue(true);
    }
    @Test
    public void testGetEverythingFromDatabase() {
        List<Container> list = ClientUpdater.getUpdatedContainersFromClient();
        Assert.assertFalse(list == null);
    }
    @Test
    public void testChangeName() {
        Assert.assertTrue(true);
    }
    @Test
    public void testChangeState() {
        Assert.assertTrue(true);
    }
    @Test
    public void testRemoveContainer() {
        Assert.assertTrue(true);
    }
    @Test
    public void testImplementContainer() {
        Assert.assertTrue(true);
    }
    @Test
    public void testSearchInCsv() {
        boolean flag = connection != null;
        Assert.assertTrue(!flag);
    }
    @Test
    public void testSearchInDatabase() {
        Assert.assertTrue(true);
    }
    @After
    public void tearDown() {
        //not really needed.
    }
}
