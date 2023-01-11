package Project.SangCom.user.service;

import Project.SangCom.user.domain.User;
import Project.SangCom.user.repository.UserRepository;
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

    public Optional<User> findUserByEmail(String email){
        Optional<User> user = repository.findByEmail(email);
        return user;
    }
}
