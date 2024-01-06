package hideonbush3.springboot.happypet.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import hideonbush3.springboot.happypet.dto.FavoriteDTO;
import hideonbush3.springboot.happypet.model.FavoriteEntity;
import hideonbush3.springboot.happypet.model.UserEntity;
import hideonbush3.springboot.happypet.persistence.FavoriteRepository;

@Service
public class FavoriteServiceImpl implements FavoriteService{
    @Autowired
    private FavoriteRepository favoriteRepository;
    
    @Override
    public FavoriteDTO selectOne(FavoriteDTO dto, String userId){
        try{
            final UserEntity userEntity = UserEntity.builder().id(userId).build();
            FavoriteEntity favoriteEntity = favoriteRepository.findByUserEntityAndNameAndAddr(userEntity, dto.getName(), dto.getAddr()).get();
            return FavoriteDTO.toFavoriteDTO(favoriteEntity);
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
    public FavoriteDTO insert(FavoriteDTO dto, String userId) {
        try{
            final FavoriteEntity favoriteEntity = FavoriteDTO.toFavoriteEntity(dto, userId);
            final UserEntity userEntity = UserEntity.builder().id(userId).build();

            Optional<FavoriteEntity> existingEntity = favoriteRepository.findByUserEntityAndNameAndAddr(userEntity, dto.getName(), dto.getAddr());

            if (existingEntity.isPresent()) {
                throw new RuntimeException("이미 즐겨찾기에 추가한 시설입니다.");
            }
            FavoriteEntity savedEntity = favoriteRepository.save(favoriteEntity);
            FavoriteDTO favoriteDTO = FavoriteDTO.toFavoriteDTO(savedEntity);
            return favoriteDTO;
        }catch(Exception e){
            throw new RuntimeException(e.getMessage());
        }
    }
    @Override
    public List<FavoriteDTO> delete(FavoriteDTO dto, String userId) {
        try {
            FavoriteEntity favoriteEntity = FavoriteDTO.toFavoriteEntity(dto, userId);
            favoriteRepository.delete(favoriteEntity);
            
            UserEntity userEntity = UserEntity.builder().id(userId).build();
            List<FavoriteEntity> entities = favoriteRepository.findAllByUserEntity(userEntity);
            List<FavoriteDTO> dtos = entities.stream().map(FavoriteDTO::toFavoriteDTO).collect(Collectors.toList());

            return dtos;
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public void deleteInModal(FavoriteDTO dto, String userId) {
        try{
            FavoriteEntity favoriteEntity = FavoriteDTO.toFavoriteEntity(dto, userId);
            favoriteRepository.delete(favoriteEntity);
        }catch(Exception e){
            throw new RuntimeException(e.getMessage());
        }
    }

    
}