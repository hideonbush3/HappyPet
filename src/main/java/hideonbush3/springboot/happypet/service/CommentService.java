package hideonbush3.springboot.happypet.service;

import java.util.List;

import hideonbush3.springboot.happypet.dto.CommentDTO;
import hideonbush3.springboot.happypet.dto.ResponseDTO;

public interface CommentService {
    public ResponseDTO<CommentDTO> insert(CommentDTO dto, String userId);
    public List<CommentDTO> delete(CommentDTO dto);
}
