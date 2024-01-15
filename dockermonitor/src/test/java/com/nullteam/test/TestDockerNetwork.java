package com.nullteam.test;

import com.github.dockerjava.api.model.Network;
import com.nullteam.ClientUpdater;
import com.nullteam.DockerImage;
import com.nullteam.DockerNetwork;
import com.nullteam.Lists;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;


import java.util.ArrayList;
import java.util.List;

public class TestDockerNetwork {
    private static List<DockerNetwork> networkslist;
    private String id;
    private String name;
    private String driver;
    private String scope;
    private DockerNetwork testNetwork;
    @Before
    public void setUp() {
        networkslist = new ArrayList<>();
        Lists.listNetworks();
        testNetwork =  DockerNetwork.networkslist.getFirst();
        networkslist.add(testNetwork);
        id = testNetwork.getNetworkId();
        name = testNetwork.getName();
        driver = testNetwork.getDriver();
        scope = testNetwork.getScope();
    }
    @Test
    public void testGetNetworkId() {
        Assert.assertEquals("Failure - wrong Network Id",
                id, DockerNetwork.networkslist.getFirst().getNetworkId());
    }
    @Test
    public void testGetName() {
        Assert.assertEquals("Failure - wrong Name",
                name, DockerNetwork.networkslist.getFirst().getName());
    }
    @Test
    public void testGetDriver() {
        Assert.assertEquals("Failure - wrong Driver",
                driver, DockerNetwork.networkslist.getFirst().getDriver());
    }
    @Test
    public void testGetScope() {
        Assert.assertEquals("Failure - wrong Scope",
                scope, DockerNetwork.networkslist.getFirst().getScope());
    }
    @Test
    public void testToString() {
        Assert.assertEquals("Failure wrong to String",
                networkslist.getFirst().toString(),
                DockerNetwork.networkslist.getFirst().toString());
    }
    @Test
    public void testInspectContainersForSubnet() {
        System.out.println(DockerNetwork.inspectContainersForSubnet(id));
        //test how it looks o=in order to make more user-friendly in the program
    }
    @After
    public void tearDown() {
        networkslist = null;
    }
}
