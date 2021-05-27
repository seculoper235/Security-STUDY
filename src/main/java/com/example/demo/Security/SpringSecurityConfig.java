package com.example.demo.Security;

import com.example.demo.Security.Handler.MyFailHandler;
import com.example.demo.Security.Handler.MyLogoutHandler;
import com.example.demo.Security.Handler.MyLogoutSuccessHandler;
import com.example.demo.Security.Handler.MySucessHandler;
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
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
/* WebSecurityConfigurerAdapter란?
 * SecurityConfigurer의 구현체로, 구현체에는 여러 종류가 있는데 Web 상의 보안을 설정하는데 특화되어 있는 추상 클래스이다.
 * configure 메소드로 auth를 설정하거나, url 별로 보안을 설정하거나, 보안 필터를 등록하는 등의 보안 관련 모든 설정을 담당한다.
 * 또한 별도의 커스텀 SecurityConfigurer를 생성하고, 이 클래스에 Bean 등록하여 사용할 수 있다. */
public class SpringSecurityConfig extends WebSecurityConfigurerAdapter {

    /* 내부에서 인증을 어떤 방식(인메모리, JDBC 등)으로 어떻게 진행할지 설정한다. */
    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.inMemoryAuthentication()
                .withUser("user")
                .password("pass")
                .roles("USER")
                .and()
                .withUser("admin")
                .password("admin")
                .roles("ADMIN");
    }

    /* 인증 요청을 받았을 때, 해당 요청을 어떻게 처리할지 흐름을 작성한다 */
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        // 로그아웃 관련
        http.logout()
                .logoutUrl("/logout")
                .logoutSuccessUrl("/login")
                // 로그아웃 요청을 어떻게 처리할지 작성(여기선 세션을 삭제해버림)
                .addLogoutHandler(myLogoutHandler())
                // 로그아웃 성공후, 진행작 작업을 작성
                .logoutSuccessHandler(myLogoutSuccessHandler());

        // 로그인 관련
        http.formLogin()
                .failureForwardUrl("/login")
                //.loginPage("/login")
                // 로그인 처리 URL(로그인 페이지를 따로 제작할 경우, form action을 이 URL로 설정하면 된다
               //.loginProcessingUrl("/login_proc")
                // 인증이 성공한 다음 어떻게 처리할지를 작성한다
                .successHandler(mySucessHandler())
                // 인증이 실패했을 때, 어떻게 처리할지를 작성한다
                .failureHandler(myFailHandler())
                .permitAll();

        // 예외 처리
        http.exceptionHandling()
                .accessDeniedPage("/denied");

        // 사용자 인가 정보
        // hasRole : 해당하는 권한을 가지면 인가 성공
        // hasAnyRole : 해당하는 권한들 중 "하나만 있어도" 인가 성공
        http.authorizeRequests()
                .antMatchers("/admin").hasRole("ADMIN")
                .antMatchers("/users").hasAnyRole("USER", "ADMIN")
                .anyRequest().permitAll()
                ;
    }

    /* 스프링 시큐리티보다 앞 단의 설정을 담당한다. 즉 애플리케이션 보안이 아닌, HTTP 방화벽 등을 설정한다.
     * 정적 리소스는 보안에 구애되지 않고 누구나 볼 수 있어야하므로, 보통 정적 리소스를 설정할 때 사용한다.
     * 정적 리소스는 requestMatchers()로 설정할 수 있다.
     */
    @Override
    public void configure(WebSecurity web) throws Exception {
        web.ignoring()
                .requestMatchers(PathRequest.toStaticResources().atCommonLocations());
    }

    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    MySucessHandler mySucessHandler() {
        return new MySucessHandler();
    }
    @Bean
    MyFailHandler myFailHandler() {
        return new MyFailHandler();
    }

    @Bean
    MyLogoutHandler myLogoutHandler() {
        return new MyLogoutHandler();
    }
    @Bean
    MyLogoutSuccessHandler myLogoutSuccessHandler() {
        return new MyLogoutSuccessHandler();
    }
}
