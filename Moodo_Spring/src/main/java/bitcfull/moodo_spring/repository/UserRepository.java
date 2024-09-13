package bitcfull.moodo_spring.repository;

import bitcfull.moodo_spring.model.MooDoUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface UserRepository extends JpaRepository<MooDoUser, String> {
    // 나중에 커스텀 쿼리 추가 할거잇음하기
    int countById(String id);

    @Query("UPDATE MooDoUser SET pass=:pass, age=:age WHERE id=:id")
    MooDoUser update(String id, String pass, String age);
}
