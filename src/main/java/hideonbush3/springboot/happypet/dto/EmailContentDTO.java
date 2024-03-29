package hideonbush3.springboot.happypet.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class EmailContentDTO {
    private String title;
    private String body;
}