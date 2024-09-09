package bitcfull.moodo_spring.service;

import bitcfull.moodo_spring.model.MooDoTodo;
import bitcfull.moodo_spring.model.MooDoUser;
import bitcfull.moodo_spring.repository.TodoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class MoodoTodoService {

    @Autowired
    private TodoRepository todoRepository;

    @Autowired
    private MoodoUserService userService;

    // 할일 추가
    public MooDoTodo insert(MooDoTodo todo, String userId) throws ParseException {
        if (todo.getTdList() == null || todo.getTdList().isEmpty()) {
            throw new IllegalArgumentException("할 일을 입력해 주세요.");
        }

        MooDoUser user = userService.findById(userId)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));

        Date currentDate = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault());
        String formattedDate = dateFormat.format(currentDate);
        Date date = dateFormat.parse(formattedDate);

        // 디버깅 로그 추가
        System.out.println("할일 추가 시 날짜 및 시간: " + date);
        System.out.println("할일 정보: " + todo.getTdList() + ", " + todo.getStartDate() + ", " + todo.getEndDate() + ", " + todo.getTdCheck());

        todo.setUser(user);
        todo.setCreatedDate(date);

        return todoRepository.save(todo);
    }

    // 하루 단위 일정 개수 조회
    public int getTodoCountForDay(String userId, String date) {
        String startOfDay = date + " 00:00:00";
        String endOfDay = date + " 23:59:59";

        int returnNum = todoRepository.countByUserIdAndStartDateLessThanEqualAndEndDateGreaterThanEqual(userId, startOfDay, endOfDay);

        System.out.println("date :" + returnNum);
        return returnNum;
    }

    // 할 일 업데이트
    public MooDoTodo update(MooDoTodo todo) {
        return todoRepository.save(todo);
    }

    // 할일 완료 체크
    public MooDoTodo updateCheck(Long id) {
        MooDoTodo updateTodo = todoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("에러 발생"));
        updateTodo.setTdCheck("Y");
        return todoRepository.save(updateTodo);
    }

    // 특정 할 일 조회
    public Optional<MooDoTodo> findById(Long id) {
        return todoRepository.findById(id);
    }

    // 특정 사용자가 지정한 날짜에 등록한 할 일 목록 조회
    public List<MooDoTodo> findByUserIdAndStartDate(String userId, String startDate) throws Exception {
        String startOfDay = startDate + " 00:00:00";
        String endOfDay = startDate + " 23:59:59";

        System.out.println("Start of Day: " + startOfDay);
        System.out.println("End of Day: " + endOfDay);

        return todoRepository.findByUserIdAndStartDateLessThanEqualAndEndDateGreaterThanEqual(userId, startOfDay, endOfDay);
    }

    // 할 일 조회 + tdCheck = Y
    public List<MooDoTodo> findByUserIdAndY(String userId, String date) {
        String startOfDay = date + " 00:00:00";
        String endOfDay = date + " 23:59:59";

        System.out.println("Start of Day: " + startOfDay);
        System.out.println("End of Day: " + endOfDay);

        return todoRepository.findByUserIdAndStartDateLessThanEqualAndEndDateGreaterThanEqualAndTdCheck(userId, startOfDay, endOfDay, "Y");
    }

    // 할 일 조회 + tdCheck = N
    public List<MooDoTodo> findByUserIdAndN(String userId, String date) {
        String startOfDay = date + " 00:00:00";
        String endOfDay = date + " 23:59:59";

        System.out.println("Start of Day: " + startOfDay);
        System.out.println("End of Day: " + endOfDay);

        return todoRepository.findByUserIdAndStartDateLessThanEqualAndEndDateGreaterThanEqualAndTdCheck(userId, startOfDay, endOfDay, "N");
    }

    // 할 일 삭제
    public void delete(Long id) {
        todoRepository.deleteById(id);
    }

    // 한 달 동안 기록된 계획 개수
    public int getTodoCountForMonth(String userId, Date month) throws ParseException {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Calendar cal = Calendar.getInstance();

        cal.setTime(dateFormat.parse(dateFormat.format(month) + "-01"));
        String startOfMonth = dateFormat.format(cal.getTime());

        cal.add(Calendar.MONTH, 1);
        cal.set(Calendar.DAY_OF_MONTH, 1);
        cal.add(Calendar.DATE, -1);
        String endOfMonth = dateFormat.format(cal.getTime());

        // 날짜를 문자열로 포맷팅
        String startOfDayDate = startOfMonth + " 00:00:00";
        String endOfDayDate = endOfMonth + " 23:59:59";

        return todoRepository.countByUserIdAndStartDateBetween(userId, startOfDayDate, endOfDayDate);
    }

    // 한 달 동안 완료된 계획(tdCheck가 'Y') 개수
    public int getCompletedTodoCountForMonth(String userId, Date month) throws ParseException {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Calendar cal = Calendar.getInstance();

        cal.setTime(dateFormat.parse(dateFormat.format(month) + "-01"));
        String startOfMonth = dateFormat.format(cal.getTime());

        cal.add(Calendar.MONTH, 1);
        cal.set(Calendar.DAY_OF_MONTH, 1);
        cal.add(Calendar.DATE, -1);
        String endOfMonth = dateFormat.format(cal.getTime());

        // 날짜를 문자열로 포맷팅
        String startOfDayDate = startOfMonth + " 00:00:00";
        String endOfDayDate = endOfMonth + " 23:59:59";

        return todoRepository.countByUserIdAndStartDateBetweenAndTdCheck(userId, startOfDayDate, endOfDayDate, "Y");
    }
}
