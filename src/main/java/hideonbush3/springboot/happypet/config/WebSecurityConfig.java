package hideonbush3.springboot.happypet.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.web.filter.CorsFilter;

import hideonbush3.springboot.happypet.security.JwtAuthenticationFilter;

// 스프링 시큐리티에서 내가 생성한 서블릿 필터(JwtAuthenti...)를 사용하라고 알리는 설정 작업
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter{

    @Autowired
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    // HttpSecurity - 시큐리티 설정을 위한 오브젝트 
    @Override
    protected void configure(HttpSecurity http) throws Exception{
        // http 시큐리티 빌더
        http.cors()     // WebMvcConfig에서 이미 설정했으므로 기본 cors 설정.
            .and()
            .csrf()     // csrf는 현재 사용하지 않으므로 disable
                .disable()
            .httpBasic()    // token을 사용하므로 basic 인증 disable
                .disable()
            .sessionManagement()    // session 기반이 아님을 선언
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            .and()
            .authorizeRequests()    // 아래 경로는 인증 안해도 됨
                .antMatchers("/",
                "/user/signup", "/user/signin", "/user/checksignup/email", "/user/checksignup/id", "/user/find-id",
                "/post", "/post/view", 
                "/favorite/is-added", 
                "/facilityAPI/**", 
                "/auth-code/**").permitAll()
            .anyRequest()   // 위 경로 이외의 모든 경로는 인증해야됨.
                .authenticated();

    // filter 등록.
    // 매 요청마다
    // CorsFilter 실행한 후에
    // jwtAuthenticationFilter 실행한다.
    http.addFilterAfter(
        jwtAuthenticationFilter,
        CorsFilter.class
    );
 }
}
