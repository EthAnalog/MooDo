package bitcfull.moodo_spring.controller;

import bitcfull.moodo_spring.model.MoodoMode;
import bitcfull.moodo_spring.service.MoodoModeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

@RestController
@RequestMapping("/api/mood")
public class MoodoModeController {

    @Autowired
    private MoodoModeService moodoModeService;

    // 유저 전체 기분 목록과 가장 많은 기분값 조회
    @GetMapping("/list/{userId}")
    public Map<String, Object> userMoodList(@PathVariable String userId) {
        List<MoodoMode> moodList = moodoModeService.findByUserId(userId);
        Integer moodMax = moodoModeService.findMoodMax(userId);

        Map<String, Object> result = new HashMap<>();
        result.put("moodList", moodList);
        result.put("moodMax", moodMax);

        return result;
    }

    // 특정 날짜 일기 조회
    @GetMapping("/list/{userId}/{date}")
    public Optional<MoodoMode> userMoodList(@PathVariable String userId, @PathVariable String date) {
        return moodoModeService.findByUserAndDate(userId, date);
    }

    // 특정 날짜 기분값 조회
    @GetMapping("/list/mdMode/{userId}/{date}")
    public int getMdMode(@PathVariable String userId, @PathVariable String date) {
        Optional<MoodoMode> mood = moodoModeService.findByUserAndDate(userId, date);
        if (mood.isPresent()) {
            int moodNum = mood.get().getMdMode();
            System.out.println(date + " 감정 " + moodNum);
            return moodNum;
        } else {
            // 데이터가 없을 경우 기본값 반환 or 예외
            System.out.println(date + " 데이터 없음");
            return 0; // 0 반환
        }
    }

    // 기분 기록 추가
    @PostMapping("/insert")
    public MoodoMode insertMood(@RequestBody MoodoMode mood) {

        System.out.println(mood.toString());

        // 사용자 정보가 있는지 확인
        if (mood.getUser() == null || mood.getUser().getId() == null) {
            throw new IllegalArgumentException("사용자 정보가 없습니다.");
        }

        return moodoModeService.insert(mood);
    }

    // 기록된 일기가 있는지 boolean return
    @GetMapping("/listCheck/{userId}/{date}")
    public Boolean userMoodListCheck(@PathVariable String userId, @PathVariable String date) {
        Optional<MoodoMode> existMood = moodoModeService.findByUserAndDate(userId, date);
        if (existMood.isPresent()) {
            return false;
        }
        else {
            return true;
        }
    }

    // 기분 기록 수정 ++ 날씨, 일기 추가
    @PutMapping("/update/{id}")
    public MoodoMode update(@PathVariable Long id, @RequestBody MoodoMode mood) {
        Optional<MoodoMode> existMood = moodoModeService.findById(id);
        System.out.println("기분 기록 수정" + mood.toString());
        if (existMood.isPresent()) {
            MoodoMode updatedMood = existMood.get();
            updatedMood.setMdMode(mood.getMdMode());
            updatedMood.setWeather(mood.getWeather());
            updatedMood.setMdDaily(mood.getMdDaily());
            return moodoModeService.update(updatedMood);
        } else {
            throw new RuntimeException("기록을 찾을 수 없습니다.");
        }
    }

    // 기분 기록 삭제
    @DeleteMapping("/delete/{id}")
    public void delete(@PathVariable Long id) {
        moodoModeService.delete(id);
    }

    // 특정 월 동안의 기분 기록 조회
    @GetMapping("/list/month/{userId}/{year}/{month}")
    public List<MoodoMode> getUserMoodListByMonth(@PathVariable String userId, @PathVariable int year, @PathVariable int month) {
        // 해당 월의 첫 날과 마지막 날 계산
        LocalDate startDate = LocalDate.of(year, month, 1);
        LocalDate endDate = startDate.withDayOfMonth(startDate.lengthOfMonth());

        // 날짜 포맷터 설정
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        // 해당 범위의 기분 기록 조회
        List<MoodoMode> moodList = moodoModeService.findByUserAndDateRange(userId, startDate.format(formatter), endDate.format(formatter));

        return moodList;
    }

    // 한 달 동안 기록된 가장 많은 감정
    @GetMapping("/moreMood/{userId}/{year}/{month}")
    public int getUserMoodNumByMonth(@PathVariable String userId, @PathVariable int year, @PathVariable int month) {
        // 해당 월의 첫 날과 마지막 날 계산
        LocalDate startDate = LocalDate.of(year, month, 1);
        LocalDate endDate = startDate.withDayOfMonth(startDate.lengthOfMonth());

        // 날짜 포맷터 설정
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        return moodoModeService.findMostCommonMoodForMonth(userId, startDate.format(formatter), endDate.format(formatter));
    }
}
