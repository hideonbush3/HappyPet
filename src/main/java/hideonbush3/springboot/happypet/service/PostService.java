package hideonbush3.springboot.happypet.service;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import hideonbush3.springboot.happypet.dto.PostDTO;

public interface PostService {
    public List<PostDTO> select();
    public PostDTO insert(String userId, String title, String content, List<MultipartFile> images, String urlAndName); 
    public PostDTO update(String userId, Long id, String title, String content, List<MultipartFile> images, String urlAndName, String[] imagesToDelete); 
    public boolean delete(Long id);

    public PostDTO selectOne(Long id);

    public List<PostDTO> selectMyPost(String userId);
}
