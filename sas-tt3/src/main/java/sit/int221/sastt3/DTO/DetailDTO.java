package sit.int221.sastt3.DTO;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.Setter;
import sit.int221.sastt3.entities.User;

import java.time.ZonedDateTime;

@Getter
@Setter
public class DetailDTO {
    private Integer id;
    private String announcementTitle;
    private String announcementDescription;
    private ZonedDateTime publishDate;
    private ZonedDateTime closeDate;
    private String announcementDisplay;
    private String announcementCategory ;
    private String  announcementOwner;
    private Integer viewCount;
    @JsonIgnore
    private CategoryDTO announcement;
    @JsonIgnore
    private User announcementsUse;
    public String getAnnouncementCategory() {
        return announcement == null ? "-" : announcement.getCategoryName();
    }
//    public String getAnnouncementOwnerUser() {
//        return announcementsUse == null ? "-" : announcementsUse.getUsername();
//    }
}
