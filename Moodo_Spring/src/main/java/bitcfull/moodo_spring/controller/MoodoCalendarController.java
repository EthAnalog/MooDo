package bitcfull.moodo_spring.controller;

import bitcfull.moodo_spring.model.MoodoHoliday;
import bitcfull.moodo_spring.model.MooDoUser;
import bitcfull.moodo_spring.model.MoodoCalendar;
import bitcfull.moodo_spring.model.MoodoMode;
import bitcfull.moodo_spring.service.MoodoAPIService;
import bitcfull.moodo_spring.service.MoodoModeService;
import bitcfull.moodo_spring.service.MoodoTodoService;
import bitcfull.moodo_spring.service.MoodoUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/calendar")
public class MoodoCalendarController {
    @Autowired
    private MoodoModeService moodoModeService;

    @Autowired
    private MoodoTodoService todoService;

    @Autowired
    private MoodoUserService userService;

    @Autowired
    private MoodoAPIService apiService;

    @Value("${Moodo_Spring.service.key}")
    private String apiKey;

    @Value("${Moodo_Spring.service.url}")
    private String apiUrl;

    @GetMapping("/count/day/{userId}/{date}")
    public MoodoCalendar getDay(@PathVariable String userId, @PathVariable String date) throws Exception {

        MoodoCalendar today = new MoodoCalendar();

        MooDoUser user = userService.getUserInfo(userId);
        SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy/MM/dd");

        SimpleDateFormat inputFormat2 = new SimpleDateFormat("yyyy-MM-dd");
        Date inputDate = inputFormat2.parse(date);

        // 출력 형식 지정 (MM-dd)
        SimpleDateFormat outputFormat = new SimpleDateFormat("MM-dd");

        SimpleDateFormat outputYear = new SimpleDateFormat("yyyy");
        SimpleDateFormat outputMonth = new SimpleDateFormat("MM");

        String opt1 = "?serviceKey=";
        String opt2 = "&solYear=";
        String opt3 = "&solMonth=";

        String year = outputYear.format(inputDate);
        String month = outputMonth.format(inputDate);

        String url = apiUrl + opt1 + apiKey + opt2 + year + opt3 + month;

        List<MoodoHoliday> holidayList = apiService.getItemList(url);

        String holiDay = "";
        String isHoliday = "N";

        if (holidayList != null) {
            for (MoodoHoliday holiday : holidayList) {
                if (date.equals(holiday.getLocdate())) {
                    holiDay = holiday.getLocdate();
                    isHoliday = holiday.getIsHoliday();
                }
                System.out.println("\n" + date + ":" + isHoliday + "\n");
            }
        }

        Date userDate = inputFormat.parse(user.getAge());
        String userAge = outputFormat.format(userDate);
        String inputDay = outputFormat.format(inputDate);

        int todayTd = todoService.getTodoCountForDay(userId, date);
        if (holiDay.equals(date)) {
            todayTd = 1;
        }

        Optional<MoodoMode> mood = moodoModeService.findByUserAndDate(userId, date);
        String todayMd;

        if (userAge.equals(inputDay)) {
            if (mood.isPresent()) {
                todayMd = "b_" + mood.get().getMdMode();

            } else {
                todayMd = "b_" + "0";
            }
        }
        else {
            if (mood.isPresent()) {
                todayMd = String.valueOf(mood.get().getMdMode());

            } else {
                todayMd = "0";
            }
        }
        today.setTodayTd(todayTd);
        today.setTodayMd(todayMd);
        today.setIsHoliday(isHoliday);

        return today;
    }

    // 공휴일 정보
    @GetMapping("/getHoliday/{date}")
    public List<MoodoHoliday> getHolidays(@PathVariable String date) throws Exception {
        SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date inputDate = inputFormat.parse(date);

        SimpleDateFormat outputYear = new SimpleDateFormat("yyyy");
        SimpleDateFormat outputMonth = new SimpleDateFormat("MM");

        String year = outputYear.format(inputDate);
        String month = outputMonth.format(inputDate);

        String opt1 = "?serviceKey=";
        String opt2 = "&solYear=";
        String opt3 = "&solMonth=";

        String url = apiUrl + opt1 + apiKey + opt2 + year + opt3 + month;

        List<MoodoHoliday> holidayList = apiService.getItemList(url);
        List<MoodoHoliday> holiday = new ArrayList<>();

        for (MoodoHoliday item : holidayList) {
            if (date.equals(item.getLocdate())) {
                holiday.add(item);
            }
        }
        return holiday;
    }
}
