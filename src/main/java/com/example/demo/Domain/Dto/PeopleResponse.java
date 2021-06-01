package com.example.demo.Domain.Dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.security.core.Authentication;

@Getter
@NoArgsConstructor
public class PeopleResponse {
    private String username;
    private boolean isAuthenticated;

    @Builder
    public PeopleResponse(String username, boolean isAuthenticated) {
        this.username = username;
        this.isAuthenticated = isAuthenticated;
    }
}
