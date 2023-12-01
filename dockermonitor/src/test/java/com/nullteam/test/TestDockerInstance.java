package com.nullteam.test;

import com.nullteam.DockerImage;
import com.nullteam.DockerInstance;

import java.util.List;

public class TestDockerInstance {
    private static List<DockerInstance> allContainers;
    private final String containerId = "89938596d8d0";
    private final DockerImage image = new DockerImage("ee3b4d1239f1", "mongo", "Latest");
    private final String status = "Exited";
    private final String name = "GREGORY";

}
