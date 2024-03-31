package hideonbush3.springboot.happypet.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class MailContentDTO {
    private String email;
    private String userId;
    private String title;
    private String body;
}