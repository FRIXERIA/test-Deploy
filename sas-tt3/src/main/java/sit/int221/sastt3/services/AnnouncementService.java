package sit.int221.sastt3.services;

import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.server.ResponseStatusException;
import sit.int221.sastt3.DTO.AnnouncementDTO;
import sit.int221.sastt3.DTO.DetailDTO;
import sit.int221.sastt3.entities.Announcement;
import sit.int221.sastt3.entities.Category;
import sit.int221.sastt3.entities.User;
import sit.int221.sastt3.exception.InvalidRequestException;
import sit.int221.sastt3.exception.ItemNotFoundException;
import sit.int221.sastt3.jwt.JwtTokenUtil;
import sit.int221.sastt3.repositories.AnnouncementRepo;
import sit.int221.sastt3.repositories.CategoryRepo;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class AnnouncementService {
    @Autowired
    public AnnouncementRepo repo;
    @Autowired
    public CategoryRepo categoryRepo;
    @Autowired
    private ModelMapper modelMapper;
    @Autowired
    public CategoryService categoryService;
    @Autowired
    private JwtTokenUtil jwtTokenUtil;
//    @Autowired
//    private SendEmailService emailService;

    @Autowired
private UserService userService;
//    public List<Announcement> getAllAnnouncement() {
////        if(repo.findAllByOrderByAnnouncementIdDesc().isEmpty()){
////            throw new ResponseStatusException(HttpStatus.NOT_FOUND,"Announcement is not found");
////        }
//       return repo.findAllByOrderByIdDesc();
//
//    }

    public List<Announcement> getAllAnnouncement() {
        return repo.findAllByOrderByIdDesc();

    }
    public List<Announcement> getActiveAnnouncement(){
        ZonedDateTime now = ZonedDateTime.now();
        List<Announcement> announcements = repo.findAllByAnnouncementDisplayEqualsOrderByIdDesc("Y");
        List<Announcement> announcementList = new ArrayList<>();

        for (Announcement announcement : announcements) {
            ZonedDateTime publishDate = announcement.getPublishDate();
            ZonedDateTime closeDate = announcement.getCloseDate();

            // Check if publishDate and closeDate are null, then only show if publishDate is not null and current time is greater than publishDate
            if (publishDate == null && closeDate == null) {
//                if (now.isAfter(announcement.getCreatedAt())) {
                    announcementList.add(announcement);
//
            }
            // Check if both publishDate and closeDate are present, then only show if current time is between publishDate and closeDate
            else if (publishDate != null && closeDate != null) {
                if (now.isAfter(publishDate) && now.isBefore(closeDate)) {
                    announcementList.add(announcement);
                }
            }
            // Check if only closeDate is present, then only show if current time is before closeDate
            else if (closeDate != null) {
                if (now.isBefore(closeDate)) {
                    announcementList.add(announcement);
                }
            }
            // Check if only publishDate is present, then only show if current time is after publishDate
            else {
                if (now.isAfter(publishDate)) {
                    announcementList.add(announcement);
                }
            }
        }

        return announcementList;
    }

    public List<Announcement> getCloseAnnouncement(){
        ZonedDateTime now = ZonedDateTime.now();
        List<Announcement> announcementsY = repo.findAllByAnnouncementDisplayEqualsOrderByIdDesc("Y");
        List<Announcement> announcementList = new ArrayList<>();
        for (Announcement announcement : announcementsY) {
            ZonedDateTime closeDate = announcement.getCloseDate();
            if (closeDate != null) {
                if (now.isAfter(closeDate)) {
                    announcementList.add(announcement);
                }
            }
        }
        return announcementList;
    }
    public Announcement getAnnouncementById(Integer id, Boolean num) {
        if(num==false){
            return repo.findById(id).orElseThrow(()->
                    new ResponseStatusException(HttpStatus.NOT_FOUND, "Announcement id "+ id + " does not exist"));
        }
      if(num==true){
          System.out.println();
            Announcement numberFor =  repo.findById(id).orElseThrow(()->
                    new ResponseStatusException(HttpStatus.NOT_FOUND, "Announcement id "+ id + " does not exist"));;
            Integer beforeNum =numberFor.getViewCount();
if(beforeNum==null){
    numberFor.setViewCount(0);
    Integer setZero=numberFor.getViewCount();
    numberFor.setViewCount(setZero+1);
    repo.saveAndFlush(numberFor);
    return numberFor;
}

            numberFor.setViewCount(beforeNum+1);
            repo.saveAndFlush(numberFor);
            return numberFor;
        }
        return repo.findById(id).orElseThrow(()->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "Announcement id "+ id + " does not exist"));
    }
