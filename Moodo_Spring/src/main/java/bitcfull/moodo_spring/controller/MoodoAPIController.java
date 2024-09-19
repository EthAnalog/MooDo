package bitcfull.moodo_spring.controller;

import bitcfull.moodo_spring.dto.Holiday;
import bitcfull.moodo_spring.service.MoodoAPIService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Controller
@RequestMapping("/api/holiday")
public class MoodoAPIController {
//    https://apis.data.go.kr/B090041/openapi/service/SpcdeInfoService/getRestDeInfo?serviceKey=YHk%2FDq2AvdrTidj5jSCst2wFU1dgbcYNzbVQBDpf70d%2FNUvG1nz7R9Jq61UI3Byugm6Zc9NDBTa5IhbUhqJTnA%3D%3D&solYear=2015&solMonth=09
    @Autowired
    MoodoAPIService apiService;

    @Value("${Moodo_Spring.service.key}")
    private String apiKey;

    @Value("${Moodo_Spring.service.url}")
    private String apiUrl;

    // 공휴일 정보
    @GetMapping("/getHoliday/{date}")
    public List<Holiday> getHolidays(@PathVariable String date) throws Exception {
//        https://apis.data.go.kr/B090041/openapi/service/SpcdeInfoService/getRestDeInfo?serviceKey=YHk%2FDq2AvdrTidj5jSCst2wFU1dgbcYNzbVQBDpf70d%2FNUvG1nz7R9Jq61UI3Byugm6Zc9NDBTa5IhbUhqJTnA%3D%3D&solYear=2024&solMonth=09

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

        List<Holiday> holidayList = apiService.getItemList(url);
        List<Holiday> holidayItem = new ArrayList<>();

        if (holidayList != null && holidayList.size() > 0) {
            for (Holiday holiday : holidayList) {
                if (holiday.getLocdate().equals(date)) {
                    Holiday Item = new Holiday();
                    Item.setLocdate(holiday.getLocdate());
                    Item.setIsHoliday(holiday.getIsHoliday());
                    Item.setDateName(holiday.getDateName());

                    System.out.println("\n" + Item.getDateName());
                    holidayItem.add(Item);
                }
            }
        }
        System.out.println(holidayItem);
        return holidayItem;
    }
}
