package hideonbush3.springboot.happypet.service;

import java.time.LocalDateTime;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.io.File;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import hideonbush3.springboot.happypet.dto.PostDTO;
import hideonbush3.springboot.happypet.model.ImageEntity;
import hideonbush3.springboot.happypet.model.PostEntity;
import hideonbush3.springboot.happypet.model.UserEntity;
import hideonbush3.springboot.happypet.persistence.ImageRepository;
import hideonbush3.springboot.happypet.persistence.PostRepository;
import hideonbush3.springboot.happypet.persistence.UserRepository;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class PostServiceImpl implements PostService{
    @Value("${image.dir}")
    private String imageDir;

    @Autowired
    private PostRepository postRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ImageRepository imageRepository;

    @Override
    public List<PostDTO> select() {
        List<PostEntity> entities = postRepository.findAll();
        List<PostDTO> dtos = entities.stream().map(PostDTO::convertToDto).collect(Collectors.toList());
        return dtos;
    }

    @Override
    public PostDTO insert(String title, String content, List<MultipartFile> images, String userId) {
        try{
            Optional<UserEntity> optionalUserEntity = userRepository.findById(userId);
            UserEntity userEntity = optionalUserEntity.orElseThrow(() -> new RuntimeException("존재하지 않는 유저"));
            if(images != null) 
                for(MultipartFile image: images){
                    if(!image.getContentType().startsWith("image")) throw new RuntimeException("이미지 파일 아님");
                }

            LocalDateTime regdate = LocalDateTime.now();

            Long order = 1L;

        
            PostEntity postEntity = PostEntity.builder()
                    .title(title)
                    .content(content)
                    .regdate(regdate)
                    .views(0L)
                    .userEntity(userEntity)
                    .commentList(new ArrayList<>())
                    .build();
            
            PostEntity savedPostEntity = postRepository.save(postEntity);
            
            List<ImageEntity> savedImageEntity = new ArrayList<>();

            for(MultipartFile image: images){
                String uuid = (LocalDate.now() + "" + LocalTime.now())
                    .replace(".", "")
                    .replace(":", "")
                    .replace("-", "");
                String fnameAndExtention = image.getOriginalFilename();
                String[] parts = fnameAndExtention.split("\\.");
                String fname = parts[0];
                String ext = parts[1];
                String fnameToSave = fname + " " + uuid + "." + ext;

                image.transferTo(new File(imageDir + fnameToSave));

                Long bytes = image.getSize();
                double kBytes = Math.ceil((double) bytes / 1024 * 100) / 100;
                
                ImageEntity imageEntity = ImageEntity.builder()
                    .name(fname)
                    .ext(ext)
                    .uuid(uuid)
                    .sequence(order)
                    .bytes(bytes)
                    .kBytes(kBytes)
                    .regdate(regdate)
                    .postEntity(PostEntity.builder().id(savedPostEntity.getId()).build())
                    .build();

                    savedImageEntity.add(imageRepository.save(imageEntity));
                    order ++;
            }
            savedPostEntity.setImageList(savedImageEntity);
            return PostDTO.convertToDto(savedPostEntity);
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
