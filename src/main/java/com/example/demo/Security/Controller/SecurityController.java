package com.example.demo.Security.Controller;

import com.example.demo.Security.Dto.GooglePeople;
import com.example.demo.Security.Service.OauthService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/")
public class SecurityController {
    private final OauthService oauthService;
    private final HttpSession httpSession;

    @GetMapping
    public String oAuthHome() {
        return "<h2>OAuth 인증을 해주세요~!</h2>" +
                "<button><a href=\"/login\">로그인</a></button>";
    }

    @GetMapping("/loginSuccess")
    public String oAuthSuccess() {
        GooglePeople people = (GooglePeople) httpSession.getAttribute("people");
        return "<h2>Google OAuth 인증에 성공하였습니다~!</h2>" +
                "<h2>" +people.getName()+ " 님, 환영합니다!!</h2>" +
                "<button><a href=\"/users\">유저 페이지로</a></button>" +
                "<button><a href=\"/admin\">관리자 페이지로</a></button>";
    }

    @GetMapping("/logoutSuccess")
    public String notAuth() {
        return "<h2>Google 로그아웃 되었습니다~!</h2>" +
                "<button><a href=\"/\">홈페이지로!</a></button>";
    }

    @GetMapping("/login")
    public String loginPage() {
        return "<h2>Google 로그인!</h2>" +
                "<button><a href=\"/oauth2/auth/google\">Google 로그인</a></button>";
    }

    @GetMapping("/users")
    public String welcomeUser() {
        return "<h2>User 전용 페이지입니다~!</h2>" +
                "<button><a href=\"/logout\">로그아웃</a></button>" +
                "<button><a href=\"/loginSuccess\">홈페이지로!</a></button>";
    }

    @GetMapping("/admin")
    public String welcomeAdmin() {
        return "<h2>관리자 전용 페이지입니다~!</h2>" +
                "<button><a href=\"/logout\">로그아웃</a></button>" +
                "<button><a href=\"/loginSuccess\">홈페이지로!</a></button>";
    }

    @GetMapping("/denied")
    public String failOAuth() {
        return "<h2>접근 권한이 없습니다!ㅠㅜ</h2>" +
                "<button><a href=\"/loginSuccess\">홈페이지로!</a></button>";
    }
}
