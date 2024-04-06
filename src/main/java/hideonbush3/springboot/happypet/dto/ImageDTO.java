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
    private String name;
    private Long bytes;

    public static ImageDTO convertToDto(ImageEntity imageEntity){
        return ImageDTO.builder()
            .name(imageEntity.getName())
            .bytes(imageEntity.getBytes())
            .build();
    }
}
