package hideonbush3.springboot.happypet.service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import hideonbush3.springboot.happypet.dto.AuthMailDTO;
import hideonbush3.springboot.happypet.dto.MailContentDTO;
import hideonbush3.springboot.happypet.dto.ResponseDTO;
import hideonbush3.springboot.happypet.model.AuthMailEntity;
import hideonbush3.springboot.happypet.model.UserEntity;
import hideonbush3.springboot.happypet.persistence.AuthMailRepository;
import hideonbush3.springboot.happypet.persistence.UserRepository;
import hideonbush3.springboot.happypet.utils.Utils;

@Service
public class MailServiceImpl implements MailService{
    @Autowired
    private JavaMailSender sender;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AuthMailRepository authMailRepository;

    PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    
    @Override
    public void sendMail(String email, String title, String body) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(email);
        message.setSubject(title);
        message.setText(body);
        sender.send(message);
    }

    @Override
    public ResponseDTO<Object> sendId(String email) {
        ResponseDTO<Object> res = new ResponseDTO<>();
        try {
            UserEntity user = userRepository.findByEmail(email);
            String userId = user.getUsername();
            sendMail(
                email,
                "Happy Pet 아이디 찾기",
                "찾으시는 아이디는 " + userId + " 입니다");
                
                res.setMessage("전송성공");
            return res;
        } catch (Exception e) {
            res.setError(e.getMessage());
            return res;
        }
    }

    @Transactional
    @Override
    public ResponseDTO<Object> checkAuthCode(AuthMailDTO dto, String process) {
        ResponseDTO<Object> res = new ResponseDTO<>();
        try {
            String email = dto.getEmail();
            String authCode = dto.getAuthCode();
            Optional<AuthMailEntity> optionalEntity = authMailRepository.findByEmailAndAuthCode(email, authCode);
            
            AuthMailEntity entity = optionalEntity.orElse(null);
    
            if(entity != null){
                LocalDateTime now = LocalDateTime.now();
                LocalDateTime createdDate = entity.getCreatedDate();
    
                // 생성시간과 현재시간을 비교
                Duration duration = Duration.between(createdDate, now);
                // 비교기준은 5분
                Duration fiveMinutes = Duration.ofMinutes(5);
                
                // 생성된지 5분이 지났으면
                if(duration.compareTo(fiveMinutes) > 0){
                    res.setMessage("유효시간종료");
                }else{
                    res.setMessage("인증성공");
                    if("password".equals(process)){
                        UserEntity userEntity = userRepository.findByEmail(email);
                        String newPassword = Utils.createUuid(0, 8);
                        String passwordToSave = passwordEncoder.encode(newPassword);
                        userEntity.setPassword(passwordToSave);
                        userRepository.save(userEntity);
                        sendMail(
                            email,
                            "HappyPet 새로운 비밀번호 입니다.",
                            "새로운 비밀번호는 " + newPassword + " 입니다." +
                            "\n마이페이지에서 비밀번호를 변경하실 수 있습니다.");
                    }
                }
    
                authMailRepository.delete(entity);
            }else res.setMessage("틀린인증코드");

            return res;
        } catch (Exception e) {
            res.setError(e.getMessage());
            return res;
        }
    }

    @Transactional
    @Override
    public ResponseDTO<AuthMailDTO> sendAuthCode(MailContentDTO mailContentDTO, String createdDate) {
        try {
            String email = null;
            // 회원가입시 이메일 인증
            if(mailContentDTO.getEmail() != null){
                email = mailContentDTO.getEmail();
            }
            
            // 비밀번호 찾기
            else if(mailContentDTO.getUserId() != null){
                String userId = mailContentDTO.getUserId();
                UserEntity user = userRepository.findByUsername(userId);
                email = user.getEmail();
            }

            // 인증코드 재전송 요청일 경우
            if(createdDate != null){
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS");
                LocalDateTime dateTime = LocalDateTime.parse(createdDate, formatter);
                authMailRepository.deleteByEmailAndCreatedDate(email, dateTime);
            }
            
            String authCode = Utils.createUuid(0, 8);
            LocalDateTime now = LocalDateTime.now();
            
            String title = mailContentDTO.getTitle();
            String body = String.format(mailContentDTO.getBody(), authCode);
            sendMail(email, title, body);
            
            AuthMailEntity authMailEntity = new AuthMailEntity();
            authMailEntity.setAuthCode(authCode);
            authMailEntity.setEmail(email);
            authMailEntity.setCreatedDate(now);

            AuthMailEntity savedEntity = authMailRepository.save(authMailEntity);
            AuthMailDTO dto = AuthMailDTO.convertToDto(savedEntity);
            ResponseDTO<AuthMailDTO> res = new ResponseDTO<>();
            res.setObject(dto);
            return res;
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }   
}