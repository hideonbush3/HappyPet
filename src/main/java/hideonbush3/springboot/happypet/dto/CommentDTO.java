package hideonbush3.springboot.happypet.dto;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import hideonbush3.springboot.happypet.model.CommentEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CommentDTO {
    private Long id;
    private String content;
    private String regdate;
    private String nickname;
    private String username;
    private Long postId;
    private List<ReplyDTO> replyList;

    public static CommentEntity convertToEntity(CommentDTO dto){
        return CommentEntity.builder()
            .id(dto.getId())
            .content(dto.getContent())
            .build();
    }

    public static CommentDTO convertToDto(CommentEntity entity){
        try {
            DateTimeFormatter fomatter = DateTimeFormatter.ofPattern("yy.MM.dd HH:mm:ss");
            String regdate = entity.getRegdate().format(fomatter);
            String nickname = entity.getUserEntity().getNickname();
            String username = entity.getUserEntity().getUsername();

            List<ReplyDTO> replyList = Optional.ofNullable(entity.getReplyList())
                .map(list -> list.stream().map(ReplyDTO::convertToDto).collect(Collectors.toList()))
                .orElse(new ArrayList<>());

            return CommentDTO.builder()
            .id(entity.getId())
            .content(entity.getContent())
            .regdate(regdate)
            .nickname(nickname)
            .username(username)
            .postId(entity.getPostEntity().getId())
            .replyList(replyList)
            .build();
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }
}
