package hideonbush3.springboot.happypet.service;

import hideonbush3.springboot.happypet.dto.CommentDTO;
import hideonbush3.springboot.happypet.dto.ResponseDTO;

public interface CommentService {
    public ResponseDTO<CommentDTO> insert(CommentDTO dto, String userId);
    public ResponseDTO<CommentDTO> delete(CommentDTO dto);
}
