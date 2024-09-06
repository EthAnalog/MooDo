package bitcfull.moodo_spring.repository;

import bitcfull.moodo_spring.model.MooDoTodo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Optional;

public interface TodoRepository extends JpaRepository<MooDoTodo, Long> {

    // 특정 날짜가 startDate 와 EndDate 사이에 있는 일정 모두 조회
    List<MooDoTodo> findByUserIdAndStartDateLessThanEqualAndEndDateGreaterThanEqual(String userId, Date startDate, Date endDate) throws Exception;
    Optional<MooDoTodo> findById(Long id); // 할 일 조회
}
