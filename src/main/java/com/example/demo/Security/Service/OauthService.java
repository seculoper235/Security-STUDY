package com.example.demo.Security.Service;

import com.example.demo.Domain.Dto.PeopleDto;
import com.example.demo.Domain.People;
import com.example.demo.Repository.PeopleRepository;
import com.example.demo.Security.Dto.GooglePeople;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpSession;
import java.util.Collections;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OauthService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {
    private final PeopleRepository peopleRepository;
    private final HttpSession httpSession;
    /* form 방식의 loadByUserName()과 동일하게 입력을 받아 인증 전용 객체를 반환하는 역할이다.
     * 파라미터: oauthUserRequest
     * - 여기에 OAuthToken 정보나, 클라이언트 등록 정보가 담겨있다.
     * 결과값: OAuthUser
     * - OAuth 인증 전용 객체로, 보통 DefaultOAuth2User를 반환한다.
     * 메소드 요약
     * - OauthRequest를 가지고 DefaultOAuthUser()에 값을 담아 객체를 생성한다. 이때 OAuth 인증에 필요한 권한들, 이름이나 여러 속성들(이메일, 프로필 이미지 등)을 설정한다.
     *   또한 이를 오버라이드하여 원하는 작업을 추가로 진행할 수 있다.
     */
    @Override
    public OAuth2User loadUser(OAuth2UserRequest oAuth2UserRequest) throws OAuth2AuthenticationException {
        /* 기본적인 loadUser 메소드 실행 */
        // 우선 기본적으로 실행되는 loadUser()를 실행하고, 이후 원하는 작업을 추가로 진행한다.
        // (DB에 데이터 저장, 세션 저장 등)
        OAuth2UserService<OAuth2UserRequest, OAuth2User> delegate = new DefaultOAuth2UserService();
        OAuth2User oAuth2User = delegate.loadUser(oAuth2UserRequest);

        /* OAuth 인증에 필요한 필드 뽑아내기 */
        // request에서 OAuth 인증에 필요한 정보들을 뽑아낸다.
        // (클라이언트 개인의 등록 ID와 이름 속성 키를 뽑아낸다)
        String registrationId = oAuth2UserRequest.getClientRegistration().getRegistrationId();
        String attributeName = oAuth2UserRequest.getClientRegistration().getProviderDetails().getUserInfoEndpoint().getUserNameAttributeName();

        /* 자유로운 사용을 위해 DTO 객체에 담기 */
        // 서버 내에서 자유롭게 데이터를 다루기 위해, PeopleDto에 필요한 값을 담는다.
        PeopleDto peopleDto = PeopleDto.builder()
                .username((String) oAuth2User.getAttributes().get("name"))
                .email((String) oAuth2User.getAttributes().get("email"))
                .image((String) oAuth2User.getAttributes().get("image"))
                .attributes(oAuth2User.getAttributes())
                .nameAttributeKey(attributeName)
                .build();

        /* DB에 저장하기 */
        // Dto를 Entity로 바꿔서 DB에 업데이트/저장한다.
        People people = savePeople(peopleDto);

        /* Session에 저장하기 */
        // 저장하고 반환된 객체는 인증된 객체, Principal이다. 따라서 여기서 인증 최소한의 필드만 뽑아서 Session 객체에 담고 Session에 저장한다.
        httpSession.setAttribute("people", new GooglePeople(people));

        /* OAuth 전용 객체 반환하기 */
        // 필요한 작업이 모두 끝났으면, 메소드 본래의 역할대로 OAuth 전용 객체를 생성하여 반환한다.
        // (form 기반 인증의 loadUserByName()에서 UserDetails의 구현체와 같은 맥락의 개체이다.)

        /* 권한 변경이 안되는 문제 */
        // DB의 권한을 ADMIN으로 바꿔도 여전히 권한을 USER로 인식하는 문제.
        // -> 권한을 oAuth2User가 아닌, DB 엔티티에서 직접 뽑아온다.
        return new DefaultOAuth2User(
                Collections.unmodifiableSet(people.getAuthorities().stream().map(e ->
                        new SimpleGrantedAuthority(e.getRole())
                ).collect(Collectors.toSet())),
                peopleDto.getAttributes(),
                peopleDto.getNameAttributeKey());
    }

    public People savePeople(PeopleDto peopleDto) {
        // save 헐 때, 한 번 조회하고 저장하는 이유?
        // 그냥 save의 기능만 있다면 모르겠지만, 이 경우 해당 Social 계정 정보가 바뀔수도 있으므로 update의 기능도 필요하다
        // 이때 update 시에는 보통 id 값과 변경되는 값만을 Entity에 담아서 저장하는데, 사실 권장되는 방법은 아니다.
        // 수정이든 저장이든 모두 save 메소드로 할 수 있는 이유는 JPA는 내부적으로 영속성 컨텍스트에서 먼저 엔티티를 불러와서
        // 있으면 update를 하고, 없다면 save를 하여 저장을 한다.
        // 따라서 이러한 점 때문에 먼저 find로 엔티티를 불러오고 해당 필드를 수정하여 save하는 것이 의미적으로 좀 더 바람직 하다고 할 수 있다.
        People people = peopleRepository.findPeopleByEmail(peopleDto.getEmail())
                .map(entity -> entity.oAuthData(peopleDto.getImage(), peopleDto.getUsername()))
                .orElseThrow(NoSuchElementException::new);

        return peopleRepository.save(people);
    }
}
