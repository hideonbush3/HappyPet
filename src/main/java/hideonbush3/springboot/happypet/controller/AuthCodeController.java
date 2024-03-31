package hideonbush3.springboot.happypet.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import hideonbush3.springboot.happypet.dto.AuthMailDTO;
import hideonbush3.springboot.happypet.dto.MailContentDTO;
import hideonbush3.springboot.happypet.dto.ResponseDTO;
import hideonbush3.springboot.happypet.service.MailService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;



@RestController
@RequestMapping("auth-code")
public class AuthCodeController {
    @Autowired
    private MailService mailService;

    // 인증코드 확인
    // Configuration of RequestBody -> String email, String authCode
    
    @DeleteMapping("/check")
    public ResponseEntity<?> retrieve(
        @RequestParam(required = false) String process,
        @RequestBody AuthMailDTO dto) {
        try {
            ResponseDTO<Object> res = mailService.checkAuthCode(dto, process);
            return ResponseEntity.ok().body(res);
        } catch (Exception e) {
            ResponseDTO<Object> res =  new ResponseDTO<>();
            res.setError(e.getMessage());
            return ResponseEntity.badRequest().body(res);
        }
    }

    // 인증코드 생성, 재생성
    // Configuration of RequestBody -> String email, String userId, String title, String body
    @PostMapping("/create")
    public ResponseEntity<?> create(
        @RequestParam(required = false) String createdDate,
        @RequestBody MailContentDTO emailContentDTO) {
        try {
            ResponseDTO<AuthMailDTO> res = mailService.sendAuthCode(emailContentDTO, createdDate);
            return ResponseEntity.ok().body(res);
        } catch (Exception e) {
            ResponseDTO<Object> res = new ResponseDTO<>();
            res.setError(e.getMessage());
            return ResponseEntity.badRequest().body(res);
        }
    }   
}
