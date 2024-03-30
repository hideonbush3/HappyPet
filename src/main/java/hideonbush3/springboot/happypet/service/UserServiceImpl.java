package hideonbush3.springboot.happypet.service;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import hideonbush3.springboot.happypet.dto.ResponseDTO;
import hideonbush3.springboot.happypet.dto.UserDTO;
import hideonbush3.springboot.happypet.model.UserEntity;
import hideonbush3.springboot.happypet.persistence.CommentRepository;
import hideonbush3.springboot.happypet.persistence.ReplyRepository;
import hideonbush3.springboot.happypet.persistence.UserRepository;
import hideonbush3.springboot.happypet.security.TokenProvider;

@Service("ussrv")
public class UserServiceImpl implements UserService{

    @Autowired
    private UserRepository userRepository;

    @Autowired 
    private CommentRepository commentRepository;

    @Autowired
    private ReplyRepository replyRepository;

    @Autowired
    private TokenProvider tokenProvider;

    @Override
    public UserEntity create(final UserEntity userEntity) {
        if(userEntity == null || userEntity.getUsername() == null){
            throw new RuntimeException("유효하지 않은 인자");
        }
        final String username = userEntity.getUsername();
        if(userRepository.existsByUsername(username)){
            throw new RuntimeException("아이디 중복");
        }
        
        if(userRepository.existsByNickname(userEntity.getNickname())){
            throw new RuntimeException("닉네임 중복");
        }

        return userRepository.save(userEntity);
    }

    @Override
    public ResponseDTO<?> getByCredentials(final String username, final String password, final PasswordEncoder encoder) {
        final UserEntity userEntity = userRepository.findByUsername(username);
        if(userEntity == null) return ResponseDTO.builder().error("아이디").build();
        if(userEntity != null && encoder.matches(password, userEntity.getPassword())){
            String token = tokenProvider.create(userEntity);
            UserDTO userDTO = UserDTO.builder()
                .username(username)
                .nickname(userEntity.getNickname())
                .token(token)
                .build();
            return ResponseDTO.builder().object(userDTO).build();
        }else{
            return ResponseDTO.builder().error("비밀번호").build();
        }
    }

    @Override
    public UserDTO select(String userId) {
        UserEntity sessionUser = userRepository.findById(userId).get();
        return UserDTO.convertToDto(sessionUser);
    }

    @Transactional
    @Override
    public void delete(String userId) {
        try {                 
            UserEntity userEntity = userRepository.findById(userId).get();
            replyRepository.deleteAllByUserEntity(userEntity);
            commentRepository.deleteAllByUserEntity(userEntity);
            userRepository.delete(userEntity);  
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public UserDTO isExist(UserDTO userDTO, PasswordEncoder passwordEncoder, String userId) {
        String password = userDTO.getPassword();
        UserEntity originUser = userRepository.findById(userId).get();
        if(passwordEncoder.matches(password, originUser.getPassword())){
            return UserDTO.convertToDto(originUser);
        }else{
            return null;
        }
    }

    // 내 정보 수정
    @Override
    public UserDTO update(UserDTO userDTO, PasswordEncoder passwordEncoder) {
        String[] usernames = userDTO.getUsername().split("/");
        String newUsername = usernames[1];
        UserEntity existingUser = userRepository.findByUsername(usernames[0]);

        if(!usernames[0].equals(newUsername) && userRepository.existsByUsername(newUsername)){
            throw new RuntimeException("아이디 중복");
        }
        if(!existingUser.getNickname().equals(userDTO.getNickname()) && userRepository.existsByNickname(userDTO.getNickname())){
            throw new RuntimeException("닉네임 중복");
        }

        String encryptedPassword;
        if(!userDTO.getPassword().equals(existingUser.getPassword())){
            encryptedPassword = passwordEncoder.encode(userDTO.getPassword());
        }else{
            encryptedPassword = userDTO.getPassword();
        }

        existingUser.setUsername(newUsername);
        existingUser.setPassword(encryptedPassword);
        existingUser.setNickname(userDTO.getNickname());
        existingUser.setEmail(userDTO.getEmail());

        UserEntity updatedUserInfo = userRepository.save(existingUser);
        return UserDTO.convertToDto(updatedUserInfo);
    }
    
    @Override
    public ResponseDTO<Object> isExistByEmail(String email){
        try {
            ResponseDTO<Object> res = new ResponseDTO<>();
            if(userRepository.existsByEmail(email)){
                res.setMessage("존재하는이메일");
            }else res.setMessage("존재하지않는이메일");
            return res;
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }
}
