package hideonbush3.springboot.happypet.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import hideonbush3.springboot.happypet.model.ImageEntity;

public interface ImageRepository extends JpaRepository<ImageEntity, Long>{
    
}
