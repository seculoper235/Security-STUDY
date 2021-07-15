package com.example.demo.Auth.Token;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.GenericFilterBean;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;


/* JwtFilter란?
 * 다른 Filter들과 마찬가지로 Jwt 인증을 담당하는 필터이다
 * 인증을 위해 JWT를 다루는 JwtProvider를 사용하며, 이 외에는 여느 Filter와 동일하다. */
@RequiredArgsConstructor
public class JwtFilter extends GenericFilterBean {
    private final JwtProvider jwtProvider;

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        // Request HTTP에서 JWT를 가져옴
        String jwtToken = jwtProvider.resolveToken((HttpServletRequest) request);

        // JWT의 유효성 검증
        if(jwtToken != null && jwtProvider.validateToken(jwtToken)) {
            // 유효하다면 Authentication을 가져옴
            Authentication authentication = jwtProvider.getAuthentication(jwtToken);
            // SecurityContext에 저장
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }

        chain.doFilter(request, response);
    }
}
