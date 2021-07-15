package com.example.demo.Security.Service;

import com.example.demo.Domain.People;
import com.example.demo.Repository.PeopleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TokenService implements UserDetailsService {
    private final PeopleRepository peopleRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        People people = peopleRepository.findById(username)
                .orElseThrow(NoSuchElementException::new);

        return toUser(people);
    }

    private User toUser(People people) {
        Set<GrantedAuthority> authorities = people.getAuthorities().stream()
                .map(authority -> new SimpleGrantedAuthority(authority.getRole()))
                .collect(Collectors.toSet());

        return (User) User.builder()
                .username(people.getUsername())
                .password(people.getPassword())
                .authorities(authorities)
                .build();
    }
}
