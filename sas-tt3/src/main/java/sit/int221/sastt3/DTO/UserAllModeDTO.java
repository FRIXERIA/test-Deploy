package sit.int221.sastt3.DTO;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;
import sit.int221.sastt3.entities.User;

import java.time.ZonedDateTime;

@Getter
@Setter
public class UserAllModeDTO {
    private Integer id;
    private String announcementTitle;
    private ZonedDateTime publishDate;
    private ZonedDateTime closeDate;
    private String announcementDisplay;
    private String announcementCategory ;
    private String  announcementOwner;
    private Integer viewCount;
//    private Integer categoryId;
    @JsonIgnore
    private CategoryDTO announcement;
    @JsonIgnore
    private User announcementsUse;



    public String getAnnouncementCategory(){
        return announcement == null ? "-" : announcement.getCategoryName();
    }

    public String getAnnouncementsUse(){
        return announcementsUse == null ? "-" : announcementsUse.getUsername();
    }
//    public String getAnnouncementOwnerUser() {
//        return announcementsUse == null ? "-" : announcementsUse.getUsername();
//    }

//    public Integer getCategoryId(){
//        return announcement == null ? 1 : announcement.getCategoryId();
//    }
//        private String getCategory(){
//        return category.getCategoryName();
//    }
}
