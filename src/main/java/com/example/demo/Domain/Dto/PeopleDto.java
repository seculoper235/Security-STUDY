package com.example.demo.Domain.Dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Map;

@Getter
@NoArgsConstructor
// 순수하게 OAuth를 사용하기 위한 DTO 객체이다.
// DefaultOAuth2User 객체에 팔요한 내용을 필드로 갖는다.
public class PeopleDto {
    private String username;
    private String email;
    private String image;
    private Map<String, Object> attributes;
    private String nameAttributeKey;

    @Builder
    public PeopleDto(String username, String email, String image, Map<String, Object> attributes, String nameAttributeKey) {
        this.username = username;
        this.email = email;
        this.image = image;
        this.attributes = attributes;
        this.nameAttributeKey = nameAttributeKey;
    }
}
