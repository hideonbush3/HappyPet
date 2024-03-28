package hideonbush3.springboot.happypet.service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import hideonbush3.springboot.happypet.dto.EmailAuthDTO;
import hideonbush3.springboot.happypet.dto.ResponseDTO;
import hideonbush3.springboot.happypet.model.EmailAuthEntity;
import hideonbush3.springboot.happypet.persistence.EmailAuthRepository;
import hideonbush3.springboot.happypet.utils.Utils;

@Service
public class EmailAuthServiceImpl implements EmailAuthService{
    @Autowired
    EmailAuthRepository emailAuthRepository;
    
    @Autowired
    JavaMailSender mailSender;

    @Transactional
    @Override
    public ResponseDTO<Object> select(EmailAuthDTO dto) {
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
                }else res.setMessage("인증성공");
    
                emailAuthRepository.delete(entity);
            }else res.setMessage("틀린인증코드");
            
            return res;
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    @Transactional
    @Override
    public ResponseDTO<EmailAuthDTO> insert(String email, String createdDate) {
        try {
            if(createdDate != null){
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS");
                LocalDateTime dateTime = LocalDateTime.parse(createdDate, formatter);
                emailAuthRepository.deleteByEmailAndCreatedDate(email, dateTime);
            }
            String authCode = Utils.createUuid(0, 8);
            LocalDateTime now = LocalDateTime.now();
            
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(email);
            message.setSubject("HappyPet 이메일 인증코드 입니다");
            message.setText("인증코드는 " + authCode + " 입니다.\n회원가입 페이지로 돌아가 이메일 인증을 완료하세요.");
    
            mailSender.send(message);
            
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
                            