package com.example.demo.Security.Controller;

import com.example.demo.Security.Service.OauthService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/")
public class SecurityController {
    private final OauthService oauthService;

    @GetMapping("/google")
    public String googleHome() {
        return "<h2>Google 인증에 성공하였습니다~!</h2>";
    }
}
