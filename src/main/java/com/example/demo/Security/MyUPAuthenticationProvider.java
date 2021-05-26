package com.example.demo.Security;

import com.example.demo.Domain.SecurityPeopleDetails;
import com.example.demo.Service.PeopleDetailsService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;


/* AuthenticationProvider 란?
 * 인증 과정을 진행하는 건 AuthenticationManager(ProviderManager)가 맞지만, 실질적인 인증의 처리는 해당 인터페이스가 진행한다.
 * ProviderManager는 모든 AuthenticationProvider를 대상으로 for문을 돌면서 인증을 진행한다.
 * 따라서 AuthenticationProvider를 구현한다는 것은 곧, 내 임의대로 커스텀한 인증을 만들어 사용하겠다는 것이다. */
@RequiredArgsConstructor
public class MyUPAuthenticationProvider implements AuthenticationProvider {
    // 인증에 필요한 DetailsService와 암호화 정책을 주입한다.
    private final PeopleDetailsService peopleDetailsService;
    private final BCryptPasswordEncoder passwordEncoder;

    // 인증의 과정을 작성하는 메소드이다.
    // 해당 인증은 form 방식이므로 요청 정보의 password와 Principal의 password를 암호화 비교하여 인증한다.
    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        UsernamePasswordAuthenticationToken token = (UsernamePasswordAuthenticationToken) authentication;
        String peopleName = token.getName();
        String peoplePassword = (String) token.getCredentials();

        // 작성한 DetailsService로 principal(인증된 객체)를 불러온다.
        SecurityPeopleDetails people = (SecurityPeopleDetails) peopleDetailsService.loadUserByUsername(peopleName);

        // 원하는 패스워드 암호화 방식으로 principal의 비밀번호와 credential을 비교한다.
        if(!passwordEncoder.matches(peoplePassword, people.getPassword()))
            throw new BadCredentialsException(people.getUsername()+"부적절한 비밀번호입니다.");

        // 인증이 성공했으므로 토큰을 새로 생성해야 한다.
        // 인증이 완료된 후엔 인가(Authorization)을 진행해야 하므로, 이를 위해 해당 사용자의 권한목록까지 같이 담아서 생성한다.
        return new UsernamePasswordAuthenticationToken(people, peoplePassword, people.getAuthorities());
    }

    // 현재 Provider가 어떤 인증 방식(토큰)을 지원하는지 작성하는 메소드이다.
    // 해당 클래스는 form 방식이므로, UsernamePassword~토큰을 지원한다.
    @Override
    public boolean supports(Class<?> authentication) {
        return UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication);
    }
}
