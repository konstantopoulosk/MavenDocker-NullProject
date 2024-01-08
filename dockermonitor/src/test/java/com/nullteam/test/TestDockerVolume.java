package com.nullteam.test;

import com.github.dockerjava.api.command.InspectVolumeResponse;
import com.nullteam.ClientUpdater;
import com.nullteam.DockerVolume;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class TestDockerVolume {
    private static List<DockerVolume> volumesList;
    private List<InspectVolumeResponse> updatedVolumes;
    private DockerVolume dockerVolume;

    @Before
    public void setUp() {
        updatedVolumes = ClientUpdater.getUpdatedVolumesFromClient();
        volumesList = new ArrayList<>();

        for (InspectVolumeResponse volumeResponse : updatedVolumes) {
            DockerVolume dockerVolume = new DockerVolume(
                    volumeResponse.getDriver(),
                    volumeResponse.getName(),
                    DockerVolume.createdAt(volumeResponse.getName()),
                    volumeResponse.getMountpoint()
            );
            volumesList.add(dockerVolume);
        }

        dockerVolume = volumesList.get(0);
    }

    @Test
    public void testGetDriver() {
        Assert.assertEquals("Failure - wrong Driver", dockerVolume.driver(), updatedVolumes.get(0).getDriver());
    }

    @Test
    public void testGetName() {
        Assert.assertEquals("Failure - wrong Name", dockerVolume.name(), updatedVolumes.get(0).getName());
    }

    @Test
    public void testCreated() {
        Assert.assertEquals("Failure - wrong Created", dockerVolume.created(), updatedVolumes.get(0).getCreatedAt());
    }

    @Test
    public void testGetMountpoint() {
        Assert.assertEquals("Failure - wrong Mountpoint", dockerVolume.mountpoint(), updatedVolumes.get(0).getMountpoint());
    }

    @Test
    public void testToString() {
        Assert.assertEquals("Failure wrong to String", dockerVolume.toString(), dockerVolume.toString());
    }

    @After
    public void tearDown() {
        volumesList = null;
    }
}
