package com.example.demo.Security;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/* UserNamePasswordAuthenticationFilter 란?
 * 수많은 시큐리티 필터중 Authentication에 관련된 필터이며, 그 중 form 로그인 방식(ID, PW가 필요한)에 해당하는 필터이다.
 * 인증을 위한 토큰을 생성하거나, 인증을 진행하기 위해 AuthenticationManager를 설정하는 등의 역할을 담당한다. */
public class MyUPAuthFilter extends UsernamePasswordAuthenticationFilter {

    // AuthenticationManager를 설정하기 위한 생성자이다.
    public MyUPAuthFilter(AuthenticationManager authenticationManager) {
        // super()를 쓰면 기본 로그인 url(POST /login)도 같이 매칭되기 때문에, setAuthenticationManager()로 별도로 세팅한다.
        super.setAuthenticationManager(authenticationManager);
    }


    // 매니저를 통해 인증을 진행하는 메소드이다.
    // 인증을 위한 토큰을 생성하고, 이를 생성자에서 정의한 매니저에게 넘겨서 인증을 진행한다.
    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        // 인증 요청 토큰을 생성하는 것이므로, 유저네임과 비밀번호를 담아서 생성한다.
        // 컨트롤러를 통해 별도의 RequestDto로 입력받을 경우엔 getParameter로 원하는 변수명을 얻어올 수 있도록 한다.
        UsernamePasswordAuthenticationToken authRequest = new UsernamePasswordAuthenticationToken(request.getParameter("userName"), request.getParameter("password"));

        // 현재 HttpRequest에 담긴 세부 정보(IP 주소, 세션 Id)를 인증 요청 객체(authRequest)에 옮겨 담는다.
        // (앞으로의 인증은 authRequest로 진행할 것이기 때문)
        setDetails(request, authRequest);

        // 매니저를 통해 인증을 진행하고, 인증 완료된 객체를 반환하도록 한다.
        return this.getAuthenticationManager().authenticate(authRequest);
    }
}
