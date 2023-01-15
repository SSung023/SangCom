package Project.SangCom.security.controller;

import Project.SangCom.user.domain.Role;
import Project.SangCom.user.domain.User;
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
        User user = User.builder()
                .email("test@naver.com")
                .role(Role.NOT_VERIFIED)
                .build();
        userService.saveUser(user);

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


}