//    public void deleteAnnouncement(Integer id){
//        Announcement announcement = repo.findById(id).orElseThrow(
//                ()-> new ResponseStatusException(HttpStatus.NOT_FOUND, "Announcement id "+ id + " does not exist"));
//        repo.delete(announcement);
//    }
public void deleteAnnouncement(Integer id) {
    try {
        Map<String, String> errors = new HashMap<>();
        if (id == null || id <= 0) {
            throw new InvalidRequestException("Invalid announcement id: ", errors);
        }
        Announcement announcement = getAnnouncementById(id,false);
        repo.delete(announcement);
    } catch (ItemNotFoundException e) {
        throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Announcement not found", e);
    } catch (InvalidRequestException e) {
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage(), e);
    }
}

//    public Announcement updateAnnouncement(Integer id, Announcement announcement) {
//        Announcement existingAnnouncement = repo.findById(id).orElseThrow(
//                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Announcement id" + id + "doesn't exist!!!"));
//        existingAnnouncement.setAnnouncementTitle(announcement.getAnnouncementTitle());
//        existingAnnouncement.setAnnouncementDescription(announcement.getAnnouncementDescription());
//        existingAnnouncement.setPublishDate(announcement.getPublishDate());
//        existingAnnouncement.setCloseDate(announcement.getCloseDate());
//        existingAnnouncement.setAnnouncementDisplay(announcement.getAnnouncementDisplay());
//        existingAnnouncement.setAnnouncementCategory(announcement.getAnnouncementCategory());
//        return repo.saveAndFlush(existingAnnouncement);
//    }

//    public Announcement updateAnnouncement(Integer id, Announcement update) {
//        Announcement announcement = repo.findById(id)
//                .orElseThrow(
//               () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Announcement id" + id + "doesn't exist!!!"));
//
//        // Update the fields of the announcement with the values from the update object
//        announcement.setAnnouncementTitle(update.getAnnouncementTitle());
//        announcement.setAnnouncementDescription(update.getAnnouncementDescription());
//        announcement.setPublishDate(update.getPublishDate());
//        announcement.setCloseDate(update.getCloseDate());
//        announcement.setAnnouncementDisplay(update.getAnnouncementDisplay());
//
//        // Update the announcement category
//        Category category = categoryService.getCategoryById(update.getCategoryId());
//        announcement.setCategoryId(update.getCategoryId());
//        announcement.setAnnouncementCategory(category);
//
//        // Save the updated announcement
//        return repo.saveAndFlush(announcement);
//    }


