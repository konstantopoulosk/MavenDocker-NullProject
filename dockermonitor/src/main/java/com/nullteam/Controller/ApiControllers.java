package com.nullteam.Controller;

import com.nullteam.Models.Container;
import com.nullteam.Repo.ContainerRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class ApiControllers {
    @Autowired
    private ContainerRepo containerRepo;
    @GetMapping(value = "/")
    public String getPage() {
        return "Ep";
    }
    @GetMapping(value = "/users")
    public List<Container> getContainers() {
        return containerRepo.findAll();
    }
    @PostMapping(value = "/save") //whenever you save sth to the DB it is a POST
    public String saveContainer(@RequestBody Container container) {
        containerRepo.save(container);
        return "Saved...";
    }
    @PutMapping(value = "update/{id}")
    public String updateUser(@PathVariable long id, @RequestBody Container container) {
        Container updatedContainer = containerRepo.findById(id).get();
        updatedContainer.setContainerId(container.getContainerId());
        updatedContainer.setName(container.getName());
        updatedContainer.setStatus(container.getStatus());
        updatedContainer.setImage(container.getImage());
        containerRepo.save(updatedContainer);
        return "Updated...";
    }
    @DeleteMapping(value = "/delete/{id}")
    public String deleteContainer(@PathVariable long id) {
        Container deleteContainer = containerRepo.findById(id).get();
        containerRepo.delete(deleteContainer);
        return "Delete container with id: " + id;
    }
}
