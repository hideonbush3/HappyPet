package hideonbush3.springboot.happypet.persistence;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import hideonbush3.springboot.happypet.model.EmailAuthEntity;

public interface EmailAuthRepository extends JpaRepository<EmailAuthEntity, Long>{
    public Optional<EmailAuthEntity> findByEmailAndAuthCode(String email, String authCode);
    public void deleteByEmailAndCreatedDate(String email, LocalDateTime createdDate);
}