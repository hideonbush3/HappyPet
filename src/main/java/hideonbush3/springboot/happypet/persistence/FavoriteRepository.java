package hideonbush3.springboot.happypet.persistence;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import hideonbush3.springboot.happypet.model.FavoriteEntity;
import hideonbush3.springboot.happypet.model.UserEntity;

public interface FavoriteRepository extends JpaRepository<FavoriteEntity, Long>{
    List<FavoriteEntity> findAllByUserEntity(UserEntity userEntity);
    Optional<FavoriteEntity> findByUserEntityAndNameAndAddr(UserEntity userEntity, String name, String addr);
    void deleteAllByUserEntity(UserEntity userEntity);
}
