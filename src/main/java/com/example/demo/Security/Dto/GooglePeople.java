package com.example.demo.Security.Dto;

import com.example.demo.Domain.People;
import lombok.Builder;
import lombok.Getter;

import java.io.Serializable;

@Getter
public class GooglePeople implements Serializable {
    private final String name;
    private final String email;
    private final String image;

    @Builder
    public GooglePeople(People people) {
        this.name = people.getUsername();
        this.email = people.getEmail();
        this.image = people.getImage();
    }
}
