package sit.int221.sastt3.DTO;

import lombok.Getter;
import lombok.Setter;

import javax.swing.*;
import java.io.Serializable;
@Getter
@Setter
public class JwtRequest {
    private String username;
    private String password;

}
