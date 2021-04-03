package com.example.demo.Security;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SecurityController {
    @GetMapping("/")
    public String home() {
        return ("<h2>Welcome!</h2><a href=\"/login\"> 로그인하기 </a>");
    }

    @GetMapping("/users")
    public String user() {
        return ("<h2>Welcome User!</h2><a href=\"/logout\"> 로그아웃하기 </a>");
    }

    @GetMapping("/admin")
    public String admin() {
        return ("<h2>Welcome Admin!</h2><a href=\"/logout\"> 로그아웃하기 </a>");
    }

    @GetMapping("/denied")
    public String error() {
        return ("<h2>부적절한 접근입니다!!</h2>");
    }
}
