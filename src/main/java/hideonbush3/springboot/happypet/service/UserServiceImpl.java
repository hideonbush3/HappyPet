package hideonbush3.springboot.happypet.service;

import java.util.Optional;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import hideonbush3.springboot.happypet.dto.ResponseDTO;
import hideonbush3.springboot.happypet.dto.UserDTO;
import hideonbush3.springboot.happypet.model.ImageEntity;
import hideonbush3.springboot.happypet.model.PostEntity;
import hideonbush3.springboot.happypet.model.UserEntity;
import hideonbush3.springboot.happypet.persistence.CommentRepository;
import hideonbush3.springboot.happypet.persistence.ReplyRepository;
import hideonbush3.springboot.happypet.persistence.UserRepository;
import hideonbush3.springboot.happypet.security.TokenProvider;

@Service("ussrv")
public class UserServiceImpl implements UserService{
    @Value("${image.dir}")
    private String imgDir;

    @Autowired
    private UserRepository userRepository;

    @Autowired 
    private CommentRepository commentRepository;

    @Autowired
    private ReplyRepository replyRepository;

    @Autowired
    private TokenProvider tokenProvider;

    PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Override
    public ResponseDTO<Object> create(UserDTO dto) {
        ResponseDTO<Object> res = new ResponseDTO<>();
        try {
            String username = dto.getUsername();
            if(userRepository.existsByUsername(username)){
                res.setMessage("아이디 중복");
            }else if(userRepository.existsByNickname(dto.getNickname())){
                res.setMessage("닉네임 중복");
            }else{
                UserEntity user = UserEntity.builder()
                .username(dto.getUsername())
                .password(passwordEncoder.encode(dto.getPassword()))
                .nickname(dto.getNickname())
                .email(dto.getEmail())
                .build();
        
                UserEntity registeredUser = userRepository.save(user);
        
                UserDTO userDTO = new UserDTO();
                userDTO.setId(registeredUser.getId());
                userDTO.setUsername(dto.getUsername());

                res.setObject(userDTO);
            }
            return res;
        } catch (Exception e) {
            res.setError(e.getMessage());
            return res;
        }
    }

    @Override
    public ResponseDTO<Object> getByCredentials(final String username, final String password, final PasswordEncoder encoder) {
        ResponseDTO<Object> res = new ResponseDTO<>();
        try{
            final UserEntity userEntity = userRepository.findByUsername(username);
    
            if(userEntity == null){
                res.setMessage("아이디불일치");
                return res;
            }
    
            if(userEntity != null && encoder.matches(password, userEntity.getPassword())){
                String token = tokenProvider.create(userEntity);
                UserDTO user = new UserDTO();
                user.setUsername(username);
                user.setNickname(userEntity.getNickname());
                user.setToken(token);
                res.setObject(user);
                return res;
            }else{
                res.setMessage("비밀번호불일치");
                return res;
            }
        }catch(Exception e){
            res.setError(e.getMessage());
            return res;
        }
    }

    @Override
    public ResponseDTO<UserDTO> select(String userId) {
        ResponseDTO<UserDTO> res = new ResponseDTO<>();
        try {
            UserEntity sessionUser = userRepository.findById(userId).get();
            res.setObject(UserDTO.convertToDto(sessionUser));
            return res;
        } catch (Exception e) {
            res.setError(e.getMessage());
            return res;
        }
    }

    @Transactional
    @Override
    public ResponseDTO<?> delete(String userId) {
        ResponseDTO<?> res = new ResponseDTO<>();
        try {                 
            UserEntity userEntity = userRepository.findById(userId).get();
            
            List<PostEntity> postList = userEntity.getPostList();
            
            if(!postList.isEmpty()){
                List<List<ImageEntity>> imgList = new ArrayList<>();
                for(int i = 0; i < postList.size(); i++){
                    if(!postList.get(i).getImageList().isEmpty()){
                        imgList.add(postList.get(i).getImageList());
                    }
                }
                if(!imgList.isEmpty()){
                    for(int i = 0; i< imgList.size(); i++){
                        int size = imgList.get(i).size();
                        for(int j = 0; j < size; j++){
                            ImageEntity img = imgList.get(i).get(j);
                            File imgToDelete = new File(imgDir + img.getName());
                            imgToDelete.delete();
                        }
                    }
                }
            }

            replyRepository.deleteAllByUserEntity(userEntity);
            commentRepository.deleteAllByUserEntity(userEntity);
            userRepository.delete(userEntity);  
            res.setMessage("탈퇴완료");
            return res;
        } catch (Exception e) {
            res.setError(e.getMessage());
            return res;
        }
    }

    @Override
    public ResponseDTO<Object> isExist(UserDTO dto, PasswordEncoder passwordEncoder, String userId, String process) {
        ResponseDTO<Object> res = new ResponseDTO<>();
          try {
            Optional<UserEntity> optionalUser = userRepository.findById(userId);
            UserEntity user = optionalUser.orElse(null);
            if(passwordEncoder.matches(dto.getPassword(), user.getPassword())){
                res.setMessage(process);
                return res;
            }else{
                res.setMessage("비밀번호불일치");
                return res;
            }
        } catch (Exception e) {
            res.setError(e.getMessage());
            return res;
        }
    }

    // 내 정보 수정
    @Override
    public ResponseDTO<UserDTO> update(UserDTO userDTO, String userId) {
        ResponseDTO<UserDTO> res = new ResponseDTO<>();
        try {
            String newLoginId = userDTO.getUsername();
            UserEntity existingUser = userRepository.findById(userId).get();
            String encryptedPassword;
    
            if(!existingUser.getUsername().equals(newLoginId) && userRepository.existsByUsername(newLoginId)){
                res.setMessage("아이디중복");
            }
            else if(!existingUser.getNickname().equals(userDTO.getNickname()) && userRepository.existsByNickname(userDTO.getNickname())){
                res.setMessage("닉네임중복");
            }
            else{
                encryptedPassword = passwordEncoder.encode(userDTO.getPassword());
                existingUser.setUsername(newLoginId);
                existingUser.setPassword(encryptedPassword);
                existingUser.setNickname(userDTO.getNickname());
                existingUser.setEmail(userDTO.getEmail());
        
                UserEntity updatedUser = userRepository.save(existingUser);
                res.setObject(UserDTO.convertToDto(updatedUser));
            }
            return res;
        } catch (Exception e) {
            res.setError(e.getMessage());
            return res;
        }
    }
    
    @Override
    public ResponseDTO<Object> isExistByEmail(String email){
        ResponseDTO<Object> res = new ResponseDTO<>();
        try {
            if(userRepository.existsByEmail(email)){
                res.setMessage("존재하는이메일");
            }else res.setMessage("존재하지않는이메일");
            return res;
        } catch (Exception e) {
            res.setError(e.getMessage());
            return res;
        }
    }

    @Override
    public ResponseDTO<Object> isExistByUserId(String userId) {
        ResponseDTO<Object> res = new ResponseDTO<>();
        try {
            Boolean isSignup = userRepository.existsByUsername(userId);
            if(isSignup) res.setMessage("존재한다");
            else res.setMessage("존재하지않음");
            
            return res;
        } catch (Exception e) {
            res.setError(e.getMessage());
            return res;
        }
    }
}
