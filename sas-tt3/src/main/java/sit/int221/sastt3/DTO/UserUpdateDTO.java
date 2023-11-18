package sit.int221.sastt3.DTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.ZonedDateTime;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UserUpdateDTO {
    private Integer id;
    private String username;
    private String name;
    private String email;
    private String role;
    private ZonedDateTime createdOn;
    private ZonedDateTime updatedOn;
}
