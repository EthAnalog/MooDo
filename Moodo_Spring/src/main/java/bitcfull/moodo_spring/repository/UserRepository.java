package bitcfull.moodo_spring.repository;

import bitcfull.moodo_spring.model.MooDoUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface UserRepository extends JpaRepository<MooDoUser, String> {
    // 나중에 커스텀 쿼리 추가 할거잇음하기
    int countById(String id);

    @Modifying
    @Query("UPDATE MooDoUser SET pass=:pass, age=:age WHERE id=:id")
    void update(@Param("id") String id, @Param("pass") String pass, @Param("age") String age);
}