package hideonbush3.springboot.happypet.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import hideonbush3.springboot.happypet.dto.FavoriteDTO;
import hideonbush3.springboot.happypet.dto.ResponseDTO;
import hideonbush3.springboot.happypet.service.FavoriteService;

@RestController
@RequestMapping("favorite")
public class FavoriteController {
    @Autowired
    private FavoriteService favoriteService;
    
    @PostMapping("/is-added")
    public ResponseEntity<?> isExist(@RequestBody FavoriteDTO dto, @AuthenticationPrincipal String userId){
            ResponseDTO<FavoriteDTO> res = favoriteService.selectOne(dto, userId);
            return ResponseEntity.ok().body(res);
        
    }

    // mypage에서 내 즐겨찾기 목록 불러오기
    @GetMapping
    public ResponseEntity<?> retrieve(@AuthenticationPrincipal String userId){
        ResponseDTO<FavoriteDTO> res = favoriteService.select(userId);
        return ResponseEntity.ok().body(res);
    }

    @PostMapping
    public ResponseEntity<?> create(@RequestBody FavoriteDTO dto, @AuthenticationPrincipal String userId){
            ResponseDTO<Object> res = favoriteService.insert(dto, userId);
            return ResponseEntity.ok().body(res);
    }

    @DeleteMapping("/removeinmodal")
    public ResponseEntity<?> removeInModal(@AuthenticationPrincipal String userId, @RequestBody FavoriteDTO dto){
        ResponseDTO<?> res = favoriteService.deleteInModal(dto, userId);
        return ResponseEntity.ok().body(res);
    }
}
