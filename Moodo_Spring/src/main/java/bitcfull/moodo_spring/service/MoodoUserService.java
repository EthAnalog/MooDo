package bitcfull.moodo_spring.service;

import bitcfull.moodo_spring.model.MooDoUser;
import bitcfull.moodo_spring.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class MoodoUserService {

    @Autowired
    private UserRepository userRepository;

//    회원가입
    public MooDoUser insert(MooDoUser user) {
        return userRepository.save(user);
    }

//    로그인
    public Optional<MooDoUser> findById(String id) {
        return userRepository.findById(id);
    }

    // 회원가입 시 아이디 중복 여부 확인
    public int userIdCheck(String id) {
        int result = userRepository.countById(id);

        return result;
    }

}
