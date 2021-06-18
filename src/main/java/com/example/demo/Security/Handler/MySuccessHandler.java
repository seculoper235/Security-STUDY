package com.example.demo.Security.Handler;

import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class MySuccessHandler extends SavedRequestAwareAuthenticationSuccessHandler {
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws ServletException, IOException {
        if(request.isUserInRole("ROLE_ADMIN")) {
            response.sendRedirect("/admin");
        }
        else {
            response.sendRedirect("/loginSuccess");
        }

        super.onAuthenticationSuccess(request, response, authentication);
    }
}
