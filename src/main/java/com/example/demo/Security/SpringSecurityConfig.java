package com.example.demo.Security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

@Configuration
@EnableWebSecurity
public class SpringSecurityConfig extends WebSecurityConfigurerAdapter {

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

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        // 로그아웃 관련
        http.logout()
                .logoutUrl("/logout")
                .logoutSuccessUrl("/login")
                // 로그아웃 요청을 어떻게 처리할지 작성(여기선 세션을 삭제해버림)
                .addLogoutHandler(new LogoutHandler() {
                    @Override
                    public void logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
                        HttpSession session = request.getSession();
                        session.invalidate();
                    }
                })
                // 로그아웃 성공후, 진행작 작업을 작성
                .logoutSuccessHandler(new LogoutSuccessHandler() {
                    @Override
                    public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
                        response.sendRedirect("/login");
                    }
                });

        // 로그인 관련
        http.formLogin()
                .failureUrl("/login")
                //.loginPage("/login")
                // 로그인 처리 URL(로그인 페이지를 따로 제작할 경우, form action을 이 URL로 설정하면 된다
               //.loginProcessingUrl("/login_proc")
                // 인증이 성공한 다음 어떻게 처리할지를 작성한다
                //(권한별로 다른 페이지를 제공)
                .successHandler(new AuthenticationSuccessHandler() {
                    @Override
                    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
                        System.out.println("Authentication Success! Hello " + authentication.getName());
                        System.out.println("사용자 권한 확인 : " + request.isUserInRole("ROLE_ADMIN"));
                        if(authentication.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))) {
                            response.sendRedirect("/admin");
                        }
                        else if(authentication.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_USER"))) {
                            response.sendRedirect("/users");
                        }
                        else {
                            response.sendRedirect("/");
                        }
                    }
                })
                // 인증이 실패했을 때, 어떻게 처리할지를 작성한다
                .failureHandler(new AuthenticationFailureHandler() {
                    @Override
                    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {
                        System.out.println("Authentication ERROR!");
                        response.sendRedirect("/login");
                    }
                })
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
                .antMatchers("/", "static/css", "static/js").permitAll()
                ;
    }

    @Override
    public void configure(WebSecurity web) throws Exception {
        super.configure(web);
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return NoOpPasswordEncoder.getInstance();
    }
}
