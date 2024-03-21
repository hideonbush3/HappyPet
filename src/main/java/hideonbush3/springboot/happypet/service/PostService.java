package hideonbush3.springboot.happypet.service;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import hideonbush3.springboot.happypet.dto.PostDTO;

public interface PostService {
    public List<PostDTO> select();
    public PostDTO insert(String title, String content, List<MultipartFile> images, String urlAndName, String userId); 
    public PostDTO update(PostDTO dto, String userId);
    public boolean delete(Long id);

    public PostDTO selectOne(Long id);

    public List<PostDTO> selectMyPost(String userId);
}
