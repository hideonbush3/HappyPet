package hideonbush3.springboot.happypet.dto;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import hideonbush3.springboot.happypet.model.PostEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PostDTO {
    private Long id;
    private String title;
    private String content;
    private Long views;
    private String regdate;
    private String nickname;
    private String username;
    private List<CommentDTO> commentList;

    public static PostEntity convertToEntity(PostDTO dto){
        return PostEntity.builder()
            .id(dto.getId())
            .title(dto.getTitle())
            .content(dto.getContent())
            .build();
    }

    public static PostDTO convertToDto(PostEntity entity){
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yy.MM.dd HH:mm:ss");
            String regdate = entity.getRegdate().format(formatter);
            String nickname = entity.getUserEntity().getNickname();
            String username = entity.getUserEntity().getUsername();

            List<CommentDTO> commentList = Optional.ofNullable(entity.getCommentList())
                    .map(list -> list.stream().map(CommentDTO::convertToDto).collect(Collectors.toList()))
                    .orElse(null);

            return PostDTO.builder()
                .id(entity.getId())
                .title(entity.getTitle())
                .content(entity.getContent())
                .views(entity.getViews())
                .regdate(regdate)
                .nickname(nickname)
                .username(username)
                .commentList(commentList)
                .build();
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }

    }
}
