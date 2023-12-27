package hideonbush3.springboot.happypet.service;

import org.springframework.security.crypto.password.PasswordEncoder;

import hideonbush3.springboot.happypet.dto.ResponseDTO;
import hideonbush3.springboot.happypet.dto.UserDTO;
import hideonbush3.springboot.happypet.model.UserEntity;

public interface UserService {
    UserEntity create(UserEntity userEntity);
    ResponseDTO<?> getByCredentials(String username, String password, PasswordEncoder passwordEncoder);
    UserDTO select(String userId);
    void delete(String userId);
    UserDTO isExist(UserDTO userDTO, PasswordEncoder passwordEncoder, String userId);
    UserDTO update(UserDTO userDTO, PasswordEncoder passwordEncoder);
}
