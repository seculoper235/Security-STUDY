package com.example.demo.Security.Service;

import com.example.demo.Domain.Dto.PeopleDto;
import com.example.demo.Domain.Mapper.PeopleMapper;
import com.example.demo.Domain.OauthUser;
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
        People people = PeopleMapper.toEntity(peopleDto);

        return new DefaultOAuth2User(oAuth2User.getAuthorities(), oAuth2User.getAttributes(), attributeName);
    }
}
