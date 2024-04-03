package hideonbush3.springboot.happypet.service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import hideonbush3.springboot.happypet.dto.ReplyDTO;
import hideonbush3.springboot.happypet.dto.ResponseDTO;
import hideonbush3.springboot.happypet.model.CommentEntity;
import hideonbush3.springboot.happypet.model.PostEntity;
import hideonbush3.springboot.happypet.model.ReplyEntity;
import hideonbush3.springboot.happypet.model.UserEntity;
import hideonbush3.springboot.happypet.persistence.CommentRepository;
import hideonbush3.springboot.happypet.persistence.PostRepository;
import hideonbush3.springboot.happypet.persistence.ReplyRepository;
import hideonbush3.springboot.happypet.persistence.UserRepository;

@Service
public class ReplyServiceImpl implements ReplyService{
    @Autowired
    private ReplyRepository replyRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private PostRepository postRepository;

    @Override
    public ResponseDTO<ReplyDTO> insert(ReplyDTO dto, String userId, Long postId) {
        ResponseDTO<ReplyDTO> res = new ResponseDTO<>();
        try {
            Long commentId = dto.getCommentId();

            String content = dto.getContent();

            Optional<PostEntity> optionalPost = postRepository.findById(postId);
            Optional<CommentEntity> optionalComment = commentRepository.findById(commentId);
            if(!optionalPost.isPresent()){
                res.setMessage("게시글이존재하지않음");
            }
            else if(!optionalComment.isPresent()){
                res.setMessage("댓글이존재하지않음");
            }else{
                CommentEntity comment = optionalComment.get();
                UserEntity user = userRepository.findById(userId).get();
                ReplyEntity replyEntity = ReplyEntity.builder()
                    .content(content)
                    .regdate(LocalDateTime.now())
                    .commentEntity(comment)
                    .userEntity(user)
                    .build();
                replyRepository.save(replyEntity);
    
                // 해당 댓글에 달린 대댓글 리스트를 조회해서 [comment_id, List<replyDTO>] 형태의 Map 객체를 생성
                Map<Long, List<ReplyDTO>> replyList = new HashMap<>();
    
                List<ReplyEntity> replyEntities = replyRepository.findAllByCommentEntity(comment);
                List<ReplyDTO> replyDtos = replyEntities.stream().map(ReplyDTO::convertToDto).collect(Collectors.toList()); 
                replyList.put(commentId, replyDtos);
                res.setMapData(replyList);
            }
            return res;
        } catch (Exception e) {
            res.setError(e.getMessage());
            return res;
        }
    }

    @Override
    public Map<Long, List<ReplyDTO>> delete(ReplyDTO dto) {
        try {
            Long commentId = dto.getCommentId();

            ReplyEntity replyEntity = ReplyEntity.builder().id(dto.getId()).build();
            replyRepository.delete(replyEntity);

            CommentEntity commentEntity = CommentEntity.builder().id(commentId).build();

            // 해당 댓글에 달린 대댓글 리스트를 조회해서 [comment_id, List<replyDTO>] 형태의 Map 객체를 생성
            Map<Long, List<ReplyDTO>> replyList = new HashMap<>();

            List<ReplyEntity> replyEntities = replyRepository.findAllByCommentEntity(commentEntity);
            List<ReplyDTO> replyDtos = replyEntities.stream().map(ReplyDTO::convertToDto).collect(Collectors.toList()); 
            replyList.put(commentId, replyDtos);
            return replyList;
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }
    
}
