package com.example.demo.Domain;

import org.springframework.security.core.userdetails.User;

/* User 란?
 * 사용의 편의성을 위해 UserDetails를 구현한 클래스이다.
 * 빌더 패턴과 Authority 관련 메소드가 존재하며, 이미 작성된 생성자나 메소드가 존재하기에 UserDetails보다 사용에 있어서 편리하다.
 * 하지만 자유 설정에 제약이 있으며, 보이지 않는 기능이 있어서 잘못 사용하면 코드가 꼬일 수 있다.
 */
public class SecurityPeople extends User {
    private String nickname;

    public SecurityPeople(People people) {
        super(people.getUsername(), people.getPassword(), people.getAuthorities());

        this.nickname = people.getNickname();
    }
}
