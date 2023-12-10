package com.nullteam.Models;

import javax.persistence.*;

@Entity
public class Container {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id; //AYTOPARAGOMENO ... Primary Key
    @Column
    private String containerId;
    @Column
    private String name;
    @Column
    private String status;
    @Column
    private String image;

    public long getId() { //AYTOPARAGOMENO
        return this.id;
    }
    public void setId(long id) { //Not really needed
        this.id = id;
    }
    public String getContainerId() {
        return this.containerId;
    }
    public void setContainerId(String containerId) { //Docker Container ID does not change. Maybe -> implement
        this.containerId = containerId;
    }
    public String getStatus() {
        return this.status;
    }
    public void setStatus(String status) { //START STOP PAUSE UNPAUSE RESTART KILL ...
        this.status = status;
    }
    public String getName() {
        return this.name;
    }
    public void setName(String name) { //Rename
        this.name = name;
    }
    public String getImage() {
        return this.image;
    }
    public void setImage(String image) { //Not really needed
        this.image = image;
    }

}
