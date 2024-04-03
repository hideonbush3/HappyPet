package hideonbush3.springboot.happypet.service;

import java.util.List;
import java.util.Map;

import hideonbush3.springboot.happypet.dto.ReplyDTO;
import hideonbush3.springboot.happypet.dto.ResponseDTO;

public interface ReplyService {
    public ResponseDTO<ReplyDTO> insert(ReplyDTO dto, String userId, Long postId);
    public Map<Long, List<ReplyDTO>> delete(ReplyDTO dto);
}
