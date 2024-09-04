package bitcfull.moodo_spring.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

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
  private int mdMode; // 기분 상태(1 ~ 5)

  @Column(name = "created_date", nullable = false)
  private LocalDate createdDate; //작성일자
}
