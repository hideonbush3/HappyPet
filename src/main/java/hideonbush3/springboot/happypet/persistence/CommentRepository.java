package hideonbush3.springboot.happypet.persistence;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import hideonbush3.springboot.happypet.model.CommentEntity;
import hideonbush3.springboot.happypet.model.PostEntity;
import hideonbush3.springboot.happypet.model.UserEntity;

public interface CommentRepository extends JpaRepository<CommentEntity, Long>{
    List<CommentEntity> findAllByPostEntity(PostEntity entity);
    List<CommentEntity> findAllByUserEntity(UserEntity userEntity);
    void deleteAllByUserEntity(UserEntity userEntity);
}

