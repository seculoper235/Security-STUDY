package com.example.demo.Service;

import com.example.demo.Domain.SecurityPeople;
import com.example.demo.Domain.People;
import com.example.demo.Domain.SecurityPeopleDetails;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/* UserDetailsService 란?
 * loadByUsername() 메소드로 시큐리티 전용 객체를 얻어내는 인터페이스이다.
 * 이 부분에서 DB의 객체를 Security 객체로 변환하는 과정이 일어난다.
 */
@Service
public class PeopleDetailsService implements UserDetailsService {
    // 데이터를 받는 People 에다가 값을 담고, 다시 인증을 위한 객체 People에 담아서 값을 리턴함
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // DB 객체인 People에 DB 값을 담음
        People people = new People();

        // Security 전용 객체에 DB 객체를 옮겨 담아서 리턴
        return new SecurityPeopleDetails(people);
    }
}
