package Project.SangCom.scrap.controller;

import Project.SangCom.security.dto.AccessTokenUserRequest;
import Project.SangCom.security.service.JwtTokenProvider;
import Project.SangCom.user.domain.Role;
import Project.SangCom.user.domain.User;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;

@SpringBootTest
@Transactional
@Slf4j
@ActiveProfiles({"jwt"})
public class ScrapControllerTest {

    @Value("${jwt.secret}")
    String secretKey;
    @Value("${jwt.refresh-secret}")
    String refreshSecretKey;
    @Value("${jwt.refresh-token-validity-in-seconds}")
    Long refreshTokenValidityInMilliseconds;
    MockMvc mockMvc;
    @Autowired WebApplicationContext context;
    @Autowired JwtTokenProvider provider;

    String AUTHORIZATION_HEADER = "Authorization";

    @BeforeEach
    public void setup(){
        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .build();
    }
    
    
    @Test
    @DisplayName("")
    public void (){
        //given
        
        //when
        
        //then
        
    }
    
    
    
    







    private User getUser() {
        return User.builder()
                .username("username")
                .nickname("nickname")
                .email("test@naver.com")
                .role(Role.STUDENT)
                .build();
    }
    private String getAccessToken() {
        User user = getUser();
        AccessTokenUserRequest tokenUserRequest = AccessTokenUserRequest.builder()
                .email(user.getEmail())
                .role(user.getRole().getKey())
                .build();

        return provider.createAccessToken(tokenUserRequest);
    }
}
