package hideonbush3.springboot.happypet.service;

import java.util.List;

import hideonbush3.springboot.happypet.dto.CommentDTO;

public interface CommentService {
    public List<CommentDTO> insert(CommentDTO dto, String userId);
    public List<CommentDTO> delete(CommentDTO dto);
}
