package hideonbush3.springboot.happypet.service;

import java.util.List;

import hideonbush3.springboot.happypet.dto.FavoriteDTO;
import hideonbush3.springboot.happypet.dto.ResponseDTO;

public interface FavoriteService {
    public ResponseDTO<FavoriteDTO> selectOne(FavoriteDTO dto, String userId);
    public List<FavoriteDTO> select(String userId);
    public ResponseDTO<Object> insert(FavoriteDTO dto, String userId);
    public ResponseDTO<?> deleteInModal(FavoriteDTO dto, String userId);
}
