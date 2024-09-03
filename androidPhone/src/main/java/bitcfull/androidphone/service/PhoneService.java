package bitcfull.androidphone.service;

import bitcfull.androidphone.model.Phone;
import bitcfull.androidphone.repository.PhoneRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.ArrayList;
import java.util.List;

@Service
public class PhoneService {
    @Autowired
    private PhoneRepository phoneRepository;

    // 전체보기
    public List<Phone> list() throws Exception {
        return phoneRepository.findAll();
    }

    // 추가
    public Phone insert(Phone phone) throws Exception{
        return phoneRepository.save(phone);
    }

    // 수정
    public Phone update(Long id, Phone phone) throws Exception{
//        1.영속성 컨텍스트에 있는 id로 phone 객체 구하기
        Phone updatePhone = phoneRepository.findById(id).orElseThrow(() -> new RuntimeException("데이터가 없습니다."));
//        2.그 객체를 수정하기 <-- 더티체킹
        updatePhone.setName(phone.getName());
        updatePhone.setPhone(phone.getPhone());

        return phoneRepository.save(updatePhone);
    }

    // 삭제
    public void delete(Long id) throws Exception{
        phoneRepository.deleteById(id);
    }
}
