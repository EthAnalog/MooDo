package bitcfull.moodo_spring.repository;

import bitcfull.moodo_spring.model.MoodoMode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public interface ModeRepository extends JpaRepository<MoodoMode, Long> {
    List<MoodoMode> findByUserId(String userId); // 사용자 기분 기록 가져옴

    // 특정 날짜의 사용자 기분 기록
    Optional<MoodoMode> findByUserIdAndCreatedDate(String userId, LocalDate createdDate);


    // 당월 1일부터 현재까지 기록된 기분 중 가장 많은 값 조회
    @Query("SELECT md.mdMode, COUNT(md.mdMode) AS mcount " +
            "FROM MoodoMode md " +
            "WHERE md.user.id = :userId " +
            "AND md.createdDate BETWEEN :startDate AND :endDate " +
            "GROUP BY md.mdMode " +
            "ORDER BY mcount DESC")
    List<Object[]> findMoodMax(String userId, LocalDate startDate, LocalDate endDate);


}



