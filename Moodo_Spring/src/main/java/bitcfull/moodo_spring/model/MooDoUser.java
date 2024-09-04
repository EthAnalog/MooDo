package bitcfull.moodo_spring.model;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter @Setter
@Entity
@Table(name = "moodo_user")
public class MooDoUser {
    @Id
    @Column(nullable = false, unique = true, length = 45)
    private String id;

    @Column(nullable = false, length = 45)
    private String name;

    @Column(nullable = false, length = 45)
    private String pass;

    @Column(nullable = false, length = 10)
    private String age;
}
