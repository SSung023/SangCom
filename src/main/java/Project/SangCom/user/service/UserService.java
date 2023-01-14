package Project.SangCom.user.service;

import Project.SangCom.security.dto.OAuthRegisterRequest;
import Project.SangCom.user.domain.Role;
import Project.SangCom.user.domain.User;
import Project.SangCom.user.domain.embedded.StudentInfo;
import Project.SangCom.user.domain.embedded.TeacherInfo;
import Project.SangCom.user.repository.UserRepository;
import Project.SangCom.util.exception.BusinessException;
import Project.SangCom.util.exception.ExMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class UserService {
    private final UserRepository repository;


    @Transactional
    public Long saveUser(User user){
        repository.save(user);
        return user.getId();
    }

    /**
     * 전달받은 객체의 email을 통해 DB에서 정보를 꺼내고, 정보 수정
     */
    @Transactional
    public Long registerUser(OAuthRegisterRequest receivedUser){
        Optional<User> byIdUser = repository.findByEmail(receivedUser.getEmail());

        if (byIdUser.isEmpty())
            throw new BusinessException(ExMessage.DATA_ERROR_NOT_FOUND);

        User targetUser = byIdUser.get();

        if (receivedUser.getRole().equals("ROLE_STUDENT")) {
            targetUser.setRole(Role.STUDENT);
        }
        else if (receivedUser.getRole().equals("ROLE_TEACHER")) {
            targetUser.setRole(Role.TEACHER);
        }

        // Builder 패턴 이용
        targetUser.setNickname(receivedUser.getNickname());
        targetUser.setUsername(receivedUser.getUsername());
        targetUser.setStudentInfo
                (StudentInfo.builder()
                        .grade(receivedUser.getGrade())
                        .classes(receivedUser.getClasses())
                        .number(receivedUser.getNumbers())
                        .build());
        targetUser.setTeacherInfo
                (TeacherInfo.builder()
                        .chargeGrade(receivedUser.getChargeGrade())
                        .chargeSubject(receivedUser.getChargeSubject())
                        .build());

        return targetUser.getId();
    }



    public Optional<User> findUserByEmail(String email){
        Optional<User> user = repository.findByEmail(email);
        return user;
    }

    public Optional<User> findUserById(Long id) {
        Optional<User> user = repository.findById(id);
        return user;
    }
}
