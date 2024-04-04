package hideonbush3.springboot.happypet.service;

import java.io.File;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import java.util.ArrayList;
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
import hideonbush3.springboot.happypet.dto.ResponseDTO;
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
    public ResponseDTO<PostDTO> insert(
        String userId, String title, 
        String content, List<MultipartFile> images, 
        String urlAndName) {
        ResponseDTO<PostDTO> res = new ResponseDTO<>();
        try{
            UserEntity user = userRepository.findById(userId).get();

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
                    .userEntity(user)
                    .commentList(new ArrayList<>())
                    .build();
            
            PostEntity savedPost = postRepository.save(postEntity);
            
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
                        .postEntity(PostEntity.builder().id(savedPost.getId()).build())
                        .build();
    
                    savedImageEntities.add(imageRepository.save(imageEntity));
                }
            }

            savedPost.setImageList(savedImageEntities);
            res.setObject(PostDTO.convertToDto(savedPost));
            return res;
        }catch(Exception e){
            res.setError(e.getMessage());
            return res;
        }
    }
    
    @Override
    @Transactional
    public ResponseDTO<PostDTO> update(
        String userId, Long id,
        String title, String content, 
        List<MultipartFile> images, String urlAndName, 
        String[] imagesToDelete) {
            ResponseDTO<PostDTO> res = new ResponseDTO<>();        
            try{
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

                PostEntity postToUpdate = postRepository.findById(id).get();

                postToUpdate.setTitle(title);
                postToUpdate.setContent(content);
                PostEntity updatedPost = postRepository.save(postToUpdate);

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
                            .postEntity(PostEntity.builder().id(updatedPost.getId()).build())
                            .build();
        
                        savedImageEntities.add(imageRepository.save(imageEntity));
                    }
                }
                
                if (imagesToDelete.length > 0) {
                    for(int i = 0; i < imagesToDelete.length; i++){
                        imageRepository.deleteByName(imagesToDelete[i]);
                        File fileToDelete = new File(imageDir + imagesToDelete[i]);
                        fileToDelete.delete();
                    }
                    updatedPost = postRepository.findById(id).get();
                }
                
                if(savedImageEntities.size() != 0){
                    List<ImageEntity> originImageEntities = updatedPost.getImageList();
                    for(int i = 0; i < savedImageEntities.size(); i++){
                        originImageEntities.add(savedImageEntities.get(i));
                    }
                    updatedPost.setImageList(originImageEntities);
                }

                res.setObject(PostDTO.convertToDto(updatedPost));
                return res;
            }catch(Exception e){
                res.setError(e.getMessage());
                return res;
            }
        }

    @Override
    public ResponseDTO<?> delete(Long id) {
        ResponseDTO<?> res = new ResponseDTO<>();
        try{
            Optional<PostEntity> optionalPost = postRepository.findById(id);
            if(!optionalPost.isPresent()){
                res.setMessage("존재하지않음");
            }

            PostEntity postToDelete = optionalPost.get();
            List<ImageEntity> imgs = postToDelete.getImageList();
            if (!imgs.isEmpty()) {
                for (ImageEntity img : imgs) {
                    File fileToDelete = new File(imageDir + img.getName());
                    fileToDelete.delete();
                }
            }

            postRepository.deleteById(id);
            res.setMessage("삭제완료");
            return res;
        }catch(Exception e){
            res.setError(e.getMessage());
            return res;
        }
    }

    @Override
    public ResponseDTO<PostDTO> selectOne(Long id) {
        ResponseDTO<PostDTO> res = new ResponseDTO<>();
        try {
            Optional<PostEntity> optionalPost = postRepository.findById(id);
            if(!optionalPost.isPresent()){
                res.setMessage("존재하지않는게시글");
            }else{
                PostEntity post = optionalPost.get();
                post.setViews(post.getViews() + 1);
                postRepository.save(post);
                PostEntity entity = postRepository.findById(id).get();
                res.setObject(PostDTO.convertToDto(entity));
            }
            return res;
        } catch (Exception e) {
            res.setError(e.getMessage());
            return res;
        }

    }

    @Override
    public ResponseDTO<PostDTO> selectMyPost(String userId) {
        ResponseDTO<PostDTO> res = new ResponseDTO<>();
        try {
            UserEntity userEntity = UserEntity.builder().id(userId).build();
            List<PostEntity> entities = postRepository.findAllByUserEntity(userEntity);
            List<PostDTO> dtos = entities.stream().map(PostDTO::convertToDto).collect(Collectors.toList());
            res.setData(dtos);
            return res;
        } catch (Exception e) {
            res.setError(e.getMessage());
            return res;
        }
    }

    
}
