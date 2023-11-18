package sit.int221.sastt3.DTO;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;
import sit.int221.sastt3.entities.User;

import java.time.ZonedDateTime;
import java.util.List;

@Getter
@Setter
public class SubscribeDTO {
    private String subscriberEmail;
    private Integer categoryId;
}
