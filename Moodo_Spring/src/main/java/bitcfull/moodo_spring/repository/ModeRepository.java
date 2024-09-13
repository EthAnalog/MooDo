package bitcfull.moodo_spring.repository;

import bitcfull.moodo_spring.model.MooDoUser;
import bitcfull.moodo_spring.model.MoodoMode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public interface ModeRepository extends JpaRepository<MoodoMode, Long> {
    List<MoodoMode> findByUserId(String userId); // 사용자 기분 기록 가져옴

    // 특정 날짜의 사용자 기분 기록
    Optional<MoodoMode> findByUserIdAndCreatedDate(String userId, String createdDate);


    // 당월 1일부터 현재까지 기록된 기분 중 가장 많은 값 조회
    @Query("SELECT md.mdMode, COUNT(md.mdMode) AS mcount " +
            "FROM MoodoMode md " +
            "WHERE md.user.id = :userId " +
            "AND md.createdDate BETWEEN :startDate AND :endDate " +
            "GROUP BY md.mdMode " +
            "ORDER BY mcount DESC")
    List<Object[]> findMoodMax(String userId, String startDate, String endDate);

    // 유저 ID와 날짜 범위로 데이터를 조회하는 쿼리
    // 한 달 기록 조회 용
    @Query("SELECT m FROM MoodoMode m WHERE m.user.id = :userId AND m.createdDate >= :startDate AND m.createdDate <= :endDate")
    List<MoodoMode> findByUserIdAndDateRange(String userId, String startDate, String endDate);

    // 한 달 가장 많은 감정
    @Query("SELECT m.mdMode FROM MoodoMode m WHERE m.user.id = :userId AND m.createdDate >= :startDate AND m.createdDate <= :endDate " +
            "GROUP BY m.mdMode ORDER BY COUNT(m.mdMode) DESC")
    List<Integer> findMostCommonMoodForMonth(String userId, String startDate, String endDate);

    // 유저별 감정기록 삭제
    void deleteByUser(MooDoUser user);

}
