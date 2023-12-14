package com.nullteam.test;
import java.io.InputStream;
import java.util.Properties;

public class DockerDatabase {
    InputStream inputStream =
            getClass().getResourceAsStream("/db.properties");
    Properties props = new Properties();
    props.load(inputStream);
}
