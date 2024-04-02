package hideonbush3.springboot.happypet.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import hideonbush3.springboot.happypet.dto.ResponseDTO;
import hideonbush3.springboot.happypet.dto.UserDTO;
import hideonbush3.springboot.happypet.service.MailService;
import hideonbush3.springboot.happypet.service.UserService;

@RestController
@RequestMapping("user")
public class UserController {
    
    @Autowired
    private UserService ussrv;

    @Autowired
    private MailService mailService;

    private PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @PostMapping("/signup")
    public ResponseEntity<?> registerUser(@RequestBody UserDTO userDTO){
        ResponseDTO<Object> res = ussrv.create(userDTO);
        return ResponseEntity.ok().body(res);
    }

    @PostMapping("/signin")
    public ResponseEntity<?> authenticate(@RequestBody UserDTO userDTO){
        ResponseDTO<Object> res = ussrv.getByCredentials(
            userDTO.getUsername(),
            userDTO.getPassword(),
            passwordEncoder);
        return ResponseEntity.ok().body(res);
    }
    
    // 로그인한 유저 정보
    @GetMapping
    public ResponseEntity<?> read(@AuthenticationPrincipal String userId){
        ResponseDTO<UserDTO> res = ussrv.select(userId);
        return ResponseEntity.ok().body(res);
    }

    // 내 정보 수정, 탈퇴 시 재인증
    @PostMapping("/reauth")
    public ResponseEntity<?> reauth(
        @RequestBody UserDTO dto,
        @RequestParam String process,
        @AuthenticationPrincipal String userId) {
        ResponseDTO<Object> res = ussrv.isExist(dto, passwordEncoder, userId, process);
        return ResponseEntity.ok().body(res);    
    }
    
    @DeleteMapping("/remove")
    public ResponseEntity<?> remove(@AuthenticationPrincipal String userId){
        ResponseDTO<?> res = ussrv.delete(userId);
        return ResponseEntity.ok().body(res);
    }
    
    @PutMapping("/modify")
    public ResponseEntity<?> modify(@RequestBody UserDTO userDTO) {
        try {
            UserDTO updatedUserInfo = ussrv.update(userDTO, passwordEncoder);
            return ResponseEntity.ok().body(updatedUserInfo);
        } catch (Exception e) {
            ResponseDTO<Object> res = ResponseDTO.builder().error(e.getMessage()).build();
            return ResponseEntity.badRequest().body(res);
        }        
    }

    // 이메일 중복여부
    @GetMapping("/checksignup/email")
    public ResponseEntity<?> readByEmail(@RequestParam String email){
        try {
            return ResponseEntity.ok().body(ussrv.isExistByEmail(email));
        } catch (Exception e) {
            ResponseDTO<Object> res = new ResponseDTO<>();
            res.setError(e.getMessage());
            return ResponseEntity.badRequest().body(res);
        }
    }

    // 아이디찾기
    @GetMapping("/find-id")
    public ResponseEntity<?> findId(@RequestParam String email){
        return ResponseEntity.ok().body(mailService.sendId(email));
    }

    // 비밀번호 찾기(해당 아이디로 가입한 유저가 있는지)
    @GetMapping("/checksignup/id")
    public ResponseEntity<?> isSignup(@RequestParam String userId){
        return ResponseEntity.ok().body(ussrv.isExistByUserId(userId));
    } 
}
