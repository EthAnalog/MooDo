package bitcfull.moodo_spring.controller;

import bitcfull.moodo_spring.model.MooDoUser;
import bitcfull.moodo_spring.model.MoodoCalendar;
import bitcfull.moodo_spring.model.MoodoMode;
import bitcfull.moodo_spring.service.MoodoModeService;
import bitcfull.moodo_spring.service.MoodoTodoService;
import bitcfull.moodo_spring.service.MoodoUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@RestController
@RequestMapping("/api/calendar")
public class MoodoCalendarController {
    @Autowired
    private MoodoModeService moodoModeService;

    @Autowired
    private MoodoTodoService todoService;

    @Autowired
    private MoodoUserService userService;

    @GetMapping("/count/day/{userId}/{date}")
    public MoodoCalendar getDay(@PathVariable String userId, @PathVariable String date) throws ParseException {

        MoodoCalendar today = new MoodoCalendar();

        MooDoUser user = userService.getUserInfo(userId);
        SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy/MM/dd");
        SimpleDateFormat inputFormat2 = new SimpleDateFormat("yyyy-MM-dd");

        // 출력 형식 지정 (MM-dd)
        SimpleDateFormat outputFormat = new SimpleDateFormat("MM-dd");

        Date userDate = inputFormat.parse(user.getAge());
        Date inputDate = inputFormat2.parse(date);
        String userAge = outputFormat.format(userDate);
        String inputDay = outputFormat.format(inputDate);


        int todayTd = todoService.getTodoCountForDay(userId, date);

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

        return today;
    }
}
