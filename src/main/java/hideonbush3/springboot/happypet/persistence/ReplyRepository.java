package hideonbush3.springboot.happypet.persistence;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import hideonbush3.springboot.happypet.model.CommentEntity;
import hideonbush3.springboot.happypet.model.ReplyEntity;
import hideonbush3.springboot.happypet.model.UserEntity;

public interface ReplyRepository extends JpaRepository<ReplyEntity, Long> {
    @Query("SELECT r FROM ReplyEntity r JOIN FETCH r.userEntity WHERE r.commentEntity = :commentEntity")
    List<ReplyEntity> findAllByCommentEntity(@Param("commentEntity") CommentEntity commentEntity);
    List<ReplyEntity> findAllByUserEntity(UserEntity userEntity);
    void deleteAllByUserEntity(UserEntity userEntity);
}
