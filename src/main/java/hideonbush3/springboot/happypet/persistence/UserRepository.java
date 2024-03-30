package hideonbush3.springboot.happypet.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import hideonbush3.springboot.happypet.model.UserEntity;

public interface UserRepository extends JpaRepository<UserEntity, String>{
    Boolean existsByUsername(String username);
    Boolean existsByNickname(String nickname);
    UserEntity findByUsername(String username);
    UserEntity findByUsernameAndPassword(String username, String password);
    Boolean existsByEmail(String email);
    UserEntity findByEmail(String email);
}
