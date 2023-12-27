package hideonbush3.springboot.happypet.service;

import java.util.List;
import java.util.Map;

import hideonbush3.springboot.happypet.dto.ReplyDTO;

public interface ReplyService {
    public Map<Long, List<ReplyDTO>> insert(ReplyDTO dto, String userId);
    public Map<Long, List<ReplyDTO>> delete(ReplyDTO dto);
}
