package hideonbush3.springboot.happypet.service;

import hideonbush3.springboot.happypet.dto.AuthMailDTO;
import hideonbush3.springboot.happypet.dto.MailContentDTO;
import hideonbush3.springboot.happypet.dto.ResponseDTO;

public interface MailService {
    public void sendMail(String email, String title, String body);
    public ResponseDTO<Object> sendId(String email);
    ResponseDTO<Object> checkAuthCode(AuthMailDTO dto, String process);
    public ResponseDTO<AuthMailDTO> sendAuthCode(MailContentDTO dto, String createdDate);
}