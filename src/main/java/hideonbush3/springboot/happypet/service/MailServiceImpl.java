package hideonbush3.springboot.happypet.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import hideonbush3.springboot.happypet.dto.ResponseDTO;
import hideonbush3.springboot.happypet.model.UserEntity;
import hideonbush3.springboot.happypet.persistence.UserRepository;

@Service
public class MailServiceImpl implements MailService{
    @Autowired
    private JavaMailSender sender;

    @Autowired
    private UserRepository userRepository;

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

    
}