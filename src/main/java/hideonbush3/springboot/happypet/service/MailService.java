package hideonbush3.springboot.happypet.service;

import hideonbush3.springboot.happypet.dto.ResponseDTO;

public interface MailService {
    public void sendMail(String email, String title, String body);
    public ResponseDTO<Object> sendId(String email);
}