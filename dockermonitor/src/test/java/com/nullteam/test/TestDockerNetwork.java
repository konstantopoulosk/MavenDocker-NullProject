package com.nullteam.test;

import com.github.dockerjava.api.model.Network;
import com.nullteam.ClientUpdater;
import com.nullteam.DockerNetwork;
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
        Assert.assertEquals("Failure - wrong Network Id", dockerNetwork.networkId(), updatedNetworks.get(0).getId());
    }
    @Test
    public void testGetName() {
        Assert.assertEquals("Failure - wrong Name", dockerNetwork.name(), updatedNetworks.get(0).getName());
    }
    @Test
    public void testGetDriver() {
        Assert.assertEquals("Failure - wrong Driver", dockerNetwork.driver(), updatedNetworks.get(0).getDriver());
    }
    @Test
    public void testGetScope() {
        Assert.assertEquals("Failure - wrong Scope", dockerNetwork.scope(), updatedNetworks.get(0).getScope());
    }
    @Test
    public void testToString() {
        Assert.assertEquals("Failure wrong to String", dockerNetwork.toString(), updatedNetworks.toString());
    }


}
