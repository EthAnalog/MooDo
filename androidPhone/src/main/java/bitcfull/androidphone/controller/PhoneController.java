package bitcfull.androidphone.controller;


import bitcfull.androidphone.model.Phone;
import bitcfull.androidphone.service.PhoneService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

// @Controller : return 값 String
// @RestController : return 값 String 외에 다양함
@RestController
public class PhoneController {
    @Autowired
    private PhoneService phoneService;

    // 전체보기
    @GetMapping("/list")
    public List<Phone> list() throws Exception {
        return phoneService.list();
    }

    // 추가
    @PostMapping("/insert")
    public Phone insert(@RequestBody Phone phone) throws Exception {
        return phoneService.insert(phone);
    }

    // 수정
    @PutMapping("/update/{id}")
    public Phone update(@PathVariable("id")Long id, @RequestBody Phone phone) throws Exception {
        return phoneService.update(id, phone);
    }

    // 삭제
    @DeleteMapping("/delete/{id}")
    public void delete(@PathVariable("id") Long id) throws Exception {
        phoneService.delete(id);
    }
}
