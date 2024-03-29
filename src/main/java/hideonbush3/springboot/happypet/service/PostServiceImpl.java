package hideonbush3.springboot.happypet.service;

import java.io.File;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.ArrayDeque;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import hideonbush3.springboot.happypet.dto.PostDTO;
import hideonbush3.springboot.happypet.model.ImageEntity;
import hideonbush3.springboot.happypet.model.PostEntity;
import hideonbush3.springboot.happypet.model.UserEntity;
import hideonbush3.springboot.happypet.persistence.ImageRepository;
import hideonbush3.springboot.happypet.persistence.PostRepository;
import hideonbush3.springboot.happypet.persistence.UserRepository;
import hideonbush3.springboot.happypet.utils.Utils;

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
    @Transactional
    public PostDTO insert(
        String userId, String title, 
        String content, List<MultipartFile> images, 
        String urlAndName) {
        try{
            // Validation
            Optional<UserEntity> optionalUserEntity = userRepository.findById(userId);
            UserEntity userEntity = optionalUserEntity.orElseThrow(() -> new RuntimeException("존재하지 않는 유저"));

            LocalDateTime regdate = LocalDateTime.now();
            String uuid = (LocalDate.now() + "" + LocalTime.now())
            .replace(".", "")
            .replace(":", "")
            .replace("-", "");
            ArrayDeque<String> uuidArr = new ArrayDeque<>();

            if(urlAndName != null){
                ObjectMapper objectMapper = new ObjectMapper();
                Map<String, String> map = null;
                try{
                    map = objectMapper.readValue(urlAndName, new TypeReference<Map<String, String>>(){});
                }catch(Exception e){
                    throw new RuntimeException("urlAndName 형변환 실패");
                }
    
                for(Map.Entry<String, String> entry : map.entrySet()){
                    String uuidString = uuid + Utils.createUuid(0, 4);
                    
                    String key = entry.getKey();
                    String value = entry.getValue();
                    String[] parts = value.split("\\.");
                    String src = "http://localhost/images/" + parts[0] + " " + uuidString + "." + parts[1];
                    content = content.replace(key, src);

                    uuidArr.add(uuidString);
                }
            }

            PostEntity postEntity = PostEntity.builder()
                    .title(title)
                    .content(content)
                    .regdate(regdate)
                    .views(0L)
                    .userEntity(userEntity)
                    .commentList(new ArrayList<>())
                    .build();
            
            PostEntity savedPostEntities = postRepository.save(postEntity);
            
            List<ImageEntity> savedImageEntities = new ArrayList<>();

            if(urlAndName != null){
                for(MultipartFile image: images){
                    String nameAndExt = image.getOriginalFilename();
                    String[] parts = nameAndExt.split("\\.");
                    String name = parts[0];
                    String ext = parts[1];
                    String nameToSave = name + " " + uuidArr.poll() + "." + ext;
    
                    image.transferTo(new File(imageDir + nameToSave));
                    
                    ImageEntity imageEntity = ImageEntity.builder()
                        .name(nameToSave)
                        .bytes(image.getSize())
                        .regdate(regdate)
                        .postEntity(PostEntity.builder().id(savedPostEntities.getId()).build())
                        .build();
    
                    savedImageEntities.add(imageRepository.save(imageEntity));
                }
            }

            savedPostEntities.setImageList(savedImageEntities);
            return PostDTO.convertToDto(savedPostEntities);
        }catch(Exception e){
            throw new RuntimeException(e.getMessage());
        }
    }
    
    @Override
    @Transactional
    public PostDTO update(
        String userId, Long id,
        String title, String content, 
        List<MultipartFile> images, String urlAndName, 
        String[] imagesToDelete) {
        userRepository.findById(userId).orElseThrow(() -> new RuntimeException("존재하지 않는 유저"));
        
        try{
            Optional<PostEntity> origin = postRepository.findById(id);

            origin.ifPresent(post -> {
                post.setTitle(title);
                post.setContent(content);

                postRepository.save(post);
            });
            if (imagesToDelete.length > 0) {
                Arrays.stream(imagesToDelete)
                      .peek(imageRepository::deleteByName)
                      .map(imageName -> new File(imageDir + imageName))
                      .forEach(File::delete);
            }

            PostEntity updatedEntity = postRepository.findById(id).get();
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
