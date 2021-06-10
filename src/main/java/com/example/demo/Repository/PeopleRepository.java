package com.example.demo.Repository;

import com.example.demo.Domain.People;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PeopleRepository extends JpaRepository<People, String> {
    /* Email로 Entity를 검색하는 이유?
     * Entity의 Id는 String Id이지만, Social 계정에서의 Id는 이메일이다.
     * 따라서 Email로 사용자를 검색하는 메소드가 필요하다.
     */
    Optional<People> findPeopleByEmail(String email);
}
