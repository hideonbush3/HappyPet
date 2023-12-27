package hideonbush3.springboot.happypet.dto;

import hideonbush3.springboot.happypet.model.UserEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserDTO {
    private String token;
    private String username;
    private String password;
    private String nickname;
    private String email;
    private String id;

    public static UserEntity convertToEntity(UserDTO dto){
        UserEntity entity = UserEntity.builder().id(dto.getId()).build();
        return entity;
    }

    public static UserDTO convertToDto(UserEntity entity){
        return UserDTO.builder()
            .username(entity.getUsername())
            .nickname(entity.getNickname())
            .email(entity.getEmail())
            .build();
    }

}
