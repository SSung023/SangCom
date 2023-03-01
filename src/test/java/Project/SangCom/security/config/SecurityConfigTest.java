package Project.SangCom.security.config;

import Project.SangCom.security.dto.AccessTokenUserRequest;
import Project.SangCom.security.service.JwtTokenProvider;
import Project.SangCom.user.domain.Role;
import Project.SangCom.user.domain.User;
import Project.SangCom.util.exception.BusinessException;
import Project.SangCom.util.exception.SuccessCode;
import Project.SangCom.utils.WithMockCustomUser;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import static Project.SangCom.security.service.JwtTokenProvider.AUTHORIZATION_HEADER;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest
@Transactional
class SecurityConfigTest {
    MockMvc mockMvc;
    @Autowired
    WebApplicationContext context;
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
    @DisplayName("permitAll()에 등록한 uri는 인증 없이 접근 가능해야 한다.")
    public void CheckPermitAllOperation() throws Exception {
        mockMvc.perform(get("/api/auth/test/login")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(SuccessCode.SUCCESS.getKey()))
                .andExpect(jsonPath("$.message").value(SuccessCode.SUCCESS.getMessage()));
    }

    @Test
    @DisplayName("swagger에 인증 없이 접근 가능해야 한다.")
    public void CheckSwaggerEnter() throws Exception {
        mockMvc.perform(get("/swagger-ui.html"))
                .andExpect(status().is3xxRedirection());

    }

    @Test
    @DisplayName("설정한 api 외에는 NOT_VERIFIED는 접근하지 못한다.")
    @WithMockCustomUser(role = Role.NOT_VERIFIED)
    public void NotRegisteredUriCannnotIn() throws Exception {
        //given
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String accessToken = getAccessToken(user);

        //when&then
        mockMvc.perform(get("/api/temp")
                .header(AUTHORIZATION_HEADER, accessToken))
                .andExpect(status().is4xxClientError());

    }






    private String getAccessToken(User user) {
        AccessTokenUserRequest tokenUserRequest = AccessTokenUserRequest.builder()
                .email(user.getEmail())
                .role(user.getRole())
                .build();

        return provider.createAccessToken(tokenUserRequest);
    }
}