package com.example.demo.Security.Config;

import com.example.demo.Security.Handler.MySuccessHandler;
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
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

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
public class SpringSecurityConfig extends WebSecurityConfigurerAdapter {
    private final MySuccessHandler successHandler;
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
                .and()
                .sessionFixation()
                    .changeSessionId()
                .sessionCreationPolicy(SessionCreationPolicy.ALWAYS)
        ;

        // 로그아웃 관련
        http.logout()
                .logoutSuccessUrl("/logoutSuccess")
        ;

        /* 로그인 관련
         * OAuth 인증은 form과 달리 엔드포인트와 연결되는데, 이 엔드포인트에는 Token, Redirection, Authorization, UserInfo 4가지가 있다. */
        http.oauth2Login()
                .loginPage("/login")
                .successHandler(successHandler)
                //.defaultSuccessUrl("/loginSuccess")
                /* authorizationEndpoint? */
                // 인증 서버에서 Social 로그인 페이지를 요청하는 EndPoint이다.
                // baseUri()를 설정하여 Social 로그인 페이지를 요청하는 URI를 설정할 수 있다.
                // (기본적으로 /oauth2/authorization/{provider}로 정해져 있으며, 구글은 건드릴 필요 없다.)
                .authorizationEndpoint()
                    .baseUri("/oauth2/auth")
                .and()

                /* RedirectionEndpoint? */
                // 인증 서버에서 Social 로그인 성공 후, 본격적인 OAuth 인증을 어디서 처리할지 설정하는 EndPoint이다.
                // baseUri()를 설정하여 redirect 페이지를 설정할 수 있다.
                // 설정이 조금 까다로운데, properties 파일과 Social OAuth와 해당 Endpoint의 baseUri을 모두 설정해줘야 한다.
                // (기본적으로 /login/oauth2/code/{provider}로 정해져 있으며, 구글은 건드릴 필요 없다.)
                /*.redirectionEndpoint()
                    .baseUri("/login/oauth2/redirect")
                .and()*/

                /* tokenEndpoint? */
                // 인증 서버에서 토큰을 처리하기 위한 Endpoint이다.
                // RedirectionEndpint로부터 받은 authorization_code를 가지고 어플리케이션을 사용할 수 있는 access token을 발급한다.
                /*.tokenEndpoint()
                    .accessTokenResponseClient(???)
                .and()*/

                /* userInfoEndpoint? */
                // 현재 서버에서 사용자의 정보를 어떻게 다루기 위한 Endpoint이다.
                // OAuth 인증이 끝난 사용자 정보의 DB 업데이트가 진행되는 역할을 맡는다.
                // 여기서 바로 UserService를 사용하여 사용자를 등록/업데이트 할 수 있으며, 전달받은 OAuth 전용 객체를 Entity로 바꿔 저장하는 loadUser() 메소드가 사용된다.
                .userInfoEndpoint()
                    .userService(oauthService)
        ;

        // 에러 핸들링
        http.exceptionHandling()
                /* AuthenticationEntryPoint? */
                //
                //.authenticationEntryPoint()
                //.defaultAuthenticationEntryPointFor()
                //.accessDeniedHandler()
                //.defaultAccessDeniedHandlerFor()
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
}
