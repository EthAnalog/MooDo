package bitcfull.moodo_spring.service;

import bitcfull.moodo_spring.model.MooDoTodo;
import bitcfull.moodo_spring.repository.TodoRepository;
import org.hibernate.annotations.Array;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class MoodoTodoService {

    @Autowired
    private TodoRepository todoRepository;

    // 할일 추가
    public MooDoTodo insert(MooDoTodo todo) {
        return todoRepository.save(todo);
    }

    public MooDoTodo updateCheck(Long id, String tdCheck) {
        MooDoTodo updateTodo = todoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("에러 발생"));

        // 완료 상태 Y랑 N만
        if (tdCheck.equals("Y") || tdCheck.equals("N")) {
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
    public List<MooDoTodo> findByUserIdAndDate(String userId, LocalDate date) {
        return todoRepository.findByUserIdAndDate(userId, date);
    }


    // 할 일 삭제
    public void delete(Long id) {
        todoRepository.deleteById(id);
    }


}
