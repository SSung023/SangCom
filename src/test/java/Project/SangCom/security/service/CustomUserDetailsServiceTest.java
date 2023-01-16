package Project.SangCom.security.service;

import Project.SangCom.user.domain.Role;
import Project.SangCom.user.domain.User;
import Project.SangCom.user.repository.UserRepository;
import Project.SangCom.user.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.transaction.annotation.Transactional;


@SpringBootTest
@Transactional
@Slf4j
class CustomUserDetailsServiceTest {

    @Autowired
    CustomUserDetailsService customUserDetailsService;
    @Autowired
    UserRepository userRepository;
    @Autowired
    UserService service;

    
    @Test
    @DisplayName("username(email)을 통해 UserDetails 객체를 받아올 수 있다.")
    public void getAuthenticationByEmail(){
        //given
        User user = getUser();
//        service.registerUser(user);
        userRepository.save(user);

        //when
        UserDetails userDetails = customUserDetailsService.loadUserByUsername(user.getEmail());

        //then
        log.info(userDetails.toString());
        Assertions.assertThat(userDetails).isInstanceOf(User.class);
    }


    private User getUser() {
        return User.builder()
                .username("username")
                .nickname("nickname1")
                .email("test@naver.com")
                .role(Role.STUDENT)
                .build();
    }
}