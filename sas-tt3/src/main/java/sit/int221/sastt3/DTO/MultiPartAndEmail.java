package sit.int221.sastt3.DTO;

import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
@Getter
@Setter
public class MultiPartAndEmail {
    private List<MultipartFile> file;
    private String title;

    // Constructors, getters, and setters
}