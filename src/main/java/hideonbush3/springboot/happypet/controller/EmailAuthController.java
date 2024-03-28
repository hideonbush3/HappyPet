package hideonbush3.springboot.happypet.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import hideonbush3.springboot.happypet.dto.EmailAuthDTO;
import hideonbush3.springboot.happypet.dto.ResponseDTO;
import hideonbush3.springboot.happypet.service.EmailAuthService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;



@RestController
@RequestMapping("emailauth")
public class EmailAuthController {
    @Autowired
    EmailAuthService emailAuthService;

    // 인증코드 확인
    // Configuration of RequestBody -> String email, String authCode
    @DeleteMapping
    public ResponseEntity<?> retrieve(@RequestBody EmailAuthDTO dto) {
        try {
            ResponseDTO<Object> res = emailAuthService.select(dto);
            return ResponseEntity.ok().body(res);
        } catch (Exception e) {
            ResponseDTO<Object> res =  new ResponseDTO<>();
            res.setError(e.getMessage());
            return ResponseEntity.badRequest().body(res);
        }
    }

    // 인증코드 생성, 재생성
    @PostMapping
    public ResponseEntity<?> create(@RequestParam String email, @RequestParam(required = false) String createdDate) {
        try {
            ResponseDTO<EmailAuthDTO> res = emailAuthService.insert(email, createdDate);
            return ResponseEntity.ok().body(res);
        } catch (Exception e) {
            ResponseDTO<Object> res = new ResponseDTO<>();
            res.setError(e.getMessage());
            return ResponseEntity.badRequest().body(res);
        }
    }

    // 
    // Configuration of RequestBody -> String email, String createdDate
    @PutMapping
    public ResponseEntity<?> remove(@RequestBody EmailAuthDTO dto){
        try {
            ResponseDTO<EmailAuthDTO> res = emailAuthService.delete(dto);
            return ResponseEntity.ok().body(res);
        } catch (Exception e) {
            ResponseDTO<Object> res = new ResponseDTO<>();
            res.setError(e.getMessage());
            return ResponseEntity.badRequest().body(res);
        }
    }
    
    
}