package bitcfull.moodo_spring.controller;

import bitcfull.moodo_spring.model.MooDoTodo;
import bitcfull.moodo_spring.service.MoodoTodoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/todo")
public class MoodoTodoController {

    @Autowired
    private MoodoTodoService todoService;

    @PostMapping("/add/{userId}")
    public MooDoTodo addTodo(@RequestBody MooDoTodo todo, @PathVariable String userId) throws ParseException {
        System.out.println("to do list 저장하기 " + userId);
        // 생성 시간은 자동으로 서버에서 저장
        return todoService.insert(todo, userId);
    }

    // 할 일 조회 (선택한 날짜 할 일 목록)
    @GetMapping("/list/{userId}/{date}")
    public List<MooDoTodo> getTodoList(@PathVariable String userId,
                                       @PathVariable String date) throws Exception {
        System.out.println("전달받은 날짜: " + date);
        return todoService.findByUserIdAndStartDate(userId, date);
    }

    // 할 일 조회 (선택한 날짜 + check = Y)
    @GetMapping("/listY/{userId}/{date}")
    public List<MooDoTodo> getTodoListY(@PathVariable String userId, @PathVariable String date) throws Exception {
        System.out.println("전달받은 날짜: " + date);
        return todoService.findByUserIdAndY(userId, date);
    }

    // 할 일 조회 (선택한 날짜 + check = N)
    @GetMapping("/listN/{userId}/{date}")
    public List<MooDoTodo> getTodoListN(@PathVariable String userId, @PathVariable String date) throws Exception {
        System.out.println("전달받은 날짜: " + date);
        return todoService.findByUserIdAndN(userId, date);
    }

    // 할 일 완료
    @PutMapping("/check/{id}")
    public MooDoTodo updateCheck(@PathVariable Long id) {
        System.out.println("Received tdCheck value: " + id); // 로그 추가
        return todoService.updateCheck(id);
    }

    // 검색해서 할 일 조회
    @GetMapping("/search/{userId}")
    public List<MooDoTodo> searchTodos(@PathVariable String userId, @RequestParam String keyword){
        System.out.println("검색어 : " + keyword);
        return todoService.searchTodos(userId, keyword);
    }

    // 할 일 수정
    @PutMapping("/update/{id}")
    public MooDoTodo updateTodo(@PathVariable Long id, @RequestBody MooDoTodo todo) {
        Optional<MooDoTodo> existingTodo = todoService.findById(id);
        if (existingTodo.isPresent()) {
            MooDoTodo updatedTodo = existingTodo.get();
            updatedTodo.setTdList(todo.getTdList());
            updatedTodo.setStartDate(todo.getStartDate());
            updatedTodo.setEndDate(todo.getEndDate());
            return todoService.update(updatedTodo);  // 업데이트 후 저장
        } else {
            throw new RuntimeException("할 일을 찾을 수 없습니다.");
        }
    }

    // 할 일 삭제
    @DeleteMapping("/delete/{id}")
    public void deleteTodo(@PathVariable Long id) {
        todoService.delete(id);
    }

    // 한 달 동안 기록된 계획 개수
    @GetMapping("/count/{userId}/{year}/{month}")
    public int getTodoCountForMonth(@PathVariable String userId, @PathVariable int year, @PathVariable int month) throws ParseException {
        System.out.println("한달 동안 기록된 계획");

        // 해당 월의 첫 날과 마지막 날 계산
        LocalDate startDate = LocalDate.of(year, month, 1);
        LocalDate endDate = startDate.withDayOfMonth(startDate.lengthOfMonth());

        // 날짜 포맷터
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        System.out.println(startDate.format(formatter));

        return todoService.getTodoCountForMonth(userId, startDate.format(formatter), endDate.format(formatter));
    }

    // 한 달 동안 완료된 계획 개수 (tdCheck가 'Y')
    @GetMapping("/completed/count/{userId}/{year}/{month}")
    public int getCompletedTodoCountForMonth(@PathVariable String userId, @PathVariable int year, @PathVariable int month) throws ParseException {
        System.out.println("한달 동안 완료된 계획");

        // 해당 월의 첫 날과 마지막 날 계산
        LocalDate startDate = LocalDate.of(year, month, 1);
        LocalDate endDate = startDate.withDayOfMonth(startDate.lengthOfMonth());

        // 날짜 포맷터
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        System.out.println(startDate.format(formatter));

        return todoService.getCompletedTodoCountForMonth(userId, startDate.format(formatter), endDate.format(formatter));
    }

    // 선택된 날짜의 일정 개수 조회
    @GetMapping("/count/day/{userId}/{date}")
    public int getTodoCountForDay(@PathVariable String userId, @PathVariable String date) {
        return todoService.getTodoCountForDay(userId, date);
    }
}
