package com.example.demo.Security;

import com.example.demo.Domain.Dto.PeopleRequest;
import com.example.demo.Domain.Dto.PeopleResponse;
import com.example.demo.Service.SecurityService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class SecurityController {
    private final SecurityService securityService;

    @GetMapping("/")
    public String home() {
        return ("<h2>Welcome!</h2><a href=\"/login\"> 로그인하기 </a>");
    }

    @PostMapping("/loginProc")
    public ResponseEntity<PeopleResponse> loginProc(@RequestBody PeopleRequest peopleRequest) {
        return ResponseEntity.ok(securityService.signUp(peopleRequest));
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
