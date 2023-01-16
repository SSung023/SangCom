package Project.SangCom.security.controller;

import Project.SangCom.security.service.JwtTokenProvider;
import Project.SangCom.user.domain.Role;
import Project.SangCom.user.domain.User;
import Project.SangCom.user.domain.embedded.StudentInfo;
import Project.SangCom.user.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
class OAuthControllerTest {

    MockMvc mockMvc;
    @Autowired
    WebApplicationContext context;
    @Autowired
    UserService userService;
    @Autowired
    JwtTokenProvider provider;


    @BeforeEach
    public void setup(){
        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
						.build();

    }

    @Test
    @DisplayName("회원가입 정보를 받아서 UserService를 통해 회원가입에 성공해야 한다.")
    @Transactional
    public void registerUser() throws Exception {
        // given
        getAndSaveUser();

        String requestJson
                = "{\"role\":\"ROLE_STUDENT\", \"email\": \"test@naver.com\", " +
                "\"nickname\": \"nickname\", \"username\": \"username\", " +
                "\"grade\": \"1\", \"classes\": \"5\", \"number\": \"23\"}";


        // when & then
        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.message").value("회원가입 성공"));

    }

    @Test
    @Transactional
    @DisplayName("이미 가입되어있는 사용자에게 JWT token을 전달해주어야 한다.")
    public void passTokenToRegisteredUser() throws Exception {
        String requestJson = "{\"email\":\"test@naver.com\"}";

        getAndSaveUser();

        mockMvc.perform(post("/api/auth/token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(header().exists("Authorization"))
                .andExpect(header().exists("Set-Cookie"))
                .andExpect(status().isOk());

    }

    @Test
    @Transactional
    @DisplayName("header의 access-token을 통해 어떤 사용자의 토큰인지 확인할 수 있다.")
    public void checkWhichUserByToken() throws Exception {
        //given
        User user = User.builder()
                .username("username")
                .nickname("nickname")
                .email("test@naver.com")
                .role(Role.STUDENT)
                .studentInfo(new StudentInfo("1", "3", "12"))
                .build();
        userService.saveUser(user);
        String accessToken = provider.createAccessToken(user);

        //when, then
        mockMvc.perform(get("/api/auth/user")
                .header("Authorization", accessToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.message").value(""))
                .andExpect(jsonPath("$.data.role").value("STUDENT"))
                .andExpect(jsonPath("$.data.nickname").value("nickname"))
                .andExpect(jsonPath("$.data.grade").value("1"))
                .andExpect(jsonPath("$.data.classes").value("3"))
                .andExpect(jsonPath("$.data.number").value("12"))
                .andExpect(jsonPath("$.data.chargeGrade").value(""))
                .andExpect(jsonPath("$.data.chargeSubject").value(""));
    }




    private User getAndSaveUser() {
        User user = User.builder()
                .email("test@naver.com")
                .role(Role.NOT_VERIFIED)
                .build();
        userService.saveUser(user);

        return user;
    }
}