package bitcfull.moodo_spring.repository;

import bitcfull.moodo_spring.model.MooDoTodo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface TodoRepository extends JpaRepository<MooDoTodo, Long> {

    @Query("SELECT t FROM MooDoTodo t WHERE t.user.id = :userId AND t.startDate <= :endDate AND t.endDate >= :startDate")
    List<MooDoTodo> findByUserIdAndStartDateLessThanEqualAndEndDateGreaterThanEqual(
            @Param("userId") String userId,
            @Param("startDate") String startDate,
            @Param("endDate") String endDate);

    Optional<MooDoTodo> findById(Long id); // 할 일 조회

    @Query("SELECT COUNT(t) FROM MooDoTodo t WHERE t.user.id = :userId AND t.startDate BETWEEN :startDate AND :endDate")
    int countByUserIdAndStartDateBetween(
            @Param("userId") String userId,
            @Param("startDate") String startDate,
            @Param("endDate") String endDate);

    @Query("SELECT COUNT(t) FROM MooDoTodo t WHERE t.user.id = :userId AND t.startDate BETWEEN :startDate AND :endDate AND t.tdCheck = :tdCheck")
    int countByUserIdAndStartDateBetweenAndTdCheck(
            @Param("userId") String userId,
            @Param("startDate") String startDate,
            @Param("endDate") String endDate,
            @Param("tdCheck") String tdCheck);
}
