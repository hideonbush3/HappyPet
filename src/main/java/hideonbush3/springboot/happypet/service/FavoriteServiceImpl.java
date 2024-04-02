package hideonbush3.springboot.happypet.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import hideonbush3.springboot.happypet.dto.FavoriteDTO;
import hideonbush3.springboot.happypet.dto.ResponseDTO;
import hideonbush3.springboot.happypet.model.FavoriteEntity;
import hideonbush3.springboot.happypet.model.UserEntity;
import hideonbush3.springboot.happypet.persistence.FavoriteRepository;

@Service
public class FavoriteServiceImpl implements FavoriteService{
    @Autowired
    private FavoriteRepository favoriteRepository;
    
    @Override
    public ResponseDTO<FavoriteDTO> selectOne(FavoriteDTO dto, String userId){
        try{
            final UserEntity userEntity = UserEntity.builder().id(userId).build();
            Optional<FavoriteEntity> optionalEntity = favoriteRepository.findByUserEntityAndNameAndAddr(userEntity, dto.getName(), dto.getAddr());
            
            ResponseDTO<FavoriteDTO> responseDTO = new ResponseDTO<>();
            if(optionalEntity.isPresent()){
                FavoriteDTO favoriteDTO = FavoriteDTO.toFavoriteDTO(optionalEntity.get());
                responseDTO.setObject(favoriteDTO);
            }
            return responseDTO;
        }catch(Exception e){
            throw new RuntimeException(e.getMessage());
        }
    }
    @Override
    public List<FavoriteDTO> select(String userId) {
        try {
            UserEntity userEntity = UserEntity.builder().id(userId).build();
            List<FavoriteEntity> entities = favoriteRepository.findAllByUserEntity(userEntity);
            List<FavoriteDTO> dtos = entities.stream().map(FavoriteDTO::toFavoriteDTO).collect(Collectors.toList());
            return dtos;       
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public ResponseDTO<Object> insert(FavoriteDTO dto, String userId) {
        ResponseDTO<Object> res = new ResponseDTO<>();
        try{
            final FavoriteEntity favoriteEntity = FavoriteDTO.toFavoriteEntity(dto, userId);
            FavoriteEntity savedEntity = favoriteRepository.save(favoriteEntity);
            FavoriteDTO favoriteDTO = FavoriteDTO.toFavoriteDTO(savedEntity);
            res.setObject(favoriteDTO);
            return res;
        }catch(Exception e){
            res.setError(e.getMessage());
            return res;
        }
    }

    @Override
    public ResponseDTO<?> deleteInModal(FavoriteDTO dto, String userId) {
        ResponseDTO<?> res = new ResponseDTO<>();
        try{
            FavoriteEntity favoriteEntity = FavoriteDTO.toFavoriteEntity(dto, userId);
            favoriteRepository.delete(favoriteEntity);
            res.setMessage("삭제성공");
            return res;
        }catch(Exception e){
            res.setError(e.getMessage());
            return res;
        }
    }

    
}
