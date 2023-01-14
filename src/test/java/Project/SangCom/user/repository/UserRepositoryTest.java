package Project.SangCom.user.repository;


import Project.SangCom.user.domain.Role;
import Project.SangCom.user.domain.User;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Optional;

@SpringBootTest
//@ExtendWith(MockitoExtension.class)
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;


    @Test
    @DisplayName("사용자 저장 후 ID를 통해 비교")
    public void saveAndCompareId(){
        // given
        User user = User.builder()
                .nickname("nickname")
                .email("test@naver.com")
                .role(Role.STUDENT)
                .build();
        user.setId(1L);

        // when
        User savedUser = userRepository.save(user);

        // then
        Assertions.assertThat(savedUser.getId()).isEqualTo(1L);
    }

    @Test
    @DisplayName("사용자 저장 후 이메일을 통해 찾았을 때, 이메일이 동일해야 한다.")
    public void findUserByEmail(){
        //given
        User user = User.builder()
                .nickname("nickname")
                .email("test@naver.com")
                .role(Role.STUDENT)
                .build();
        user.setId(1L);

        //when
        User savedUser = userRepository.save(user);
        String email = savedUser.getEmail();

        Optional<User> byEmail = userRepository.findByEmail(email);

        // then
        // 왜 User 자체를 비교하면 데이터는 같은데 왜 다르다고 나올까?
        Assertions.assertThat(email).isEqualTo(byEmail.get().getEmail());
    }



}