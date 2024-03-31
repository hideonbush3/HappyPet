package hideonbush3.springboot.happypet.persistence;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import hideonbush3.springboot.happypet.model.AuthMailEntity;

public interface AuthMailRepository extends JpaRepository<AuthMailEntity, Long>{
    public Optional<AuthMailEntity> findByEmailAndAuthCode(String email, String authCode);
    public void deleteByEmailAndCreatedDate(String email, LocalDateTime createdDate);
}