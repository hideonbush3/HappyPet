package hideonbush3.springboot.happypet.dto;

import java.time.format.DateTimeFormatter;

import hideonbush3.springboot.happypet.model.ReplyEntity;
import hideonbush3.springboot.happypet.model.UserEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReplyDTO {    
    private Long id;
    private String content;
    private String regdate;
    private String username;
    private String nickname;
    private Long commentId;

    public static ReplyDTO convertToDto(ReplyEntity entity){
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yy.MM.dd HH:mm:ss");
            String regdate = entity.getRegdate().format(formatter);
            UserEntity userEntity = entity.getUserEntity();

            ReplyDTO dto = ReplyDTO.builder()
                .id(entity.getId())
                .content(entity.getContent())
                .regdate(regdate)
                .username(userEntity.getUsername())
                .nickname(userEntity.getNickname())
                .commentId(entity.getCommentEntity().getId())
                .build();
            return dto;            
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    public static ReplyEntity convertToEntity(ReplyDTO replyDTO){
        ReplyEntity replyEntity = ReplyEntity.builder()
            .id(replyDTO.getId())
            .build();
        return replyEntity;
    }
}
