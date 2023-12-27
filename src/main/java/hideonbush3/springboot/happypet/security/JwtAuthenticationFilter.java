package hideonbush3.springboot.happypet.security;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter{
    @Autowired
    private TokenProvider tokenProvider;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        try {
            // 요청에서 토큰 가져오기
            String token = parseBearerToken(request);
            log.info("필터 실행중..");
            // 토큰이 있는지 검사
            if(token != null && !token.equalsIgnoreCase("null")){

                // 토큰에서 userId 추출, 위조된 경우 validateAndGetUserId()에서 예외처리
                String userId = tokenProvider.validateAndGetUserId(token);
                log.info("인증된 유저의 아이디 : " + userId);

                // AbstractAuth... Spring Security의 인증 토큰을 나타내는 추상 클래스 - 사용자 인증 정보를 저장
                // UsernamePass... 실제로 사용자 인증 정보를 저장하는 구체적인 인증 토큰 클래스
                AbstractAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                    userId,                         // 인증된 사용자의 정보, 모든 타입 가능
                    null,               // 패스워드
                    AuthorityUtils.NO_AUTHORITIES   // 아무 권한도 없음
                );

                // 사용자의 요청(request)에 관련 정보를 세부 저장
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                // SecurityContext 객체를 생성
                // 현재 사용자의 인증 정보 및 권한 정보를 관리하는 컨테이너
                SecurityContext securityContext = SecurityContextHolder.createEmptyContext();

                // 앞에서 설정한 authentication 객체를 securityContext에 할당
                // 현재 사용자의 인증 정보가 SecurityContext에 저장됨
                securityContext.setAuthentication(authentication);
                
                // SecurityContext를 현재 실행 중인 스레드에 연결
                // 이렇게 하면 현재 스레드에서 사용자 인증 및 보안 정보에 쉽게 액세스할 수 있게 됨
                SecurityContextHolder.setContext(securityContext);
            }
        } catch (Exception e) {
            logger.error("SecurityContext에서 사용자 인증을 설정할 수 없음", e);
        }

        filterChain.doFilter(request, response);
    }

    private String parseBearerToken(HttpServletRequest request){
        // Http 요청의 헤더를 파싱해 Bearer 토큰을 리턴
        String bearerToken = request.getHeader("Authorization");

        if(StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")){
            return bearerToken.substring(7);
        }
        return null;
    }

}
