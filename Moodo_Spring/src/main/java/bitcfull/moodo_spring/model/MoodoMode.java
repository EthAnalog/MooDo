package bitcfull.moodo_spring.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter @Setter
@Table(name = "moodo_mode")
public class MoodoMode {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idx;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private MooDoUser user;

    @Column(nullable = false)
    private int mdMode; // 기분 상태(1 ~ 5)

    @Column(nullable = false)
    private LocalDateTime createdDate; //작성일자
}
