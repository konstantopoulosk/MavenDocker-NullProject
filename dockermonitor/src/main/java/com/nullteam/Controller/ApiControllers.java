
package com.nullteam.Controller;
/*
import com.nullteam.Models.Container;
import com.nullteam.Models.Images;
import com.nullteam.Repo.ContainerRepo;
import com.nullteam.Repo.ImageRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class ApiControllers {
    @Autowired
    private ContainerRepo containerRepo;
    @Autowired
    private ImageRepo imageRepo;
    @GetMapping(value = "/")
    public String getPage() {
        return "Welcome Null Team!";
    }
    @GetMapping(value = "/containers")
    public List<Container> getContainers() {
        return containerRepo.findAll();
    }
    @GetMapping(value = "/images")
    public List<Images> getImages() {
        return imageRepo.findAll();
    }

 */
    /*
    @PostMapping(value = "/saveContainer") //whenever you save sth to the DB it is a POST
    public String saveContainer(@RequestBody Container container) {
        containerRepo.save(container);
        return "Container Saved...";
    }
    @PostMapping(value = "/saveImage")
    public String saveImage(@RequestBody Images image) {
        imageRepo.save(image);
        return "Image Saved...";
    }
    @PutMapping(value = "update/Container/{id}")
    public String updateContainer(@PathVariable long id, @RequestBody Container container) {
        Container updatedContainer = containerRepo.findById(id).get();
        updatedContainer.setContainerId(container.getContainerId());
        updatedContainer.setName(container.getName());
        updatedContainer.setStatus(container.getStatus());
        updatedContainer.setImage(container.getImage());
        containerRepo.save(updatedContainer);
        return "Container Updated...";
    }
    @PutMapping(value = "update/Image/{id}")
    public String updateImage(@PathVariable long id, @RequestBody Images image) {
        Images updatedImage = imageRepo.findById(id).get();
        updatedImage.setImageRep(image.getImageRep());
        updatedImage.setImageId(image.getImageId());
        updatedImage.setImageTag(image.getImageTag());
        imageRepo.save(updatedImage);
        return "Image Updated...";
    }
    @DeleteMapping(value = "/delete/Container/{id}")
    public String deleteContainer(@PathVariable long id) {
        Container deleteContainer = containerRepo.findById(id).get();
        containerRepo.delete(deleteContainer);
        return "Deleted container with id: " + id;
    }
    @DeleteMapping(value = "/delete/Image/{id}")
    public String deleteImage(@PathVariable long id) {
        Images deleteImage = imageRepo.findById(id).get();
        imageRepo.delete(deleteImage);
        return "Deleted Image with id: " + id;
    }
    */
/*
}

*/