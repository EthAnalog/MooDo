package bitcfull.moodo_spring.repository;

import bitcfull.moodo_spring.model.MooDoTodo;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Date;
import java.util.List;
import java.util.Optional;

public interface TodoRepository extends JpaRepository<MooDoTodo, Long> {

    // 특정 날짜가 startDate 와 EndDate 사이에 있는 일정 모두 조회
    List<MooDoTodo> findByUserIdAndStartDateLessThanEqualAndEndDateGreaterThanEqual(String userId, Date startDate, Date endDate) throws Exception;
    Optional<MooDoTodo> findById(Long id); // 할 일 조회

    // 한 달 동안 기록된 모든 계획의 개수를 가져오는 쿼리
    int countByUserIdAndStartDateBetween(String userId, Date startDate, Date endDate);

    // 한 달 동안 완료된(tdCheck가 'Y') 계획의 개수를 가져오는 쿼리
    int countByUserIdAndStartDateBetweenAndTdCheck(String userId, Date startDate, Date endDate, String tdCheck);
}
