package hideonbush3.springboot.happypet.model;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import org.hibernate.annotations.GenericGenerator;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "user", uniqueConstraints = {@UniqueConstraint(columnNames = "username")}) // "username" 열에 중복된 값을 허용하지 않음
public class UserEntity {
    @Id
    @GeneratedValue(generator="system-uuid")
    @GenericGenerator(name="system-uuid", strategy = "uuid")
    private String id;
    
    @Column(nullable = false)
    private String username;
    
    private String password;
    
    private String nickname;
    
    private String email;

    private String role;        // 일반유저 or 관리자

    private String authProvider;    // OAuth에서 사용할 유저 정보 제공자

    @OneToMany(mappedBy = "userEntity", cascade = CascadeType.REMOVE)
    private List<PostEntity> postList;

    @OneToMany(mappedBy = "userEntity", cascade = CascadeType.REMOVE)
    private List<FavoriteEntity> favoriteList;
}
