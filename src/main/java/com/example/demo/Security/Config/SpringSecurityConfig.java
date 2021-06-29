package com.example.demo.Security.Config;

import com.example.demo.Security.Service.OauthService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.session.Session;
import org.springframework.session.data.redis.RedisIndexedSessionRepository;
import org.springframework.session.security.SpringSessionBackedSessionRegistry;

import javax.sql.DataSource;

import static com.example.demo.Security.Config.QueryState.SELECT_AUTH;
import static com.example.demo.Security.Config.QueryState.SELECT_USER;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
/* WebSecurityConfigurerAdapter란?
 * SecurityConfigurer의 구현체로, 구현체에는 여러 종류가 있는데 Web 상의 보안을 설정하는데 특화되어 있는 추상 클래스이다.
 * configure 메소드로 auth를 설정하거나, url 별로 보안을 설정하거나, 보안 필터를 등록하는 등의 보안 관련 모든 설정을 담당한다.
 * 또한 별도의 커스텀 SecurityConfigurer를 생성하고, 이 클래스에 Bean 등록하여 사용할 수 있다. */
public class SpringSecurityConfig<s extends Session> extends WebSecurityConfigurerAdapter {
    private final RedisIndexedSessionRepository sessionRepository;
    private final OauthService oauthService;
    private final DataSource dataSource;

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        // DB를 사용할 수 있도록 먼저 datasource를 입력한다. 해당 데이터 소스는 application.properties에서 작성할 수 있다.
        // usersBy로 People Entity를 검색하고, authoirutiesBy로 권한 Entity를 검색한다.
        auth.jdbcAuthentication()
                .dataSource(dataSource)
                .rolePrefix("ROLE_")
                .usersByUsernameQuery(SELECT_USER)
                .authoritiesByUsernameQuery(SELECT_AUTH)
                .passwordEncoder(bCryptPasswordEncoder());
    }

    /* 인증 요청을 받았을 때, 해당 요청을 어떻게 처리할지 흐름을 작성한다 */
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf().disable();

        /* 세션을 관리하기 위한 빌더 패턴이다.
         * 최대 세션은 1개이고, 동시에 세션을 접속할 순 없다.
         * SessionFixation은 세션 고정 공격의 대응 방안을 의미하며, 이 공격은 attacker의 세션 ID로 victim이 로그인 함으로써 세션을 공유하게 되는 공격이다.
         * 스프링 시큐리티에서는 접속 시마다 세션을 새로 발급하는 방법을 제공하며, 이전 세션이 사용 불가능한 newSession()과 가능한 changeSessionId()가 있다. */
        http.sessionManagement()
                .maximumSessions(1)
                .maxSessionsPreventsLogin(true)
                .expiredUrl("/expired")
                .sessionRegistry(sessionRegistry())
        ;

        // 로그아웃 관련
        http.logout()
                .logoutSuccessUrl("/logoutSuccess")
        ;

        /* 로그인 관련
         * OAuth 인증은 form과 달리 엔드포인트와 연결되는데, 이 엔드포인트에는 Token, Redirection, Authorization, UserInfo 4가지가 있다. */
        http.oauth2Login()
                .defaultSuccessUrl("/loginSuccess")
                .userInfoEndpoint()
                    .userService(oauthService)
        ;

        // 에러 핸들링
        http.exceptionHandling()
                .accessDeniedPage("/denied")
        ;

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


    /* 로그아웃 해도 세션이 삭제되지 않는 이유? */
    // Redis에 맞지 않는 SessionRegistry를 사용하지 않아서 생긴 일.
    // ~~Impl은 레포지토리를 사용하지 않으므로, 레포지토리를 사용하는 SpringSessionBackedSessionRegistry를 사용해야 한다.
    // (레포지토리는 세션 이벤트를 지원하는 RedisIndexedSessionRepository를 사용하는 것이 좋다)
    @Bean
    public SpringSessionBackedSessionRegistry<?> sessionRegistry() {
        return new SpringSessionBackedSessionRegistry<>(this.sessionRepository);
    }

    /*@Bean
    public SessionRegistry sessionRegistry() {
        return new SessionRegistryImpl();
    }

    @Bean
    public static ServletListenerRegistrationBean<?> httpSessionEventPublisher() {
        return new ServletListenerRegistrationBean<>(new HttpSessionEventPublisher());
    }*/
}
