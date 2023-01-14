package Project.SangCom.security.config;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest
class SecurityConfigTest {
    MockMvc mockMvc;
    @Autowired
    WebApplicationContext context;

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
        mockMvc.perform(get("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.message").isEmpty())
                .andExpect(jsonPath("$.data.email").value("test@naver.com"));
    }

    @Test
    @DisplayName("swagger에 인증 없이 접근 가능해야 한다.")
    public void CheckSwaggerEnter() throws Exception {
        mockMvc.perform(get("/swagger-ui.html"))
                .andExpect(status().is3xxRedirection());

    }

    @Test
    @DisplayName("permitAll에 등록하지 않은 uri는 인증 없이 접근할 수 없다.")
    public void NotRegisteredUriCannnotIn() throws Exception {
        mockMvc.perform(get("/api/test"))
                .andExpect(status().is3xxRedirection());

    }
}