package com.example.demo.Auth.Token;

import com.example.demo.Security.Service.TokenService;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.security.Key;
import java.util.Date;
import java.util.Set;


/* JwtProvider란?
 * JWT 토큰을 직접적으로 다루는 클래스이다
 * 기본적으로 CRAV 기능을 구현하며, 따로 작성한 JwtFilter에서 인증을 위해 이 Provider를 가져다 쓴다.
 * (CRAV ==> Create, Resolve, getAuthentication, Validate
 *  즉, 토큰 생성, 토큰 획득, Authentication 획득, 토큰 검증을 의미한다)*/
@Slf4j
@Component
@PropertySource({"classpath:application-jwt.properties"})
public class JwtProvider {
    @Value("${jwt.secret}")
    private String secret;
    private final Key secretKey;
    private long tokenValidTime = 30 * 60 * 1000L;
    private final TokenService TokenService;

    public JwtProvider(TokenService TokenService) {
        this.secretKey = Keys.hmacShaKeyFor(Decoders.BASE64.decode(this.secret));
        this.TokenService = TokenService;
    }

    /* JWT 토큰 생성하기 */
    // Jwts로 설정하며, 다음 구조가 기본이 되는 JWT Payload 이다
    public String createToken(String username, Set<String> authorities) {
        Date now = new Date();

        return Jwts.builder()
                .setSubject(username)
                .claim("auth", authorities)
                .setIssuedAt(now)
                .setExpiration(new Date(now.getTime() + tokenValidTime))
                .signWith(secretKey, SignatureAlgorithm.ES256)
                .compact();
    }

    /* JWT 토큰 얻어오기 */
    // 클라이언트의 인증 요청 HTTP의 "X-AUTH-TOKEN" 헤더에서 값을 가져온다
    public String resolveToken(HttpServletRequest request) {
        return request.getHeader("X-AUTH-TOKEN");
    }

    /* JWT 토큰에서 인증 토큰 얻어오기 */
    // JWT는 애플리케이션 인증을 위한 토큰이다. 따라서 인증을 하려면, 해당 토큰에서 정보를 뽑아와야 한다
    public Authentication getAuthentication(String jwtToken) {
        String username = Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(jwtToken)
                .getBody().getSubject();

        UserDetails people = TokenService.loadUserByUsername(username);
        return new UsernamePasswordAuthenticationToken(people, jwtToken, people.getAuthorities());
    }

    /* JWT 토큰 유효성 검증하기 */
    // JWT 토큰은 무상태성을 가진다. 즉, 한번 발급하면 끝이라는 것이다.
    // 세션처럼 수정하거나 삭제할 수 없으므로, 만료 시간을 정해 재발급 받는 방법 밖에 없다
    // 해당 메소드는 그런 만료 상태를 검증하는 메소드이다
    public boolean validateToken(String jwtToken) {
        // Jwts가 parser 빌더로 JWT 토큰을 파싱하면서 검증도 같이 해준다
        // 따라서 별도로 검증 구문을 작성할 필요 없이, 예외만 잘 처리해주면 된다
        Jws<Claims> claims = null;
        try {
            claims = Jwts.parserBuilder()
                    .setSigningKey(secretKey)
                    .build()
                    .parseClaimsJws(jwtToken);

            return true;
        } catch (ExpiredJwtException e) {
            log.error("유효하지 않은 JWT 입니다.", e);
        } catch (UnsupportedJwtException e) {
            log.error("지원하지 않는 JWT 입니다.", e);
        } catch (MalformedJwtException e) {
            log.error("JWT의 구조가 올바르지 않습니다.", e);
        } catch (SignatureException e) {
            log.error("잘못된 JWT 서명 입니다.", e);
        } catch (IllegalArgumentException e) {
            log.error("잘못된 JWT 입니다.", e);
        }
        return false;
    }
}
