package Project.SangCom.user.service;

import Project.SangCom.security.dto.OAuthRegisterRequest;
import Project.SangCom.user.domain.Role;
import Project.SangCom.user.domain.User;
import Project.SangCom.user.domain.embedded.StudentInfo;
import Project.SangCom.user.domain.embedded.TeacherInfo;
import Project.SangCom.user.repository.UserRepository;
import Project.SangCom.util.exception.BusinessException;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Slf4j
class UserServiceTest {
    @Autowired
    private UserRepository repository;
    @Autowired
    private UserService service;



    @Test
    @Transactional
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
        Assertions.assertThat(user).isEqualTo(foundUser);
    }

    @Test
    @Transactional
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
        service.saveUser(user);
        User foundUser = service.findUserByEmail(user.getEmail()).get();

        //then
        Assertions.assertThat(user).isEqualTo(foundUser);
    }

    @Test
    @Transactional
    @DisplayName("회원가입 시 email이 같다면 덮어쓰기한다.")
    public void whenEmailIsSameOverwriteInfo(){
        //given
        User user1 = User.builder()
                .role(Role.STUDENT)
                .email("test@naver.com")
                .nickname("nickname1")
                .username("username1")
                .studentInfo(new StudentInfo("1", "3", "23"))
                .build();

        User user2 = User.builder()
                .role(Role.STUDENT)
                .email("test@naver.com")
                .nickname("nickname2")
                .username("username2")
                .studentInfo(new StudentInfo("1", "5", "24"))
                .build();


        // when
        // 이메일이 같은 유저를 회원가입을 진행한다면, 덮어쓰기가 진행된다.
        repository.save(user1);
        Long registerId = service.registerUser(user2);

        user2.setId(registerId);
        User registerUser = repository.findById(registerId).get();

        //then
        Assertions.assertThat(registerUser).isEqualTo(user2);
        Assertions.assertThat(registerUser.getStudentInfo()).isEqualTo(user2.getStudentInfo());
        Assertions.assertThat(registerUser.getTeacherInfo()).isEqualTo(user2.getTeacherInfo());

    }

    @Test
    @Transactional
    @DisplayName("회원가입 시 nickname은 unique하지 않으면 오류가 발생해야 한다.")
    public void nicknameNeedToBeUnique (){
        //given
        User user1 = User.builder()
                .role(Role.STUDENT)
                .email("test@naver.com")
                .nickname("nickname")
                .username("username")
                .build();

        User user2 = User.builder()
                .role(Role.STUDENT)
                .email("test@naver.com")
                .nickname("nickname")
                .username("username")
                .build();

        //when
        //닉네임이 같은 유저가 회원가입을 진행한다면
        repository.save(user1);

        //then
        //validateDuplicateNickname 결과가 false가 나와야 한다.
        org.junit.jupiter.api.Assertions.assertThrows(BusinessException.class, () -> {
                    service.registerUser(user2);
                }
        );
    }
}