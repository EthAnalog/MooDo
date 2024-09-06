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
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/todo")
public class MoodoTodoController {

    @Autowired
    private MoodoTodoService todoService;

    @PostMapping("/add")
    public MooDoTodo addTodo(@RequestBody MooDoTodo todo, @RequestParam String userId) throws ParseException {
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

    // 할 일 완료 여부
    @PutMapping("/check/{id}")
    public MooDoTodo updateCheck(@PathVariable Long id, @RequestBody String tdCheck) {
        System.out.println("Received tdCheck value: " + tdCheck); // 로그 추가
        return todoService.updateCheck(id, tdCheck.trim());
    }

    // 검색해서 할 일 조회
    @GetMapping("/{id}")
    public Optional<MooDoTodo> getTodoById(@PathVariable Long id) {
        return todoService.findById(id);
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
}
