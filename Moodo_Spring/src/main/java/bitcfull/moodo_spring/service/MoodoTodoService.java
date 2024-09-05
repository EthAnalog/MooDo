package bitcfull.moodo_spring.service;

import bitcfull.moodo_spring.model.MooDoTodo;
import bitcfull.moodo_spring.model.MooDoUser;
import bitcfull.moodo_spring.repository.TodoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
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

    // 특정 할 일 조회
    public Optional<MooDoTodo> findById(Long id) {
        return todoRepository.findById(id);
    }

    // 특정 사용자가 지정한 날짜에 등록한 할 일 목록 조회
    public List<MooDoTodo> findByUserIdAndDate(String userId, LocalDate date) {
        // 하루의 시작 시간
        LocalDateTime startOfDay = date.atStartOfDay();
        // 하루의 끝 시간
        LocalDateTime endOfDay = date.atTime(LocalTime.MAX);

        // 특정 날짜가 일정의 시작일과 종료일 사이에 있는 할 일 목록 조회
        return todoRepository.findByUserIdAndStartDateLessThanEqualAndEndDateGreaterThanEqual(userId, startOfDay, endOfDay);
    }

    // 할 일 삭제
    public void delete(Long id) {
        todoRepository.deleteById(id);
    }
}
