package bitcfull.moodo_spring.repository;

import bitcfull.moodo_spring.model.MooDoUser;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<MooDoUser, String> {
    // 나중에 커스텀 쿼리 추가 할거잇음하기
  int countById(String id);
}
