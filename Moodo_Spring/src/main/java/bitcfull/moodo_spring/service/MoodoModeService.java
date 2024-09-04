package bitcfull.moodo_spring.service;

import bitcfull.moodo_spring.MoodoSpringApplication;
import bitcfull.moodo_spring.model.MoodoMode;
import bitcfull.moodo_spring.repository.ModeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class MoodoModeService {

    @Autowired
    private ModeRepository modeRepository;
    @Autowired
    private MoodoSpringApplication moodoSpringApplication;

    // 기분 기록 추가
    public MoodoMode insert(MoodoMode mood) {
        return modeRepository.save(mood);
    }

    // 기분 기록 삭제
    public void delete(Long id) {
        modeRepository.deleteById(id);
    }

    // 특정 기분 기록 조회
    public Optional<MoodoMode> findById(Long id) {
        return modeRepository.findById(id);
    }
}
