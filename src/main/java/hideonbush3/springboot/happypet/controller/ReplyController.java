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
import org.springframework.web.bind.annotation.RequestParam;
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
    public ResponseEntity<?> create(
        @RequestBody ReplyDTO dto, 
        @AuthenticationPrincipal String userId,
        @RequestParam Long postId){
            ResponseDTO<ReplyDTO> res = replyService.insert(dto, userId, postId);
            return ResponseEntity.ok().body(res);
        }

    // Configuration of RequestBody -> Long id, Long commentId
    @DeleteMapping("/remove")
    public ResponseEntity<?> remove(@RequestBody ReplyDTO dto){
        ResponseDTO<ReplyDTO> res = replyService.delete(dto);
        return ResponseEntity.ok().body(res);
    }
}
