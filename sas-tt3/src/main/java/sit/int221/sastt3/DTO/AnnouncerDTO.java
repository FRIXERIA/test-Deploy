package sit.int221.sastt3.DTO;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;
import sit.int221.sastt3.entities.User;

import java.time.ZonedDateTime;
@Getter
@Setter
public class AnnouncerDTO {
    private Integer id;
    private String announcementTitle;
    private ZonedDateTime publishDate;
    private ZonedDateTime closeDate;
    private String announcementDisplay;
    private String announcementCategory ;
    private Integer viewCount;

}
