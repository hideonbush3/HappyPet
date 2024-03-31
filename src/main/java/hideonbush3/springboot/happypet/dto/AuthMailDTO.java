package hideonbush3.springboot.happypet.dto;

import java.time.LocalDateTime;

import hideonbush3.springboot.happypet.model.AuthMailEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuthMailDTO {
    private String email;
    private String authCode;
    private LocalDateTime createdDate;

    public static AuthMailDTO convertToDto(AuthMailEntity entity){
        AuthMailDTO dto = new AuthMailDTO();
        dto.setEmail(entity.getEmail());
        dto.setCreatedDate(entity.getCreatedDate());
        return dto;
    }
}
