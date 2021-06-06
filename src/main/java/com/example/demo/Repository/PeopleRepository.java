package com.example.demo.Repository;

import com.example.demo.Domain.People;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PeopleRepository extends JpaRepository<People, String> {
    Optional<People> findByUsername(String username);
}
