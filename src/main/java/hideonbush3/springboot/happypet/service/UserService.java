package hideonbush3.springboot.happypet.service;

import org.springframework.security.crypto.password.PasswordEncoder;

import hideonbush3.springboot.happypet.dto.ResponseDTO;
import hideonbush3.springboot.happypet.dto.UserDTO;

public interface UserService {
    ResponseDTO<Object> create(UserDTO dto);
    ResponseDTO<Object> getByCredentials(String username, String password, PasswordEncoder passwordEncoder);
    ResponseDTO<UserDTO> select(String userId);
    ResponseDTO<?> delete(String userId);
    ResponseDTO<Object> isExist(UserDTO dto, PasswordEncoder passwordEncoder, String userId, String process);
    ResponseDTO<UserDTO> update(UserDTO userDTO, String userId);
    ResponseDTO<Object> isExistByEmail(String email);
    ResponseDTO<Object> isExistByUserId(String userId);
}
