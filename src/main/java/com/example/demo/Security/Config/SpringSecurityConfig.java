package com.example.demo.Security.Config;

import com.example.demo.Auth.Token.JwtFilter;
import com.example.demo.Auth.Token.JwtProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.session.Session;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
/* WebSecurityConfigurerAdapter란?
 * SecurityConfigurer의 구현체로, 구현체에는 여러 종류가 있는데 Web 상의 보안을 설정하는데 특화되어 있는 추상 클래스이다.
 * configure 메소드로 auth를 설정하거나, url 별로 보안을 설정하거나, 보안 필터를 등록하는 등의 보안 관련 모든 설정을 담당한다.
 * 또한 별도의 커스텀 SecurityConfigurer를 생성하고, 이 클래스에 Bean 등록하여 사용할 수 있다. */
public class SpringSecurityConfig<s extends Session> extends WebSecurityConfigurerAdapter {
    private final JwtProvider jwtProvider;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf().disable();
        http.httpBasic().disable();

        /* 세션을 관리하기 위한 빌더 패턴이다.
         * 최대 세션은 1개이고, 동시에 세션을 접속할 순 없다.
         * SessionFixation은 세션 고정 공격의 대응 방안을 의미하며, 이 공격은 attacker의 세션 ID로 victim이 로그인 함으로써 세션을 공유하게 되는 공격이다.
         * 스프링 시큐리티에서는 접속 시마다 세션을 새로 발급하는 방법을 제공하며, 이전 세션이 사용 불가능한 newSession()과 가능한 changeSessionId()가 있다. */
        // 세션을 사용하지 않는 JWT는 HTTP의 무상태성을 가지고 있으므로, 세션 정책도 그에 맞춰준다
        http.sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
        ;

        // 로그아웃 관련
        http.logout()
                .logoutSuccessUrl("/logoutSuccess")
        ;

        // 에러 핸들링
        http.exceptionHandling()
                .accessDeniedPage("/denied")
        ;

        // JWT 토큰 적용
        /* username~ 필터보다 jwt 필터가 앞인 이유?
         * UsernamePassword 인증 필터는 UserDetailsService와 UserDetails/User를 사용하여 인증을 한다
         * 하지만 JWT 토큰을 사용하기 위해선 UserDetails를 사용해야 한다
         * 따라서 Username~ 인증 필터에서 먼저 걸러지지 않기 위해선, JWT 필터는 username 필터 앞쪽에 위치하여 먼저 인증해야 한다
         */
        http.addFilterBefore(new JwtFilter(jwtProvider), UsernamePasswordAuthenticationFilter.class);

        // 권한 정보
        http.authorizeRequests()
                .antMatchers("/admin").hasRole("ADMIN")
                .antMatchers("/users").hasAnyRole("USER", "ADMIN")
                .antMatchers("/").permitAll()
                .anyRequest().permitAll()
        ;
    }

    /* 스프링 시큐리티보다 앞 단의 설정을 담당한다. 즉 애플리케이션 보안이 아닌, HTTP 방화벽 등을 설정한다.
     * 정적 리소스는 보안에 구애되지 않고 누구나 볼 수 있어야하므로, 보통 정적 리소스를 설정할 때 사용한다.
     * 정적 리소스는 requestMatchers()로 설정할 수 있다.
     */
    @Override
    public void configure(WebSecurity web){
        web.ignoring()
                .requestMatchers(PathRequest.toStaticResources().atCommonLocations());
    }

    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
