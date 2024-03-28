package hideonbush3.springboot.happypet.dto;

import java.time.LocalDateTime;

import hideonbush3.springboot.happypet.model.EmailAuthEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EmailAuthDTO {
    private String email;
    private String authCode;
    private LocalDateTime createdDate;

    public static EmailAuthDTO convertToDto(EmailAuthEntity entity){
        EmailAuthDTO dto = new EmailAuthDTO();
        dto.setEmail(entity.getEmail());
        dto.setCreatedDate(entity.getCreatedDate());
        return dto;
    }
}
