package Project.SangCom.user.repository;

import Project.SangCom.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    // 이메일 주소를 가진 회원이 있는지 찾기: null이 나올 수 있으므로 Optional로 감싼다
    public Optional<User> findByEmail(String email);

    public Optional<User> findByNickname(String nickname);
}
