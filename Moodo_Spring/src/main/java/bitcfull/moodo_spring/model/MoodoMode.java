package bitcfull.moodo_spring.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;

@Entity
@Getter @Setter
// 선택한 날짜에 대해 한 유저당 1회만 감정 기록 가능하도록 설정
@Table(name = "moodo_mode", uniqueConstraints = {
        @UniqueConstraint(columnNames = { "user_id", "created_date"})
})
public class MoodoMode {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long idx;

  @ManyToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = "user_id", nullable = false)
  private MooDoUser user;

  @Column(nullable = false)
  private int mdMode; // 기분 상태(1 ~ 5 / 나쁨 ~ 좋음 순)

  @Column(name = "created_date", nullable = false, length = 10)
  private String createdDate; //작성일자

  @Column(nullable = false)
  private int weather; // 날씨 (1 ~ 4 / 1: 맑음, 2:흐림, 3:비, 4:눈)

  @Column(nullable = false, length = 500, name = "md_daily")
  private String mdDaily; // 한줄일기
}
