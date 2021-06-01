package com.example.demo.Security.Dto;

import com.example.demo.Domain.People;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import javax.persistence.*;

@Entity
@Getter
@Table(name = "myauthority")
@EqualsAndHashCode(of = "id")
public class MyAuthority {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String role;

    @ManyToOne
    @JoinColumn(name = "people_id")
    private People people;
}
