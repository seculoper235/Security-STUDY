package com.example.demo.Domain.Dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class PeopleDto {
    private String username;
    private String password;

    @Builder
    public PeopleDto(String username, String password) {
        this.username = username;
        this.password = password;
    }
}
