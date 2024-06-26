package hideonbush3.springboot.happypet.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import hideonbush3.springboot.happypet.dto.PostDTO;
import hideonbush3.springboot.happypet.dto.ResponseDTO;
import hideonbush3.springboot.happypet.service.PostService;

@RestController
@RequestMapping("post")
public class PostController {
    @Autowired
    private PostService postService;

    @GetMapping
    public ResponseEntity<?> retrieve(){
        ResponseDTO<PostDTO> res = postService.select();
        return ResponseEntity.ok().body(res);
    }

    // Configuration of request body -> FormData{title, content}
    @PostMapping
    public ResponseEntity<?> create(
        @AuthenticationPrincipal String userId,
        @RequestParam("title") String title, 
        @RequestParam("content") String content,
        @RequestParam(value="images", required = false) List<MultipartFile> images,
        @RequestParam(value="urlAndName", required = false) String urlAndName){
                return ResponseEntity.ok().body(postService.insert(userId, title, content, images, urlAndName));
    }

    // Configuration of request body -> title, content
    @PutMapping("/modify")
    public ResponseEntity<?> modify(
        @AuthenticationPrincipal String userId,
        @RequestParam("postId") Long id,
        @RequestParam("title") String title,
        @RequestParam("content") String content,
        @RequestParam(value="images", required = false) List<MultipartFile> images,
        @RequestParam(value="urlAndName", required = false) String urlAndName,
        @RequestParam(value="imagesToDelete", required = false) String[] imagesToDelete){
            ResponseDTO<PostDTO> res = postService.update(userId, id, title, content, images, urlAndName, imagesToDelete);
            return ResponseEntity.ok().body(res);
    }

    // Configuration of request body -> id
    @DeleteMapping("/remove")
    public ResponseEntity<?> remove(@RequestParam Long id){
        ResponseDTO<?> res = postService.delete(id);
        return ResponseEntity.ok().body(res);
    }

    @GetMapping("/view")
    public ResponseEntity<?> readOne(@RequestParam Long id){
        ResponseDTO<PostDTO> res = postService.selectOne(id);
        return ResponseEntity.ok().body(res);
    }

    @GetMapping("/my-post")
    public ResponseEntity<?> retrieveMyPost(@AuthenticationPrincipal String userId){
        ResponseDTO<PostDTO> res = postService.selectMyPost(userId);
        return ResponseEntity.ok().body(res);
    }
}
