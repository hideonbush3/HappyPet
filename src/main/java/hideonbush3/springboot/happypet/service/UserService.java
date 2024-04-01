package hideonbush3.springboot.happypet.service;

import org.springframework.security.crypto.password.PasswordEncoder;

import hideonbush3.springboot.happypet.dto.ResponseDTO;
import hideonbush3.springboot.happypet.dto.UserDTO;
import hideonbush3.springboot.happypet.model.UserEntity;

public interface UserService {
    UserEntity create(UserEntity userEntity);
    ResponseDTO<Object> getByCredentials(String username, String password, PasswordEncoder passwordEncoder);
    UserDTO select(String userId);
    ResponseDTO<?> delete(String userId);
    ResponseDTO<Object> isExist(UserDTO dto, PasswordEncoder passwordEncoder, String userId, String process);
    UserDTO update(UserDTO userDTO, PasswordEncoder passwordEncoder);
    ResponseDTO<Object> isExistByEmail(String email);
    ResponseDTO<Object> isExistByUserId(String userId);
}
