package com.example.demo.Security.Service;

import com.example.demo.Domain.Dto.PeopleDto;
import com.example.demo.Domain.Dto.PeopleRequest;
import com.example.demo.Domain.Dto.PeopleResponse;
import com.example.demo.Domain.People;
import com.example.demo.Exception.NoSuchException;
import com.example.demo.Repository.PeopleRepository;
import com.example.demo.Security.Config.MyUPAuthenticationProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class SecurityService {
    private final PeopleRepository peopleRepository;
    private final MyUPAuthenticationProvider authenticationProvider;


    public PeopleResponse signUp(PeopleRequest peopleRequest) {
        PeopleDto peopleDto = PeopleDto.builder()
                .username(peopleRequest.getUsername())
                .password(peopleRequest.getPassword())
                .build();
        Optional<People> people = peopleRepository.findById(peopleDto.getUsername());
        if(people.isEmpty()) {
            throw new NoSuchException();
        }

        return formAuthentication(peopleDto);
    }
    
    public PeopleResponse formAuthentication(PeopleDto dto) {
        // 인증을 위한 미인증 토큰 생성
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(dto.getUsername(), dto.getPassword());
        // 인증 진행하고, 인증 완료된 토큰 생성
        Authentication token = authenticationProvider.authenticate(authenticationToken);
        // 인증된 토큰의 사용자 정보를 쉽게 꺼낼 수 있도록 SecurityContext에 저장
        SecurityContextHolder.getContext().setAuthentication(token);
        return PeopleResponse.builder()
                .username((String) token.getPrincipal())
                .isAuthenticated(token.isAuthenticated())
                .build();
    }
}
