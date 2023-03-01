package Project.SangCom.user.service;

import Project.SangCom.user.domain.Role;
import Project.SangCom.user.domain.User;
import Project.SangCom.user.domain.embedded.StudentInfo;
import Project.SangCom.user.repository.UserRepository;
import Project.SangCom.util.exception.BusinessException;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
@Slf4j
class UserServiceTest {
    @Autowired
    private UserRepository repository;
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
                .role(Role.STUDENT.getKey())
                .build();
        
        //when
        Long savedUserId = service.saveUser(user);
        User foundUser = service.findUserById(savedUserId);

        //then
        Assertions.assertThat(user).isEqualTo(foundUser);
    }

    @Test
    @DisplayName("service를 통해 사용자 저장 후, email을 통해 사용자 찾기")
    public void findUserByEmail(){
        //given
        User user = User.builder()
                .username("username")
                .nickname("nickname")
                .email("test@naver.com")
                .role(Role.STUDENT.getKey())
                .build();

        //when
        service.saveUser(user);
        User foundUser = service.findUserByEmail(user.getEmail());

        //then
        Assertions.assertThat(user).isEqualTo(foundUser);
    }

    @Test
    @DisplayName("회원가입 시 email이 같다면 덮어쓰기한다.")
    public void whenEmailIsSameOverwriteInfo(){
        //given
        User user1 = User.builder()
                .role(Role.STUDENT.getKey())
                .email("test@naver.com")
                .nickname("nickname1")
                .username("username1")
                .studentInfo(new StudentInfo("1", "3", "23"))
                .build();

        User user2 = User.builder()
                .role(Role.STUDENT.getKey())
                .email("test@naver.com")
                .nickname("nickname2")
                .username("username2")
                .studentInfo(new StudentInfo("1", "5", "24"))
                .build();


        // when
        // 이메일이 같은 유저를 회원가입을 진행한다면, 덮어쓰기가 진행된다.
        repository.save(user1);
        Long registerId = service.registerUser(user2);

        User registerUser = repository.findById(registerId).get();

        //then
        //정보가 제대로 덮어쓰기 되어 있어야 한다.
        Assertions.assertThat(registerUser.getNickname()).isEqualTo(user2.getNickname());
        Assertions.assertThat(registerUser.getUsername()).isEqualTo(user2.getUsername());
        Assertions.assertThat(registerUser.getStudentInfo()).isEqualTo(user2.getStudentInfo());
        Assertions.assertThat(registerUser.getTeacherInfo()).isEqualTo(user2.getTeacherInfo());

    }

    @Test
    @DisplayName("회원가입 시 nickname은 unique하지 않으면 오류가 발생해야 한다.")
    public void nicknameNeedToBeUnique (){
        //given
        User user1 = User.builder()
                .role(Role.STUDENT.getKey())
                .email("test@naver.com")
                .nickname("nickname")
                .username("username")
                .build();

        User user2 = User.builder()
                .role(Role.STUDENT.getKey())
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

    @Test
    @DisplayName("회원가입 시 nickname의 길이는 10 이하까지 허용된다.")
    public void limitLengthOfNickname(){
        //given
        User user = User.builder()
                .role(Role.STUDENT.getKey())
                .email("test@naver.com")
                .build();

        User user2 = User.builder()
                .role(Role.STUDENT.getKey())
                .nickname("nickname")
                .email("test@naver.com")
                .studentInfo(StudentInfo.builder().grade("1").classes("2").number("23").build())
                .build();

        //when
        repository.save(user);

        //then
        service.registerUser(user2);
    }
}