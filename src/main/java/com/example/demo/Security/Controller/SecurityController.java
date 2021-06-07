package com.example.demo.Security.Controller;

import com.example.demo.Domain.Dto.PeopleRequest;
import com.example.demo.Domain.Dto.PeopleResponse;
import com.example.demo.Security.Service.OauthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/auth", consumes = MediaType.APPLICATION_JSON_VALUE)
public class SecurityController {
    private final OauthService oauthService;

    @GetMapping
    public String home() {
        return "<h2>OAuth 인증에 성공하였습니다~!</h2>";
    }
}
