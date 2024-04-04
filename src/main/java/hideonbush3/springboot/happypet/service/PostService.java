package hideonbush3.springboot.happypet.service;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import hideonbush3.springboot.happypet.dto.PostDTO;
import hideonbush3.springboot.happypet.dto.ResponseDTO;

public interface PostService {
    public ResponseDTO<PostDTO> select();
    public ResponseDTO<PostDTO> insert(String userId, String title, String content, List<MultipartFile> images, String urlAndName); 
    public ResponseDTO<PostDTO> update(String userId, Long id, String title, String content, List<MultipartFile> images, String urlAndName, String[] imagesToDelete); 
    public ResponseDTO<?> delete(Long id);
    public ResponseDTO<PostDTO> selectOne(Long id);
    public ResponseDTO<PostDTO> selectMyPost(String userId);
}
