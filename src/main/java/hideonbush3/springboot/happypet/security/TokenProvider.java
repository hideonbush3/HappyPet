package hideonbush3.springboot.happypet.security;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import hideonbush3.springboot.happypet.model.UserEntity;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

@Service
public class TokenProvider {

    @Value("${jwt.secret.key}")
    private String SECRET_KEY;

    public String create(UserEntity user){
        // 기한 - 지금으로부터 1일
        Date expiryDate = Date.from(Instant.now().plus(1, ChronoUnit.DAYS));
        return Jwts.builder()
            .signWith(SignatureAlgorithm.HS512, SECRET_KEY)
            .setSubject(user.getId())
            .setIssuer("happy pet")
            .setIssuedAt(new Date())
            .setExpiration(expiryDate)
            .compact();
    }

    public String validateAndGetUserId(String token){
        Claims claims = Jwts.parser()
        .setSigningKey(SECRET_KEY)  // 헤더와 페이로드를 시크릿키로 서명
        .parseClaimsJws(token)      // token을 디코딩 및 파싱
        .getBody();

        return claims.getSubject();
    }
    
}
