package bitcfull.androidphone.repository;

import bitcfull.androidphone.model.Phone;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PhoneRepository extends JpaRepository<Phone, Long> {
}