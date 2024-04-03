package hideonbush3.springboot.happypet.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import hideonbush3.springboot.happypet.dto.CommentDTO;
import hideonbush3.springboot.happypet.dto.ResponseDTO;
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
    public ResponseDTO<CommentDTO> insert(CommentDTO dto, String userId) {
        ResponseDTO<CommentDTO> res = new ResponseDTO<>();
        try {
            LocalDateTime regdate = LocalDateTime.now();
            Optional<PostEntity> optionalPost = postRepository.findById(dto.getPostId());
            if(!optionalPost.isPresent()){
                res.setMessage("게시글이존재하지않음");
            }
            else{
                UserEntity user = userRepository.findById(userId).get();
                PostEntity post = optionalPost.get();
                
                CommentEntity commentForSave = CommentEntity.builder()
                    .content(dto.getContent())
                    .regdate(regdate)
                    .userEntity(user)
                    .postEntity(post)
                    .build();
                commentRepository.save(commentForSave);
     
                List<CommentEntity> commentList = commentRepository.findAllByPostEntity(post);
    
                List<CommentDTO> commentDtoList = commentList.stream().map(CommentDTO::convertToDto).collect(Collectors.toList());
                res.setData(commentDtoList);
            }
            return res;
        } catch (Exception e) {
            res.setError(e.getMessage());
            return res;
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
