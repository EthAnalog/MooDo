package bitcfull.moodo_spring.repository;

import bitcfull.moodo_spring.model.MooDoTodo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface TodoRepository extends JpaRepository<MooDoTodo, Long> {
    // 특정 날짜가 startDate와 endDate 범위에 속하는 할 일 목록 조회
    List<MooDoTodo> findByUserIdAndStartDateLessThanEqualAndEndDateGreaterThanEqual(String userId, LocalDateTime startDate, LocalDateTime endDate);
}
