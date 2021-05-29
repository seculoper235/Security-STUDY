package com.example.demo.Repository;

import com.example.demo.Security.MyAuthority;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MyAuthorityRepository extends JpaRepository<MyAuthority, Long> {
}
