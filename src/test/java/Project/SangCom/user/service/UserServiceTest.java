package Project.SangCom.user.service;

import Project.SangCom.user.domain.Role;
import Project.SangCom.user.domain.User;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class UserServiceTest {
    @Autowired
    private UserService service;

    @Test
    @DisplayName("service를 통해 사용자 저장 후, ID를 통해 사용자 찾기")
    public void findUserById(){
        //given
        User user = User.builder()
                .username("username")
                .nickname("nickname")
                .email("test@naver.com")
                .role(Role.STUDENT)
                .build();
        
        //when
        Long savedUserId = service.saveUser(user);
        User foundUser = service.findUserById(savedUserId).get();

        //then
        Assertions.assertThat(savedUserId).isEqualTo(foundUser.getId());
    }

    @Test
    @DisplayName("service를 통해 사용자 저장 후, email을 통해 사용자 찾기")
    public void findUserByEmail(){
        //given
        User user = User.builder()
                .username("username")
                .nickname("nickname")
                .email("test@naver.com")
                .role(Role.STUDENT)
                .build();

        //when
        Long savedUserId = service.saveUser(user);
        User foundUser = service.findUserByEmail(user.getEmail()).get();

        //then
        Assertions.assertThat(user.getEmail()).isEqualTo(foundUser.getEmail());
    }
}