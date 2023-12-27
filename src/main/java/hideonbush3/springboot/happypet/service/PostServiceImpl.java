package hideonbush3.springboot.happypet.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import hideonbush3.springboot.happypet.dto.PostDTO;
import hideonbush3.springboot.happypet.model.PostEntity;
import hideonbush3.springboot.happypet.model.UserEntity;
import hideonbush3.springboot.happypet.persistence.PostRepository;
import hideonbush3.springboot.happypet.persistence.UserRepository;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class PostServiceImpl implements PostService{
    @Autowired
    private PostRepository postRepository;
    @Autowired
    private UserRepository userRepository;

    @Override
    public List<PostDTO> select() {
        List<PostEntity> entities = postRepository.findAll();
        List<PostDTO> dtos = entities.stream().map(PostDTO::convertToDto).collect(Collectors.toList());
        return dtos;
    }

    @Override
    public PostDTO insert(PostDTO dto, String userId) {
        try{
            Optional<UserEntity> optionalUserEntity = userRepository.findById(userId);
            UserEntity userEntity = optionalUserEntity.orElseThrow(() -> new RuntimeException("존재하지 않는 유저"));

            PostEntity postEntity = PostEntity.builder()
                    .title(dto.getTitle())
                    .content(dto.getContent())
                    .regdate(LocalDateTime.now())
                    .views(0L)
                    .userEntity(userEntity)
                    .commentList(new ArrayList<>())
                    .build();
            
            PostEntity savedEntity = postRepository.save(postEntity);
            return PostDTO.convertToDto(savedEntity);
        }catch(Exception e){
            throw new RuntimeException(e.getMessage());
        }
        
    }
    
    @Override
    public PostDTO update(PostDTO dto, String userId) {
        try{
            Optional<PostEntity> origin = postRepository.findById(dto.getId());

            origin.ifPresent(post -> {
                post.setTitle(dto.getTitle());
                post.setContent(dto.getContent());

                postRepository.save(post);
            });

            PostEntity updatedEntity = postRepository.findById(dto.getId()).get();
            return PostDTO.convertToDto(updatedEntity);
        }catch(Exception e){
            throw new RuntimeException(e.getMessage());
        }

    }

    @Override
    public boolean delete(Long id) {
        Optional<PostEntity> entityForDelete = postRepository.findById(id);
        if(entityForDelete.isPresent()){
            postRepository.deleteById(id);
            return true;
        }
        throw new RuntimeException("존재하지 않는 게시물");
    }

    @Override
    public PostDTO selectOne(Long id) {
        Optional<PostEntity> origin = postRepository.findById(id);
        origin.ifPresent(post -> {
            post.setViews(post.getViews() + 1);
            postRepository.save(post);
        });

        PostEntity entity = postRepository.findById(id).get();        
        return PostDTO.convertToDto(entity);
    }

    @Override
    public List<PostDTO> selectMyPost(String userId) {
        try {
            UserEntity userEntity = UserEntity.builder().id(userId).build();
            List<PostEntity> entities = postRepository.findAllByUserEntity(userEntity);
            List<PostDTO> dtos = entities.stream().map(PostDTO::convertToDto).collect(Collectors.toList());
            return dtos;
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    
}
