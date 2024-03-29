package hideonbush3.springboot.happypet.service;

import hideonbush3.springboot.happypet.dto.EmailAuthDTO;
import hideonbush3.springboot.happypet.dto.EmailContentDTO;
import hideonbush3.springboot.happypet.dto.ResponseDTO;

public interface EmailAuthService {
    public ResponseDTO<EmailAuthDTO> insert(String email, EmailContentDTO emailContentDTO, String createdDate);
    public ResponseDTO<Object> select(EmailAuthDTO dto);
}
