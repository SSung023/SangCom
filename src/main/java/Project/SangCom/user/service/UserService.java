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
     * 전달받은 객체의 email을 통해 DB에서 사용자의 정보를 꺼냄
     * -> Role, username, nickname.. 등의 정보를 설정
     */
    @Transactional
    public Long registerUser(User receivedUser){
        // FE에서 전달받은 정보 중 email을 통해 DB에 저장한 User 객체를 가져온다.
        Optional<User> byIdUser = repository.findByEmail(receivedUser.getEmail());
        if (byIdUser.isEmpty())
            throw new BusinessException(ExMessage.DATA_ERROR_NOT_FOUND);

        User storedUser = byIdUser.get(); // DB에 저장되어 있던 사용자

        /**
         * nickname이 같은 경우 -> BusinessException 발생
         */
        if (!validateNicknameLength(receivedUser)){
            throw new BusinessException("닉네임의 길이가 최대 길이(10)를 초과했습니다. 닉네임의 길이를 줄여주세요.");
        }
        else if (!validateDuplicateNickname(receivedUser)) {
            throw new BusinessException("닉네임이 중복됩니다. 다른 닉네임으로 설정해주세요.");
        }


        // email, id를 제외한 정보를 설정
        storedUser.setNickname(receivedUser.getNickname());
        storedUser.setUsername(receivedUser.getUsername());

        if (receivedUser.getRole() == Role.STUDENT){
            storedUser.setStudentInfo
                    (StudentInfo.builder()
                            .grade(receivedUser.getStudentInfo().getGrade())
                            .classes(receivedUser.getStudentInfo().getClasses())
                            .number(receivedUser.getStudentInfo().getNumber())
                            .build());
            storedUser.setTeacherInfo(null);
        }
        else if (receivedUser.getRole() == Role.TEACHER){
            storedUser.setStudentInfo(null);
            storedUser.setTeacherInfo
                    (TeacherInfo.builder()
                            .chargeGrade(receivedUser.getTeacherInfo().getChargeGrade())
                            .chargeSubject(receivedUser.getTeacherInfo().getChargeSubject())
                            .build());
        }

        return storedUser.getId();
    }

    private boolean validateNicknameLength(User targetUser){
        return (targetUser.getNickname().length() <= 10);
    }

    private boolean validateDuplicateNickname(User targetUser) {
        Optional<User> byNickname = repository.findByNickname(targetUser.getNickname());
        return byNickname.isEmpty();
    }



    public Optional<User> findUserByEmail(String email){
        Optional<User> user = repository.findByEmail(email);
        return user;
    }

    public Optional<User> findUserById(Long id) {
        Optional<User> user = repository.findById(id);
        return user;
    }

    public Optional<User> findUserByNickname(String nickname){
        return repository.findByNickname(nickname);
    }
}
