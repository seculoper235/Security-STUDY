package com.example.demo.Security.Handler;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.stream.Collectors;

public class MySucessHandler implements AuthenticationSuccessHandler {
    /* success() 메소드 문제
     * configure()의 successHandler()에서 사용하는 메소드는 AuthenticationSuccessHandler의 메소드이다.
     * 따라서 이를 사용하려면 해당 인터페이스를 구현한 클래스를 사용해야한다.
     * 만약 다른 클래스를 구현하고 싶다면, AuthenticationSuccessHandler의 인터페이스에 해당하는 Success() 메소드를 따로 작성해주면 된다. */
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        SecurityContextHolder.getContext().setAuthentication(authentication);

        System.out.println("Authentication Success! Hello " + authentication.getName());
        System.out.println("사용자 권한 확인 : " + authentication.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(Collectors.toList()));
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
}
