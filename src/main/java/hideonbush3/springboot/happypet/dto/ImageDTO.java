package hideonbush3.springboot.happypet.dto;

import hideonbush3.springboot.happypet.model.ImageEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ImageDTO {
    private Long postId;
    private String name;
    private String ext;
    private Long sequence;
    private Double kBytes;

    public static ImageDTO convertToDto(ImageEntity imageEntity){
        return ImageDTO.builder()
            .postId(imageEntity.getPostEntity().getId())
            .name(imageEntity.getName() + " " + imageEntity.getUuid() + "." + imageEntity.getExt())
            .ext(imageEntity.getExt())
            .sequence(imageEntity.getSequence())
            .kBytes(imageEntity.getKBytes())
            .build();
    }
}
