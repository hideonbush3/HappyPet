package hideonbush3.springboot.happypet.service;

import hideonbush3.springboot.happypet.dto.ReplyDTO;
import hideonbush3.springboot.happypet.dto.ResponseDTO;

public interface ReplyService {
    public ResponseDTO<ReplyDTO> insert(ReplyDTO dto, String userId, Long postId);
    public ResponseDTO<ReplyDTO> delete(ReplyDTO dto);
}
