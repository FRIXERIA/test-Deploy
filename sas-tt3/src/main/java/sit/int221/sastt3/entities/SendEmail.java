package sit.int221.sastt3.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SendEmail {
    // Class data members
    private String recipient;
    private String msgBody;
    private String subject;
    private String attachment;
}
