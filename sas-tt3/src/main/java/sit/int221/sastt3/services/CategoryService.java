package sit.int221.sastt3.services;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import sit.int221.sastt3.DTO.AnnouncementInsert;
import sit.int221.sastt3.DTO.UserRealDTO;
import sit.int221.sastt3.entities.Announcement;
import sit.int221.sastt3.entities.Category;
import sit.int221.sastt3.entities.User;
import sit.int221.sastt3.exception.InvalidRequestException;
import sit.int221.sastt3.exception.ItemNotFoundException;
import sit.int221.sastt3.repositories.AnnouncementRepo;
import sit.int221.sastt3.repositories.CategoryRepo;
import sit.int221.sastt3.repositories.UserRepo;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class CategoryService {
    @Autowired
    public CategoryRepo repo;
    @Autowired
    private ModelMapper modelMapper;
    @Autowired
    private UserService serviceUser;
//    @Autowired
//    private SendEmailService emailService;
    public List<Category> getAllCategory() {
        return repo.findAll();
    }
    public Category getCategoryById(Integer id) {
        Map<String, String> errors = new HashMap<>();
        try {
            return repo.findById(id).orElseThrow(() -> new ItemNotFoundException("Category id " + id + " does not exist"));
        } catch (ItemNotFoundException e) {
            errors.put("categoryId","does not exists");
            if (!errors.isEmpty()) {
                throw new InvalidRequestException("Announcement attributes validation failed!", errors);
            }
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "CategoryId not found",e );
        } catch (InvalidRequestException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage(), e);
        }
    }
    public Category updateCategory(Integer id, Category category) {
        Category existingCategory = repo.findById(id).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Category id" + id + "doesn't exist!!!"));
        existingCategory.setId(category.getId());
        existingCategory.setCategoryName(category.getCategoryName());
        return repo.saveAndFlush(existingCategory);
    }
    @Autowired
    private AnnouncementRepo AnnounceRepo;

public Announcement addAnnouncement(AnnouncementInsert newAnnouncement){
    try {
        System.out.println(newAnnouncement.getPublishDate());
        ZonedDateTime now =ZonedDateTime.now();
        Map<String, String> errors = new HashMap<>();

        if (newAnnouncement.getAnnouncementTitle() == null) {
            errors.put("announcementTitle", "must not be null");
            //} else if (newAnnouncement.getAnnouncementTitle().length() > 200 ) {
        } else if (newAnnouncement.getAnnouncementTitle().length() > 200 || newAnnouncement.getAnnouncementTitle().length() < 1) {
            errors.put("announcementTitle", "size must be between 1 and 200");
        }
        else if (newAnnouncement.getAnnouncementTitle().isBlank()) {
            errors.put("announcementTitle", "must not be blank");
        }
        if (newAnnouncement.getAnnouncementDescription() == null) {
            errors.put("announcementDescription", "must not be null");
            //} else if (newAnnouncement.getAnnouncementDescription().length() > 10000) {
        } else if (newAnnouncement.getAnnouncementDescription().length() > 10000 || newAnnouncement.getAnnouncementDescription().length() < 1) {
            System.out.println(newAnnouncement.getAnnouncementDescription().length());
            System.out.println(newAnnouncement.getAnnouncementDescription());
            errors.put("announcementDescription", "size must be between 1 and 10000");
        } else if (newAnnouncement.getAnnouncementDescription().isBlank()) {
            errors.put("announcementDescription", "must not be blank");
        }
        if (newAnnouncement.getAnnouncementDisplay() == null) {
            newAnnouncement.setAnnouncementDisplay("N");

        }
        if (!newAnnouncement.getAnnouncementDisplay().equals("Y") && !newAnnouncement.getAnnouncementDisplay().equals("N")) {
            errors.put("announcementDisplay", "must be either 'Y' or 'N'");
        }

        if (newAnnouncement.getCategoryId() == null) {
            errors.put("categoryId", "must not be null");
        } else {
            Category category = getCategoryById(newAnnouncement.getCategoryId());
            if (category == null) {
                errors.put("categoryId", "must not be null");
            }
        }
        if (newAnnouncement.getPublishDate() != null) {
            if (newAnnouncement.getPublishDate().isBefore(now)) {
                errors.put("publishDate", "must be a date in the present or in the future");
            }
        }
        if (newAnnouncement.getCloseDate() != null) {
            if (newAnnouncement.getCloseDate().isBefore(now)) {
                errors.put("closeDate", "must be a future date");
            }

        }
        if (newAnnouncement.getCloseDate() != null && newAnnouncement.getPublishDate()!=null) {
            if (newAnnouncement.getCloseDate().isBefore(now)) {
                errors.put("closeDate", "must be a future date");
            }
//
            if(newAnnouncement.getCloseDate().isBefore(newAnnouncement.getPublishDate())){
                errors.put("closeDate", "must be later than publish date");
            }
            if(newAnnouncement.getCloseDate().isEqual(newAnnouncement.getPublishDate())){
                errors.put("closeDate", "must be later than publish date");
            }
            if (newAnnouncement.getPublishDate().isBefore(now)) {
                errors.put("publishDate", "must be a date in the present or in the future");
            }

            if(newAnnouncement.getCloseDate().isBefore(newAnnouncement.getPublishDate())){
                errors.put("closeDate", "must be later than publish date");
            }
            if(newAnnouncement.getCloseDate().isEqual(newAnnouncement.getPublishDate())){
                errors.put("closeDate", "must be later than publish date");
            }
        }
        if (!errors.isEmpty()) {
            throw new InvalidRequestException("Announcement attributes validation failed!", errors);
        }
        Announcement announcement = new Announcement();
        announcement.setAnnouncementTitle(newAnnouncement.getAnnouncementTitle());
        announcement.setAnnouncementDescription(newAnnouncement.getAnnouncementDescription());
        announcement.setPublishDate(newAnnouncement.getPublishDate());
        announcement.setCloseDate(newAnnouncement.getCloseDate());
        announcement.setAnnouncementDisplay(newAnnouncement.getAnnouncementDisplay());
        announcement.setCategoryId(newAnnouncement.getCategoryId());
        announcement.setAnnouncementOwner(newAnnouncement.getAnnouncementOwner());
        // Set the announcement category
        Category category = getCategoryById(newAnnouncement.getCategoryId());
        User user = serviceUser.getUserById(newAnnouncement.getAnnouncementOwner());
        announcement.setAnnouncementCategory(category);
        announcement.setAnnouncementOwnerUser(user);
        if(announcement.getViewCount()==null){
            announcement.setViewCount(0);
        }
        // Save the new announcement
//        if(announcement.getPublishDate()==null){
//            emailService.sendnewAnnounceMail(announcement);
//        }
//        emailService.sendnewAnnounceMail(announcement);
        return AnnounceRepo.saveAndFlush(announcement);
    }
    catch (ItemNotFoundException exception){throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Announcement not found", exception);}
}

    public Category createCategory(Category newCategory){
        Map<String, String> errors = new HashMap<>();
        if (newCategory == null || newCategory.getId() < 1 ){throw new InvalidRequestException("Cannot create category",errors);}
        return  repo.saveAndFlush(newCategory);
    }
}
