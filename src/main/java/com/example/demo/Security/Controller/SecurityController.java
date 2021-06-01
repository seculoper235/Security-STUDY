package com.example.demo.Security.Controller;

import com.example.demo.Domain.Dto.PeopleRequest;
import com.example.demo.Domain.Dto.PeopleResponse;
import com.example.demo.Security.Service.SecurityService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/auth", consumes = MediaType.APPLICATION_JSON_VALUE)
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
    public String error1() {
        return ("<h2>부적절한 접근입니다!!</h2>");
    }

    @GetMapping("/except")
    public String error2() {
        return ("<h2>예외 처리입니다!!</h2>");
    }

    @GetMapping("/failed")
    public String error3() {
        return ("<h2>로그인 실패입니다!!</h2>");
    }
}
