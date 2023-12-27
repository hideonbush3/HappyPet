package hideonbush3.springboot.happypet.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import hideonbush3.springboot.happypet.dto.PostDTO;
import hideonbush3.springboot.happypet.dto.ResponseDTO;
import hideonbush3.springboot.happypet.service.PostService;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("post")
public class PostController {
    @Autowired
    private PostService postService;

    @GetMapping
    public ResponseEntity<?> retrieve(){
        try {
            List<PostDTO> dtos = postService.select();
            ResponseDTO<PostDTO> res = ResponseDTO.<PostDTO>builder().data(dtos).build();
            return ResponseEntity.ok().body(res);
        } catch (Exception e) {
            ResponseDTO<Object> res = ResponseDTO.<Object>builder().error(e.getMessage()).build();
            return ResponseEntity.badRequest().body(res);
        }

    }

    // Configuration of request body -> title, content
    @PostMapping
    public ResponseEntity<?> create(@RequestBody PostDTO dto, @AuthenticationPrincipal String userId){
        try {
            return ResponseEntity.ok().body(postService.insert(dto, userId));
        } catch (Exception e) {
            ResponseDTO<Object> res = ResponseDTO.builder().error(e.getMessage()).build();
            return ResponseEntity.badRequest().body(res);
        }
    }

    // Configuration of request body -> title, content
    @PutMapping("/modify")
    public ResponseEntity<?> modify(@RequestBody PostDTO dto, @AuthenticationPrincipal String userId){
        try {
            return ResponseEntity.ok().body(postService.update(dto, userId));
        } catch (Exception e) {
            ResponseDTO<Object> res = ResponseDTO.builder().error(e.getMessage()).build();
            return ResponseEntity.badRequest().body(res);
        }
    }

    // Configuration of request body -> id
    @DeleteMapping("/remove")
    public ResponseEntity<?> remove(@RequestParam Long id){
        try{
            boolean success = postService.delete(id);
            return ResponseEntity.ok().body(success);
        }catch(Exception e){
            ResponseDTO<Object> res = ResponseDTO.builder().error(e.getMessage()).build();
            return ResponseEntity.badRequest().body(res);
        }
    }

    @GetMapping("/view")
    public ResponseEntity<?> readOne(@RequestParam Long id){
        try {
            return ResponseEntity.ok().body(postService.selectOne(id));
        } catch (Exception e) {
            ResponseDTO<Object> res = ResponseDTO.builder().error(e.getMessage()).build();
            return ResponseEntity.badRequest().body(res);
        }
    }

    @GetMapping("/mypost")
    public ResponseEntity<?> retrieveMyPost(@AuthenticationPrincipal String userId){
        try {
            List<PostDTO> dtos = postService.selectMyPost(userId);
            ResponseDTO<PostDTO> res = ResponseDTO.<PostDTO>builder().data(dtos).build();
            return ResponseEntity.ok().body(res);
        } catch (Exception e) {
            ResponseDTO<Object> res = ResponseDTO.builder().error(e.getMessage()).build();
            return ResponseEntity.badRequest().body(res);
        }
    }
}