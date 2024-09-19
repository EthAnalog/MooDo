package bitcfull.moodo_spring.repository;

import bitcfull.moodo_spring.model.MooDoTodo;
import bitcfull.moodo_spring.model.MooDoUser;
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

    // 선택한 날짜 할일 조회 및 td_check 확인
    @Query("SELECT t FROM MooDoTodo t WHERE t.user.id = :userId AND t.startDate <= :endDate AND t.endDate >= :startDate AND t.tdCheck = :check")
    List<MooDoTodo> findByUserIdAndStartDateLessThanEqualAndEndDateGreaterThanEqualAndTdCheck(
            @Param("userId") String userId,
            @Param("startDate") String startOfDay,
            @Param("endDate") String endOfDay,
            @Param("check") String check);

    // 캘린더 표시, 하루 단위 to do list 개수 반환
    @Query("SELECT COUNT(t) FROM MooDoTodo t WHERE t.tdCheck = 'N' AND t.user.id = :userId AND t.startDate <= :endDate AND t.endDate >= :startDate")
    int countByUserIdAndStartDateLessThanEqualAndEndDateGreaterThanEqual(
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

    @Query("SELECT t FROM MooDoTodo t WHERE t.user.id = :userId AND t.tdList LIKE %:keyword%")
    List<MooDoTodo> searchTodosByKeyword(@Param("userId") String userId, @Param("keyword") String keyword);

    // 탈퇴 시 해당 유저 모든 기록 삭제
    void deleteByUser(MooDoUser user);
}
