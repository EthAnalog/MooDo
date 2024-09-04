package bitcfull.moodo_spring.repository;

import bitcfull.moodo_spring.model.MooDoTodo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface TodoRepository extends JpaRepository<MooDoTodo, Long> {

    // 기존의 findByUserIdAndDate 으로 할 경우, Date를 Todo에서 찾을 수 없다는 에러 발생, startDate로 변경
    List<MooDoTodo> findByUserIdAndStartDate(String userId, LocalDateTime startDate); // 사용자와 사용자의 날짜로 할 일 목록을 가져오는 메서드

    Optional<MooDoTodo> findById(Long id); // 할 일 조회
}
