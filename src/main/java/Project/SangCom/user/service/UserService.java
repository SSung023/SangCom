package Project.SangCom.user.service;

import Project.SangCom.user.domain.Role;
import Project.SangCom.user.domain.User;
import Project.SangCom.user.domain.embedded.StudentInfo;
import Project.SangCom.user.domain.embedded.TeacherInfo;
import Project.SangCom.user.repository.UserRepository;
import Project.SangCom.util.exception.BusinessException;
import Project.SangCom.util.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Slf4j
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
            throw new BusinessException(ErrorCode.DATA_ERROR_NOT_FOUND);

        User storedUser = byIdUser.get(); // DB에 저장되어 있던 사용자

        /**
         * nickname이 같은 경우 -> BusinessException 발생
         */
        if (!validateNicknameLength(receivedUser)){
            throw new BusinessException(ErrorCode.NICKNAME_LENGTH_EXCEED);
        }
        else if (!validateDuplicateNickname(receivedUser)) {
            throw new BusinessException(ErrorCode.NICKNAME_DUPLICATED);
        }


        // email, id를 제외한 정보(username, nickname, studentInfo(teacherInfo)를 재설정
        storedUser.updateUser(receivedUser.getRole(), receivedUser.getUsername(), receivedUser.getNickname(),
                receivedUser.getStudentInfo(), receivedUser.getTeacherInfo());

        log.info(storedUser.toString());

        return storedUser.getId();
    }

    private boolean validateNicknameLength(User targetUser){
        return (targetUser.getNickname().length() <= 10);
    }

    private boolean validateDuplicateNickname(User targetUser) {
        Optional<User> byNickname = repository.findByNickname(targetUser.getNickname());
        return byNickname.isEmpty();
    }



    public User findUserByEmail(String email){
        return repository.findByEmail(email)
                .orElseThrow(() -> new BusinessException(ErrorCode.SAVED_MEMBER_NOT_FOUND));
    }

    public User findUserById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.DATA_ERROR_NOT_FOUND));
    }

    public User findUserByNickname(String nickname){
        return repository.findByNickname(nickname)
                .orElseThrow(() -> new BusinessException(ErrorCode.DATA_ERROR_NOT_FOUND));
    }
}
