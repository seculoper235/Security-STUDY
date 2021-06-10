package com.example.demo.Security.Dto;

import com.example.demo.Domain.People;
import lombok.Builder;
import lombok.Getter;

import java.io.Serializable;

/* 인증된 사용자의 정보만을 담는 객체
 * PeopleDto와 비슷해 보일 수도 있으나, PeopleDto는 단순히 서버 내에서 사용하기 편하도록 데이터를 담는 객체일 뿐이고,
 * 해당 객체는 세션에 저장하기 위해 인증된 사용자 정보만을 담는 객체이다.
 * 따라서 간략하게 사용자 이름과 OAuth 범위 정보를 필드로 넣어 세션에 저장한다. */
@Getter
public class GooglePeople implements Serializable {
    // 사용자 이름
    private final String name;

    // OAuth 범위 필드
    private final String email;
    private final String image;

    @Builder
    public GooglePeople(People people) {
        this.name = people.getUsername();
        this.email = people.getEmail();
        this.image = people.getImage();
    }
}
