package hideonbush3.springboot.happypet.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import hideonbush3.springboot.happypet.dto.CommentDTO;
import hideonbush3.springboot.happypet.model.CommentEntity;
import hideonbush3.springboot.happypet.model.PostEntity;
import hideonbush3.springboot.happypet.model.UserEntity;
import hideonbush3.springboot.happypet.persistence.PostRepository;
import hideonbush3.springboot.happypet.persistence.CommentRepository;
import hideonbush3.springboot.happypet.persistence.UserRepository;

@Service
public class CommentServiceImpl implements CommentService{
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private PostRepository postRepository;
    
    @Override
    public List<CommentDTO> insert(CommentDTO dto, String userId) {
        try {
            LocalDateTime regdate = LocalDateTime.now();
            Optional<PostEntity> optionalPostEntity = postRepository.findById(dto.getPostId());
            PostEntity postEntity = optionalPostEntity.orElseThrow(() -> new RuntimeException("게시물이 존재하지 않습니다"));

            Optional<UserEntity> optionalUserEntity = userRepository.findById(userId);
            UserEntity userEntity = optionalUserEntity.orElseThrow(() -> new RuntimeException("유저가 존재하지 않습니다"));
            
            CommentEntity commentForSave = CommentEntity.builder()
                .content(dto.getContent())
                .regdate(regdate)
                .userEntity(userEntity)
                .postEntity(postEntity)
                .build();
            commentRepository.save(commentForSave);
 
            List<CommentEntity> commentList = commentRepository.findAllByPostEntity(postEntity);

            return commentList.stream().map(CommentDTO::convertToDto).collect(Collectors.toList());
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public List<CommentDTO> delete(CommentDTO dto) {
        try {
            commentRepository.deleteById(dto.getId());
            PostEntity postEntity = PostEntity.builder().id(dto.getPostId()).build();
            List<CommentEntity> commentList = commentRepository.findAllByPostEntity(postEntity);
            return commentList.stream().map(CommentDTO::convertToDto).collect(Collectors.toList());
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    
}
