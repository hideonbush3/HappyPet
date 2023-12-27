package hideonbush3.springboot.happypet.dto;

import hideonbush3.springboot.happypet.model.FavoriteEntity;
import hideonbush3.springboot.happypet.model.UserEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FavoriteDTO {
    private Long id;
    private String name;
    private String type;
    private String sigun;
    private String dong;
    private String addr;
    private String tel;
    private String opTime;
    private String satOpTime;
    private String sunOpTime;
    private String restDay;
    private String img;
    private String lot;
    private String lat;

    public static FavoriteEntity toFavoriteEntity(FavoriteDTO dto, String userId){
        UserEntity userEntity = UserEntity.builder().id(userId).build();
        FavoriteEntity favoriteEntity = FavoriteEntity.builder()
            .id(dto.getId())
            .name(dto.getName())
            .type(dto.getType())
            .sigun(dto.getSigun())
            .dong(dto.getDong())
            .addr(dto.getAddr())
            .tel(dto.getTel())
            .opTime(dto.getOpTime())
            .satOpTime(dto.getSatOpTime())
            .sunOpTime(dto.getSunOpTime())
            .restDay(dto.getRestDay())
            .img(dto.getImg())
            .lot(dto.getLot())
            .lat(dto.getLat())
            .userEntity(userEntity)
            .build();

        return favoriteEntity;
    }

    public static FavoriteDTO toFavoriteDTO(FavoriteEntity entity) {
        return FavoriteDTO.builder()
                .id(entity.getId())
                .name(entity.getName())
                .type(entity.getType())
                .sigun(entity.getSigun())
                .dong(entity.getDong())
                .addr(entity.getAddr())
                .tel(entity.getTel())
                .opTime(entity.getOpTime())
                .satOpTime(entity.getSatOpTime())
                .sunOpTime(entity.getSunOpTime())
                .restDay(entity.getRestDay())
                .img(entity.getImg())
                .lot(entity.getLot())
                .lat(entity.getLat())
                .build();
    }
}
