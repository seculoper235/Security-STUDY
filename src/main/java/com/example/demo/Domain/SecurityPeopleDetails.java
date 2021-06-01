package com.example.demo.Domain;

import com.example.demo.Security.Dto.MyAuthority;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.*;

/* UserDetails 란?
 * Security 객체로 사용하기에 꼭 필요한 필ㄷ/메소드만 모아놓은 인터페이스이다
 * 오버라이드만 빼면 설졍이 매우 자유로우며 간편하다.
 * 하지만 필요한 것은 일일히 다 작성해줘야 한다는 단점이 있다.
 */
@NoArgsConstructor
public class SecurityPeopleDetails implements UserDetails {
     private People people;

    public SecurityPeopleDetails(People people) {
        this.people = people;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Collection<GrantedAuthority> collection = new HashSet<>();

        for (MyAuthority myAuthority : people.getAuthorities()) {
            collection.add(new GrantedAuthority() {
                @Override
                public String getAuthority() {
                    return myAuthority.getRole();
                }
            });
        }
        return collection;
    }

    @Override
    public String getPassword() {
        return people.getPassword();
    }

    @Override
    public String getUsername() {
        return people.getUsername();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
