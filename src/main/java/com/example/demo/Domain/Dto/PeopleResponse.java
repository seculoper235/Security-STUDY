package com.example.demo.Domain.Dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.security.core.Authentication;

@Getter
@NoArgsConstructor
public class PeopleResponse {
    private Authentication authInfo;
    private boolean isAuthenticated;

    @Builder
    public PeopleResponse(Authentication authInfo, boolean isAuthenticated) {
        this.authInfo = authInfo;
        this.isAuthenticated = isAuthenticated;
    }
}
