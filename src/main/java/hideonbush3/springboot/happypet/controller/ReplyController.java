package hideonbush3.springboot.happypet.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import hideonbush3.springboot.happypet.dto.ReplyDTO;
import hideonbush3.springboot.happypet.dto.ResponseDTO;
import hideonbush3.springboot.happypet.service.ReplyService;

@RestController
@RequestMapping("reply")
public class ReplyController {
    @Autowired
    private ReplyService replyService;

    // Configuration of RequestBody -> String centent, Long commentId
    @PostMapping("/write")
    public ResponseEntity<?> create(@RequestBody ReplyDTO dto, @AuthenticationPrincipal String userId){
        try {
            Map<Long, List<ReplyDTO>> replyList = replyService.insert(dto, userId);
            return ResponseEntity.ok().body(replyList);
        } catch (Exception e) {
            ResponseDTO<Object> res = ResponseDTO.builder().error(e.getMessage()).build();
            return ResponseEntity.badRequest().body(res);
        }
    }

    // Configuration of RequestBody -> Long id, Long commentId
    @DeleteMapping("/remove")
    public ResponseEntity<?> remove(@RequestBody ReplyDTO dto){
        try {
            Map<Long, List<ReplyDTO>> replyList = replyService.delete(dto);
            return ResponseEntity.ok().body(replyList);
        } catch (Exception e) {
            ResponseDTO<Object> res = ResponseDTO.builder().error(e.getMessage()).build();
            return ResponseEntity.badRequest().body(res);
        }
    }
}
