package com.nullteam.test;

import com.github.dockerjava.api.model.Network;
import com.nullteam.ClientUpdater;
import com.nullteam.DockerNetwork;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;


import java.util.ArrayList;
import java.util.List;

public class TestDockerNetwork {
    private static List<DockerNetwork> networkslist;
    private List<Network> updatedNetworks;
    private DockerNetwork dockerNetwork;
    @Before
    public void setUp() {
        updatedNetworks = ClientUpdater.getUpdatedNetworksFromClient();
        networkslist = new ArrayList<>();

        for (Network networks : updatedNetworks) {
            DockerNetwork dockerNetwork = new DockerNetwork(
                    networks.getId(),
                    networks.getName(),
                    networks.getDriver(),
                    networks.getScope()
            );
            networkslist.add(dockerNetwork);
        }

        dockerNetwork = networkslist.get(0);
    }
    @Test
    public void testGetNetworkId() {
        Assert.assertEquals("Failure - wrong Network Id", dockerNetwork.getNetworkId(), updatedNetworks.get(0).getId());
    }
    @Test
    public void testGetName() {
        Assert.assertEquals("Failure - wrong Name", dockerNetwork.getName(), updatedNetworks.get(0).getName());
    }
    @Test
    public void testGetDriver() {
        Assert.assertEquals("Failure - wrong Driver", dockerNetwork.getDriver(), updatedNetworks.get(0).getDriver());
    }
    @Test
    public void testGetScope() {
        Assert.assertEquals("Failure - wrong Scope", dockerNetwork.getScope(), updatedNetworks.get(0).getScope());
    }
    @Test
    public void testToString() {
        String name = "NetworkID: " + updatedNetworks.get(0).getId() + " Name: "
                + updatedNetworks.get(0).getName() + " Driver: " +updatedNetworks.get(0).getDriver()
                + " Scope: " + updatedNetworks.get(0).getScope();
        Assert.assertEquals("Failure wrong to String", dockerNetwork.toString(), name);
    }
    @After
    public void tearDown() {
        //Not really needed.
    }

}
