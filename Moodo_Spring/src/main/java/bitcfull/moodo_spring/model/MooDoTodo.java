package bitcfull.moodo_spring.model;


import bitcfull.moodo_spring.MoodoSpringApplication;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter @Setter
@Entity
@Table(name = "moodo_todo")
public class MooDoTodo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idx; // 글번호 (고유식별자)

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private MooDoUser user; // 조인

    @Column(nullable = false, length = 400)
    private String tdlist; // 할일 목록

    @Column(nullable = false)
    private LocalDateTime startDate; // 일정 시작일

    @Column(nullable = false)
    private LocalDateTime endDate; // 일정 마지막일

    @Column(nullable = false, length = 1) // 할일 완료 여부
    private Boolean tdCheck;

    @Column(nullable = false)
    private LocalDateTime createdDate; // 작성일
}
