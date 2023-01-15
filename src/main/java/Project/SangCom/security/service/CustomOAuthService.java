package Project.SangCom.security.service;

import Project.SangCom.user.domain.Role;
import Project.SangCom.user.domain.User;
import Project.SangCom.user.repository.UserRepository;
import Project.SangCom.util.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
@Slf4j
@RequiredArgsConstructor
public class CustomOAuthService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {

    private final UserRepository repository;


    @Override
    @Transactional
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2UserService<OAuth2UserRequest, OAuth2User> oAuth2UserService = new DefaultOAuth2UserService();
        OAuth2User oAuth2User = oAuth2UserService.loadUser(userRequest);

        // 현재 진행 중인 서비스가 무엇인지 구분하기 위해 값을 받음 -> kakao, google, naver 등
        String serviceName = userRequest.getClientRegistration().getRegistrationId();
        // OAuth2 로그인의 시 키 값이 서비스마다 다르기 때문에 변수로 받아둠 (google: sub, naver: response, kakao: id)
        String userNameAttributeName = userRequest.getClientRegistration().getProviderDetails().getUserInfoEndpoint().getUserNameAttributeName();

        // 요청한 사용자의 정보를 Map 형태로 저장
        Map<String, Object> attributes = oAuth2User.getAttributes();
        String email;

        // 서비스가 카카오인 경우 이메일 정보를 받아온다.
        if ("kakao".equals(serviceName)){
            Map<String, Object> profile = (Map<String, Object>) attributes.get("kakao_account");
            email = (String) profile.get("email");
            log.info("email: " + email);
        }
        else {
            throw new BusinessException("허용되지 않는 인증입니다.");
        }

        User user;
        Optional<User> optionalUser = repository.findByEmail(email);
        String accessToken = userRequest.getAccessToken().getTokenValue();

        // DB에서 일치하는 email을 찾은 경우
        if (optionalUser.isPresent()){
            user = optionalUser.get();
            log.info("DB에 존재하는 사용자입니다.");
        }
        // DB에 없는 사용자인 경우 - 이메일 정보만 저장하고 미인증사용자로 저장
        else {
            user = User.builder()
                    .email(email)
                    .role(Role.NOT_VERIFIED)
                    .build();
            repository.save(user);
        }


        //인증된 사용자를 반환
        return new DefaultOAuth2User(Collections.singleton(new SimpleGrantedAuthority(user.getRole().getKey()))
                ,oAuth2User.getAttributes()
                ,userNameAttributeName);
    }
}
