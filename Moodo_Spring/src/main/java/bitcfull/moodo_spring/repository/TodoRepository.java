package bitcfull.moodo_spring.repository;

import bitcfull.moodo_spring.model.MooDoTodo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface TodoRepository extends JpaRepository<MooDoTodo, Long> {
    List<MooDoTodo> findByUserIdAndDate(String userId, LocalDate date); // 사용자와 사용자의 날짜로 할 일 목록을 가져오는 메서드

    Optional<MooDoTodo> findById(Long id); // 할 일 조회
}
