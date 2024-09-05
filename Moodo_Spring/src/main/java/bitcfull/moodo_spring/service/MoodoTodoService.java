package bitcfull.moodo_spring.service;

import bitcfull.moodo_spring.model.MooDoTodo;
import bitcfull.moodo_spring.model.MooDoUser;
import bitcfull.moodo_spring.repository.TodoRepository;
import org.hibernate.annotations.Array;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

@Service
public class MoodoTodoService {

    @Autowired
    private TodoRepository todoRepository;

    @Autowired
    private MoodoUserService userService;

    // 할일 추가
    public MooDoTodo insert(MooDoTodo todo, String userId) {
        if (todo.getTdList() == null || todo.getTdList().isEmpty()) {
            throw new IllegalArgumentException("할 일을 입력해 주세요.");
        }

        // 유저 정보를 가져와서 설정
        MooDoUser user = userService.findById(userId)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));

        todo.setUser(user);  // 유저 정보 설정
        todo.setCreatedDate(LocalDateTime.now());  // 생성일 설정

        return todoRepository.save(todo);
    }

    // 할 일 업데이트
    public MooDoTodo update(MooDoTodo todo) {
        return todoRepository.save(todo);
    }

    //    할일 완료 체크
    public MooDoTodo updateCheck(Long id, String tdCheck) {
        System.out.println("Received tdCheck value in service: " + tdCheck); // 로그 추가
        MooDoTodo updateTodo = todoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("에러 발생"));
        // 상태 체크
        if ("Y".equals(tdCheck) || "N".equals(tdCheck)) {
            updateTodo.setTdCheck(tdCheck);
            return todoRepository.save(updateTodo);
        } else {
            throw new IllegalArgumentException("올바른 상태 값이 아닙니다.");
        }
    }

    // 특정 할 일 조회 (검색 통해서 조회? 나중에 필요없으면 빼기)
    public Optional<MooDoTodo> findById(Long id) {
        return todoRepository.findById(id);
    }

    // 특정 사용자가 지정한 날짜에 등록한 할 일 목록 조회 (달력에서 날짜 터치하고 해당 날짜 리스트 조회)
    public List<MooDoTodo> findByUserIdAndStartDate(String userId, String startDate) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd");
        LocalDateTime localDateTime = LocalDateTime.parse(startDate, formatter);
        return todoRepository.findByUserIdAndStartDate(userId, localDateTime);
    }


    // 할 일 삭제
    public void delete(Long id) {
        todoRepository.deleteById(id);
    }


}
