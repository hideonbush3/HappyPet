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
import hideonbush3.springboot.happypet.model.CommentEntity;
import hideonbush3.springboot.happypet.model.ReplyEntity;
import hideonbush3.springboot.happypet.model.UserEntity;
import hideonbush3.springboot.happypet.persistence.CommentRepository;
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

    @Override
    public Map<Long, List<ReplyDTO>> insert(ReplyDTO dto, String userId) {
        try {
            Long commentId = dto.getCommentId();

            String content = dto.getContent();

            Optional<CommentEntity> optionalCommentEntity = commentRepository.findById(commentId);
            CommentEntity commentEntity = optionalCommentEntity.orElseThrow(() -> new RuntimeException("게시글이 존재하지 않음"));
            Optional<UserEntity> optionalUserEntity = userRepository.findById(userId);
            UserEntity userEntity = optionalUserEntity.orElseThrow(() -> new RuntimeException("유저가 존재하지 않음"));

            ReplyEntity replyEntity = ReplyEntity.builder()
                .content(content)
                .regdate(LocalDateTime.now())
                .commentEntity(commentEntity)
                .userEntity(userEntity)
                .build();
            replyRepository.save(replyEntity);

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
