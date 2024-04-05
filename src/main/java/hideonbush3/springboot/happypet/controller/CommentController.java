package hideonbush3.springboot.happypet.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import hideonbush3.springboot.happypet.dto.CommentDTO;
import hideonbush3.springboot.happypet.dto.ResponseDTO;
import hideonbush3.springboot.happypet.service.CommentService;

@RestController
@RequestMapping("comment")
public class CommentController {
    @Autowired
    private CommentService commentService;
    
    // Configuration of RequestBody -> String content, Long postId
    @PostMapping("/write")
    public ResponseEntity<?> create(@RequestBody CommentDTO dto, @AuthenticationPrincipal String userId){
        ResponseDTO<CommentDTO> res = commentService.insert(dto, userId);
        return ResponseEntity.ok().body(res);
    }

    // Configuration of RequestBody -> Long id, Long postId
    @DeleteMapping("/remove")
    public ResponseEntity<?> remove(@RequestBody CommentDTO dto){
        ResponseDTO<CommentDTO> res = commentService.delete(dto);
        return ResponseEntity.ok().body(res);
    }
}
