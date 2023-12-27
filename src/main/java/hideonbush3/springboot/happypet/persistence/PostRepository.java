package hideonbush3.springboot.happypet.persistence;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import hideonbush3.springboot.happypet.model.PostEntity;
import hideonbush3.springboot.happypet.model.UserEntity;

public interface PostRepository extends JpaRepository<PostEntity, Long>{
    List<PostEntity> findAllByUserEntity(UserEntity userEntity);
}