//    public Announcement updateAnnouncement(Integer id, Announcement update) {
//        try {
//            if (id == null || id <=0) {
//                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid announcement id: " + id);
//            }
//            if (update == null) {
//                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No fields to update");
//            }
//            if (update.getAnnouncementTitle() != null && update.getAnnouncementTitle().length() > 200) {
//                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Title is over the limit");
//            }
//            if (update.getAnnouncementDescription() != null && update.getAnnouncementDescription().length() > 10000) {
//                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Description is over the limit");
//            }
//            Announcement announcement = getAnnouncementById(id);
//            // Update the fields of the announcement with the values from the update object
//            if (update.getAnnouncementTitle() != null && update.getAnnouncementTitle().length() < 200) {
//                announcement.setAnnouncementTitle(update.getAnnouncementTitle());
//            }
//            if (update.getAnnouncementDescription() != null && update.getAnnouncementDescription().length() < 10000) {
//                announcement.setAnnouncementDescription(update.getAnnouncementDescription());
//            }
//            if (update.getPublishDate() != null) {
//                announcement.setPublishDate(update.getPublishDate());
//            }
//            if (update.getCloseDate() != null) {
//                announcement.setCloseDate(update.getCloseDate());
//            }
//            if (update.getAnnouncementDisplay() != null) {
//                announcement.setAnnouncementDisplay(update.getAnnouncementDisplay());
//            }
//            if (update.getCategoryId() != null) {
//                Category category = categoryService.getCategoryById(update.getCategoryId());
//                announcement.setCategoryId(update.getCategoryId());
//                announcement.setAnnouncementCategory(category);
//            }
//            // Save the updated announcement
//            return repo.saveAndFlush(announcement);
//        } catch (ItemNotFoundException e) {
//            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Announcement not found", e);
//        }
//    }


    public Announcement updateAnnouncement(Integer id, Announcement update) {
        try {
            System.out.println(update.getPublishDate());
            ZonedDateTime now =ZonedDateTime.now();
            Map<String, String> errors = new HashMap<>();
            if (id == null || id <=0) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid announcement id: " + id);
            }
            if (update == null) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No fields to update");
            }
            if (update.getAnnouncementTitle() != null && update.getAnnouncementTitle().length() > 200) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Title is over the limit");
            }
            if (update.getAnnouncementDescription() != null && update.getAnnouncementDescription().length() > 10000) {
                System.out.println(update.getAnnouncementDescription().length());
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Description is over the limit");
            }
            Announcement announcement = getAnnouncementById(id,false);
            // Update the fields of the announcement with the values from the update object
            if (update.getAnnouncementTitle() != null && update.getAnnouncementTitle().length() <= 200) {
                announcement.setAnnouncementTitle(update.getAnnouncementTitle());
            }
            if (update.getAnnouncementDescription() != null && update.getAnnouncementDescription().length() <= 10000) {
                announcement.setAnnouncementDescription(update.getAnnouncementDescription());
            }
            if(update.getPublishDate()==null){
                announcement.setPublishDate(null);
            }
            if (update.getPublishDate() != null) {
                if (update.getPublishDate().isBefore(now)) {
                    errors.put("publishDate", "must be a date in the present or in the future");
                }else{
                announcement.setPublishDate(update.getPublishDate());
                    System.out.println(update.getPublishDate());
                }
            }
            if(update.getCloseDate()==null){
                announcement.setCloseDate(null);
            }
            if (update.getCloseDate() != null) {
                if (update.getCloseDate().isBefore(now)) {
                    errors.put("closeDate", "must be a future date");
                }else{
                announcement.setCloseDate(update.getCloseDate());
                }
            }
            if (update.getAnnouncementDisplay() != null) {
                announcement.setAnnouncementDisplay(update.getAnnouncementDisplay());
            }
            if(update.getPublishDate()!=null&&update.getCloseDate()!=null){
                if(update.getCloseDate().isEqual(update.getPublishDate())){
                    errors.put("closeDate", "must be later than publish date");
                }
            }
            if (update.getCategoryId() != null) {
                Category category = categoryService.getCategoryById(update.getCategoryId());
                announcement.setCategoryId(update.getCategoryId());
                announcement.setAnnouncementCategory(category);
            }
            if (!errors.isEmpty()) {
                throw new InvalidRequestException("Announcement attributes validation failed!", errors);
            }
//            if(announcement.getPublishDate()==null){
//                emailService.sendupdateAnnounceMail(announcement);
//            }
            // Save the updated announcement
//            emailService.sendupdateAnnounceMail(announcement);
            return repo.saveAndFlush(announcement);
        } catch (ItemNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Announcement not found", e);
        }
    }

    public List<Announcement> filterCategory(int id){
        return repo.findAllByCategoryIdEqualsOrderByIdDesc(id);

    }

    public Page <Announcement> getWithPagination(String mode, Integer page, Integer size, Integer category ){
        List<Announcement> active =getActiveAnnouncement();
        List<Announcement> close =getCloseAnnouncement();
        List <Announcement> all =getAllAnnouncement();
        List <Announcement> filterResult =new ArrayList<>();
        PageRequest requestPage =PageRequest.of(page,size);
        switch (mode) {
            case "active":
                if(category!=null) {
                 for(Announcement activeAnnouncement:active){

                     if(activeAnnouncement.getAnnouncementCategory().equals(categoryRepo.findById(category).get())){
                         filterResult.add(activeAnnouncement);
                     }
                 }

                }if(category==null){
                  filterResult=active;
                }
                break;
            case "close":
                if(category!=null) {
                    for(Announcement closeAnnouncement:close){
                        if(closeAnnouncement.getAnnouncementCategory().equals(categoryRepo.findById(category).get())){
                            filterResult.add(closeAnnouncement);
                        }
                    }

                }if(category==null){
                    filterResult=close;
                }

                break;
            case "admin":
                if(category!=null){
                    for(Announcement allAnnouncement:all){
                        if(allAnnouncement.getAnnouncementCategory().equals(categoryRepo.findById(category).get())){
                            filterResult.add(allAnnouncement);
                        }
                    }

                }else{
                filterResult=all;
            }
            break;
            default:
                if(category!=null){
                    for(Announcement allAnnouncement:all){
                        if(allAnnouncement.getAnnouncementCategory().equals(categoryRepo.findById(category).get())){
                            filterResult.add(allAnnouncement);
                        }
                    }
                }else{
                    filterResult=all;
                }

                break;
        }
        int startIndex = page * size;
        int endIndex = Math.min(startIndex + size, filterResult.size());
        List<Announcement> paginations=filterResult.subList(startIndex,endIndex);
        return new PageImpl<>(paginations,requestPage,filterResult.size());
    }

}
