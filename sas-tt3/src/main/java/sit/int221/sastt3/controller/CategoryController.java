package sit.int221.sastt3.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import sit.int221.sastt3.entities.Announcement;
import sit.int221.sastt3.entities.Category;
import sit.int221.sastt3.services.AnnouncementService;
import sit.int221.sastt3.services.CategoryService;

import java.util.List;
//@CrossOrigin(origins= {"http://intproj22.sit.kmutt.ac.th/","http://localhost:5173/"})
@RestController
@RequestMapping("/api")
@CrossOrigin
public class CategoryController {
    @Autowired
    private CategoryService service;

    @GetMapping("/category")
    public List<Category> getAllCustomer(){
        return service.getAllCategory();
    }

    @GetMapping("/category/{id}")
    public Category getAllCustomer(@PathVariable Integer id){
        return service.getCategoryById(id);
    }

    @PostMapping("/{categories}")
    public Category createCategory(@RequestBody Category newcategory) {
        return service.createCategory(newcategory);
    }

}
