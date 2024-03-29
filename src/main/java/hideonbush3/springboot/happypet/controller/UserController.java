package hideonbush3.springboot.happypet.controller;

import java.util.ArrayList;
import java.util.List;

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
import hideonbush3.springboot.happypet.model.UserEntity;
import hideonbush3.springboot.happypet.service.UserService;

@RestController
@RequestMapping("user")
public class UserController {
    
    @Autowired
    private UserService ussrv;

    private PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @PostMapping("/signup")
    public ResponseEntity<?> registerUser(@RequestBody UserDTO userDTO){
        try {
            if(userDTO == null || userDTO.getPassword() == null){
                throw new RuntimeException("비밀번호를 다시 확인하세요");
            }

            UserEntity user = UserEntity.builder()
                .username(userDTO.getUsername())
                .password(passwordEncoder.encode(userDTO.getPassword()))
                .nickname(userDTO.getNickname())
                .email(userDTO.getEmail())
                .build();

            UserEntity registeredUser = ussrv.create(user);
            UserDTO resUserDTO = UserDTO.builder()
                .id(registeredUser.getId())
                .username(userDTO.getUsername())
                .build();
        
            return ResponseEntity.ok().body(resUserDTO);
        } catch (Exception e) {
            ResponseDTO<Object> res = ResponseDTO.builder().error(e.getMessage()).build();
            return ResponseEntity.badRequest().body(res);
        }
    }

    @PostMapping("/signin")
    public ResponseEntity<?> authenticate(@RequestBody UserDTO userDTO){
        ResponseDTO<?> res = ussrv.getByCredentials(
            userDTO.getUsername(),
            userDTO.getPassword(),
            passwordEncoder);

        if(res.getObject() != null){
            return ResponseEntity.ok().body(res);
        }else{
            return ResponseEntity.badRequest().body(res);
        }
    }
    
    // 로그인한 유저 정보
    @GetMapping
    public ResponseEntity<?> read(@AuthenticationPrincipal String userId){
        try {
            UserDTO res = ussrv.select(userId);
            return ResponseEntity.ok().body(res); 
        } catch (Exception e) {
            String error = e.getMessage();
            ResponseDTO<Object> res = ResponseDTO.builder().error(error).build();
            return ResponseEntity.badRequest().body(res);
        }
    }

    // 내 정보 수정, 탈퇴 시 재인증
    @PostMapping("/reauth")
    public ResponseEntity<?> reauth(@RequestBody UserDTO userDTO, @RequestParam String process, @AuthenticationPrincipal String userId) {
        UserDTO user = ussrv.isExist(userDTO, passwordEncoder, userId);
        if(user != null){
            List<UserDTO> entity = new ArrayList<UserDTO>();
            entity.add(user);
            ResponseDTO<UserDTO> res = ResponseDTO.<UserDTO>builder().data(entity).message(process).build();
            return ResponseEntity.ok().body(res);
        }else{
            ResponseDTO<Object> res = ResponseDTO.builder().message("비밀번호 틀림").build();
            return ResponseEntity.badRequest().body(res);
        }    
    }
    
    @DeleteMapping("/remove")
    public ResponseEntity<?> remove(@AuthenticationPrincipal String userId){
        try {
            ussrv.delete(userId);
            ResponseDTO<Object> res = ResponseDTO.<Object>builder().message("탈퇴완료").build();
            return ResponseEntity.ok().body(res);   
        } catch (Exception e) {
            ResponseDTO<Object> res = ResponseDTO.<Object>builder().error(e.getMessage()).build();
            return ResponseEntity.badRequest().body(res);
        }
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
    
}
