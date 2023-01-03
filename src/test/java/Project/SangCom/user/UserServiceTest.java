package Project.SangCom.user;

import Project.SangCom.user.domain.User;
import Project.SangCom.user.repository.UserRepository;
import Project.SangCom.user.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * 소셜 로그인 + 자체 회원가입(소속 확인 + 닉네임 설정)
 * 소셜 로그인을 성공하면 이메일을 받아서, 이메일을 통해 이전 가입 여부를 확인한다.
 * 가입 X시 인증 과정으로 이동 -> 학생인지 교사인지 선택
 * 1) 학생인 경우에는 학년/반/번호가 맞는지 확인  2)교사인 경우에는 담당 학년/ 담당 과목이 맞는지 확인
 * 가입 O시 -> 메인화면으로 이동
 */
public class UserServiceTest {

    @Autowired private UserRepository repo;

    @Test
    public void join_user_through_service(){
        UserService service = new UserService(repo);
    }


}
