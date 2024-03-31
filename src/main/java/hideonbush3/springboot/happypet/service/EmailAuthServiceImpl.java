package hideonbush3.springboot.happypet.service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import hideonbush3.springboot.happypet.dto.EmailAuthDTO;
import hideonbush3.springboot.happypet.dto.EmailContentDTO;
import hideonbush3.springboot.happypet.dto.ResponseDTO;
import hideonbush3.springboot.happypet.model.EmailAuthEntity;
import hideonbush3.springboot.happypet.model.UserEntity;
import hideonbush3.springboot.happypet.persistence.EmailAuthRepository;
import hideonbush3.springboot.happypet.persistence.UserRepository;
import hideonbush3.springboot.happypet.utils.Utils;

@Service
public class EmailAuthServiceImpl implements EmailAuthService{
    @Autowired
    EmailAuthRepository emailAuthRepository;
    @Autowired
    private MailService mailService;
    @Autowired
    private UserRepository userRepository;

    PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Transactional
    @Override
    public ResponseDTO<Object> select(EmailAuthDTO dto, String process) {
        try {
            ResponseDTO<Object> res = new ResponseDTO<>();
            String email = dto.getEmail();
            String authCode = dto.getAuthCode();
            Optional<EmailAuthEntity> optionalEntity = emailAuthRepository.findByEmailAndAuthCode(email, authCode);
            
            EmailAuthEntity entity = optionalEntity.orElse(null);
    
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
                        mailService.sendMail(
                            email,
                            "HappyPet 새로운 비밀번호 입니다.",
                            "새로운 비밀번호는 " + newPassword + " 입니다." +
                            "\n마이페이지에서 비밀번호를 변경하실 수 있습니다.");
                    }
                }
    
                emailAuthRepository.delete(entity);
            }else res.setMessage("틀린인증코드");
            
            return res;
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    @Transactional
    @Override
    public ResponseDTO<EmailAuthDTO> insert(EmailContentDTO emailContentDTO, String createdDate) {
        try {
            String email = null;
            // 회원가입시 이메일 인증
            if(emailContentDTO.getEmail() != null){
                email = emailContentDTO.getEmail();
            }
            
            // 비밀번호 찾기
            else if(emailContentDTO.getUserId() != null){
                String userId = emailContentDTO.getUserId();
                UserEntity user = userRepository.findByUsername(userId);
                email = user.getEmail();
            }

            // 인증코드 재전송 요청일 경우
            if(createdDate != null){
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS");
                LocalDateTime dateTime = LocalDateTime.parse(createdDate, formatter);
                emailAuthRepository.deleteByEmailAndCreatedDate(email, dateTime);
            }
            
            String authCode = Utils.createUuid(0, 8);
            LocalDateTime now = LocalDateTime.now();
            
            String title = emailContentDTO.getTitle();
            String body = String.format(emailContentDTO.getBody(), authCode);
            mailService.sendMail(email, title, body);
            
            EmailAuthEntity emailAuthEntity = new EmailAuthEntity();
            emailAuthEntity.setAuthCode(authCode);
            emailAuthEntity.setEmail(email);
            emailAuthEntity.setCreatedDate(now);

            EmailAuthEntity savedEntity = emailAuthRepository.save(emailAuthEntity);
            EmailAuthDTO dto = EmailAuthDTO.convertToDto(savedEntity);
            ResponseDTO<EmailAuthDTO> res = new ResponseDTO<>();
            res.setObject(dto);
            return res;
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }    
}