package Project.SangCom.security.dto;

import Project.SangCom.user.domain.Role;
import Project.SangCom.user.domain.User;
import Project.SangCom.user.domain.embedded.StudentInfo;
import Project.SangCom.user.domain.embedded.TeacherInfo;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;



@SpringBootTest
@Transactional
@Slf4j
class OAuthRegisterRequestTest {


    @Test
    @DisplayName("OAuthRegisterRequest를 User(학생)로 변환할 수 있다.")
    public void ConvertToStudent(){
        //given
        OAuthRegisterRequest request
                = OAuthRegisterRequest.builder()
                .role("student")
                .email("test@naver.com")
                .nickname("nickname")
                .username("username")
                .grade("1")
                .classes("3")
                .number("23")
                .build();

        //when
        User receivedUser = request.toEntity();
        log.info(receivedUser.toString());

        //then
        Assertions.assertThat(receivedUser.getRole()).isEqualTo(Role.STUDENT.getKey());
        Assertions.assertThat(receivedUser.getEmail()).isEqualTo("test@naver.com");
        Assertions.assertThat(receivedUser.getNickname()).isEqualTo("nickname");
        Assertions.assertThat(receivedUser.getUsername()).isEqualTo("username");
        Assertions.assertThat(receivedUser.getStudentInfo()).isEqualTo(new StudentInfo("1", "3", "23"));
    }

    @Test
    @DisplayName("OAuthRegisterRequest를 User(교사)로 변환할 수 있다.")
    public void ConvertToTeacher(){
        //given
        OAuthRegisterRequest request
                = OAuthRegisterRequest.builder()
                .role("teacher")
                .email("test@naver.com")
                .nickname("nickname")
                .username("username")
                .chargeGrade("1")
                .chargeSubject("Science")
                .build();

        //when
        User receivedUser = request.toEntity();
        log.info(receivedUser.toString());

        //then
        Assertions.assertThat(receivedUser.getRole()).isEqualTo(Role.TEACHER.getKey());
        Assertions.assertThat(receivedUser.getEmail()).isEqualTo("test@naver.com");
        Assertions.assertThat(receivedUser.getNickname()).isEqualTo("nickname");
        Assertions.assertThat(receivedUser.getUsername()).isEqualTo("username");
        Assertions.assertThat(receivedUser.getTeacherInfo()).isEqualTo(new TeacherInfo("1", "Science"));

    }
}