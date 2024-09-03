package bitcfull.moodo_spring.repository;

import bitcfull.moodo_spring.model.MoodoMode;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ModeRepository extends JpaRepository<MoodoMode, Long> {
    List<MoodoMode> findByUserId(String userid); // 사용자 기분 기록 가져옴
}
