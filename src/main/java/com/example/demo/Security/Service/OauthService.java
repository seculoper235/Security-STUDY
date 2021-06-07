package com.example.demo.Security.Service;

import com.example.demo.Domain.Dto.PeopleDto;
import com.example.demo.Domain.People;
import com.example.demo.Repository.PeopleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class OauthService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {
    private final PeopleRepository peopleRepository;
    /* form 방식의 loadByUserName()과 동일하게 입력을 받아 인증 전용 객체를 반환하는 역할이다.
     * 파라미터: oauthUserRequest
     * - 여기에 OAuthToken 정보나, 클라이언트 등록 정보가 담겨있다.
     * 결과값: OAuthUser
     * - OAuth 인증 전용 객체로, 보통 DefaultOAuth2User를 반환한다.
     * 메소드 요약
     * - OauthRequest를 가지고 DefaultOAuthUser()에 값을 담아 객체를 생성한다. 이때 OAuth 인증에 필요한 권한들, 이름이나 여러 속성들(이메일, 프로필 이미지 등)을 설정한다.
     */
    @Override
    public OAuth2User loadUser(OAuth2UserRequest oAuth2UserRequest) throws OAuth2AuthenticationException {
        // 우선 DefaultOAuthUserService로 OAuth 전용 객체를 반환함
        OAuth2UserService<OAuth2UserRequest, OAuth2User> delegate = new DefaultOAuth2UserService();
        OAuth2User oAuth2User = delegate.loadUser(oAuth2UserRequest);

        // request의 ID와 oAuth2User의 속성(google의 이메일, 개인정보 등)같은 People 엔티티에 필요한 속성만을 뽑아낸다.
        String registrationId = oAuth2UserRequest.getClientRegistration().getRegistrationId();
        String attributeName = oAuth2UserRequest.getClientRegistration().getProviderDetails().getUserInfoEndpoint().getUserNameAttributeName();

        // PeopleDto에 필요한 값을 담는다.
        PeopleDto peopleDto = PeopleDto.builder()
                .username((String) oAuth2User.getAttributes().get("name"))
                .email((String) oAuth2User.getAttributes().get("email"))
                .image((String) oAuth2User.getAttributes().get("image"))
                .attributes(oAuth2User.getAttributes())
                .nameAttributeKey(attributeName)
                .build();

        // Mapper로 Dto를 Entity로 바꾸고 저장한다.
        // 왜 바로 Entity에 담지 않을까?
        // -> 엔티티는 DB와 창구 역할을 하는 객체이므로, 외부 값을 직접 Entity에 담는 것은 좋지 않다.
        //    Dto를 거쳐 Mapper로 담는 것이 코드의 확작성에도 좋고 보기에도 깔끔하다.
        People people = savePeople(peopleDto);

        return new DefaultOAuth2User(oAuth2User.getAuthorities(), peopleDto.getAttributes(), peopleDto.getNameAttributeKey());
    }

    public People savePeople(PeopleDto peopleDto) {
        // save 헐 때, 한 번 조회하고 저장하는 이유?
        // 그냥 save의 기능만 있다면 모르겠지만, 이 경우 해당 Social 계정 정보가 바뀔수도 있으므로 update의 기능도 필요하다
        // 이때 update 시에는 보통 id 값과 변경되는 값만을 Entity에 담아서 저장하는데, 사실 권장되는 방법은 아니다.
        // 수정이든 저장이든 모두 save 메소드로 할 수 있는 이유는 JPA는 내부적으로 영속성 컨텍스트에서 먼저 엔티티를 불러와서
        // 있으면 update를 하고, 없다면 save를 하여 저장을 한다.
        // 따라서 이러한 점 때문에 먼저 find로 엔티티를 불러오고 해당 필드를 수정하여 save하는 것이 의미적으로 좀 더 바람직 하다고 할 수 있다.

        /* 이메일로 찾는 이유? */
        // 여기서 이메일이란 Social 계정 정보이고 Social 계정에선 이메일이 곧 Id 이기 때문에, 이메일로 계정을 찾았다.
        People people = peopleRepository.findPeopleByEmail(peopleDto.getEmail())
                .map(entity -> People.builder()
                                    .email(peopleDto.getUsername())
                                    .image(peopleDto.getImage())
                                    .build())
                .orElseThrow(NoSuchElementException::new);

        return peopleRepository.save(people);
    }
}
