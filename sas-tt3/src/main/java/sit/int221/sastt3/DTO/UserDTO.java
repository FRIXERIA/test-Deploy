package sit.int221.sastt3.DTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.ZonedDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor

public class UserDTO {
        private Integer id;
        private String username;
        private String name;
        private String email;
        private String role;
        private ZonedDateTime createdOn;
        private ZonedDateTime updatedOn;
}
