package com.example.moodo.todolist

import android.animation.ObjectAnimator
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.moodo.R
import com.example.moodo.databinding.ActivityMainToDoWriteBinding
import com.example.moodo.db.MooDoClient
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class MainActivity_ToDo_Write : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val binding = ActivityMainToDoWriteBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        val userId = intent.getStringExtra("userId")
        val stats = intent.getStringExtra("stats")

        val startDay = binding.startDay
        val startTime = binding.startTime
        val endDay = binding.endDay
        val endTime = binding.endTime
        val edtTodo = binding.edtToDo

        // 체크
        var toDoCheck = false
        var toDoColor = "none"

        // 뒤로가기
        binding.btnClose.setOnClickListener {
            setResult(RESULT_CANCELED, null)
            finish()
        }

        // 일정 색깔 버튼 배열
        val colorButtons = listOf(
            binding.colorRed,
            binding.colorBlue,
            binding.colorOrange,
            binding.colorGreen,
            binding.colorYellow
        )

        colorButtons.forEachIndexed { index, btn ->
            btn.setOnClickListener {
                // 선택된 버튼 크기 크게
                ObjectAnimator.ofFloat(btn, "scaleX", 1.1f).apply {
                    duration = 300
                    start()
                }
                ObjectAnimator.ofFloat(btn, "scaleY", 1.1f).apply {
                    duration = 300
                    start()
                }

                //  나머지 버튼 크기 작게
                colorButtons.forEachIndexed{ i, otherBtn ->
                    if (i != index) {
                        ObjectAnimator.ofFloat(otherBtn, "scaleX", 0.9f).apply {
                            duration = 300
                            start()
                        }
                        ObjectAnimator.ofFloat(otherBtn, "scaleY", 0.9f).apply {
                            duration = 300
                            start()
                        }
                    }
                }
                when(index) {
                    0->toDoColor="red"
                    1->toDoColor="blue"
                    2->toDoColor="orange"
                    3->toDoColor="green"
                    4->toDoColor="yellow"
                    else -> toDoColor="none"
                }
            }
        }

        // 일정 추가 버튼 클릭 시
        if (stats == "insert") {
            // 초기 값 = 넘어온 값으로
            val selectDate = intent.getStringExtra("selectDate")
            Log.d("MooDoLog ToDoWrite", selectDate.toString())

            startDay.text = selectDate
            endDay.text = selectDate
        }
        // 일정 수정 버튼
        else if (stats == "update") {
            val inStartDay = intent.getStringExtra("startDay")
            val inStartTime = intent.getStringExtra("startTime")
            val inEndDay = intent.getStringExtra("endDay")
            val inEndTime = intent.getStringExtra("endTime")
            val tdStr = intent.getStringExtra("tdStr")
            toDoColor = intent.getStringExtra("toDoColor").toString()

            startDay.text = inStartDay
            startTime.text = inStartTime
            endDay.text = inEndDay
            endTime.text = inEndTime
            edtTodo.setText(tdStr)

            when (toDoColor) {
                "red" -> colorButtons[0].performClick()
                "blue" -> colorButtons[1].performClick()
                "orange" -> colorButtons[2].performClick()
                "green" -> colorButtons[3].performClick()
                "yellow" -> colorButtons[4].performClick()
            }
        }

        // startDay Click
        startDay.setOnClickListener {
            var calendar = Calendar.getInstance()
            var year = calendar.get(Calendar.YEAR)
            var month = calendar.get(Calendar.MONTH)
            var day = calendar.get(Calendar.DAY_OF_MONTH)

            // DatePickerDialog 생성 (스피너 모드로 설정)
            val datePickerDialog = DatePickerDialog(
                this,
                android.R.style.Theme_Holo_Light_Dialog,
                DatePickerDialog.OnDateSetListener{ _, selectedYear, selectedMonth, selectedDay ->
                    // 선택한 날짜 text 표시
                    val formattedMonth = String.format("%02d", selectedMonth + 1)
                    val formatttedDay = String.format("%02d", selectedDay)

                    val date = "${selectedYear}-${formattedMonth}-${formatttedDay}"

                    startDay.text = date
                },
                year, month, day
            )
            datePickerDialog.show()
        }
        // startTime Click
        startTime.setOnClickListener {
            val calendar = Calendar.getInstance()
            val hour = calendar.get(Calendar.HOUR_OF_DAY)
            val minute = calendar.get(Calendar.MINUTE)

            // timePicker
            val timePickerDialog = TimePickerDialog(this, android.R.style.Theme_Holo_Light_Dialog,
                TimePickerDialog.OnTimeSetListener { _, selectedHour, selectedMinute ->
                    // 선택한 시간을 두 자리 형식으로 표시
                    val formattedHour = String.format("%02d", selectedHour)
                    val formattedMinute = String.format("%02d", selectedMinute)

                    val time = "$formattedHour:$formattedMinute"
                    startTime.text = time
                },
                hour, minute, true // 24시간 형식으로 표시
            )
            timePickerDialog.show()
        }

        // endDay
        endDay.setOnClickListener {
            var calendar = Calendar.getInstance()
            var year = calendar.get(Calendar.YEAR)
            var month = calendar.get(Calendar.MONTH)
            var day = calendar.get(Calendar.DAY_OF_MONTH)

            // DatePickerDialog 생성 (스피너 모드로 설정)
            val datePickerDialog = DatePickerDialog(
                this,
                android.R.style.Theme_Holo_Light_Dialog,
                DatePickerDialog.OnDateSetListener{ _, selectedYear, selectedMonth, selectedDay ->
                    // 선택한 날짜 text 표시
                    val formattedMonth = String.format("%02d", selectedMonth + 1)
                    val formatttedDay = String.format("%02d", selectedDay)

                    val date = "${selectedYear}-${formattedMonth}-${formatttedDay}"

                    endDay.text = date
                },
                year, month, day
            )
            datePickerDialog.show()
        }
        // endTime
        endTime.setOnClickListener {
            val calendar = Calendar.getInstance()
            val hour = calendar.get(Calendar.HOUR_OF_DAY)
            val minute = calendar.get(Calendar.MINUTE)

            // timePicker
            val timePickerDialog = TimePickerDialog(this, android.R.style.Theme_Holo_Light_Dialog,
                TimePickerDialog.OnTimeSetListener { _, selectedHour, selectedMinute ->
                    // 선택한 시간을 두 자리 형식으로 표시
                    val formattedHour = String.format("%02d", selectedHour)
                    val formattedMinute = String.format("%02d", selectedMinute)

                    val time = "$formattedHour:$formattedMinute"
                    endTime.text = time
                },
                hour, minute, true // 24시간 형식으로 표시
            )
            timePickerDialog.show()
        }

        // 저장 버튼
        binding.btnSave.setOnClickListener {
            // edtTodo 가 비어 있을 경우
            if (edtTodo.text.isEmpty()) {
                AlertDialog.Builder(this)
                    .setMessage("일정을 기입해주세요.")
                    .setPositiveButton("확인", null)
                    .show()

                toDoCheck = false
            }
            else {
                val startStr = "${startDay.text} ${startTime.text}"
                val endStr = "${endDay.text} ${endTime.text}"

                val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())

                val startDate = dateFormat.parse(startStr)
                val endDate = dateFormat.parse(endStr)

                Log.d("MooDoLog Write Start", startDate.toString())
                Log.d("MooDoLog Write Start Str", startStr)

                // 오늘 날짜 구하기
                val today = Calendar.getInstance().apply {
                    set(Calendar.HOUR_OF_DAY, 0)
                    set(Calendar.MINUTE, 0)
                    set(Calendar.SECOND, 0)
                    set(Calendar.MILLISECOND, 0)
                }.time

                // 시작일이 종료일보다 멀 때
                if (endDate.before(startDate)) {
                    AlertDialog.Builder(this)
                        .setMessage("일정 시작일과 종료일을 확인하세요.")
                        .setPositiveButton("확인", null)
                        .show()

                    toDoCheck = false
                }
                // 시작일이 오늘보다 이전일 때
                else if (startDate.before(today) && stats == "insert") {
                    AlertDialog.Builder(this)
                        .setMessage("시작일이 오늘보다 이전입니다. 오늘 이후로 설정하세요.")
                        .setPositiveButton("확인", null)
                        .show()

                    toDoCheck = false
                }
                else {
                    toDoCheck = true
                    Log.d("MooDoLog Write Success", edtTodo.text.toString())
                    Log.d("MooDoLog Write Success", startDate.toString())
                    Log.d("MooDoLog Write Success", toDoColor)
                    if (toDoColor!="none") {
                        // 일정 저장
                        intent.putExtra("startDay",startStr)
                        intent.putExtra("endDay", endStr)
                        intent.putExtra("toDoStr", edtTodo.text.toString())
                        intent.putExtra("toDoColor", toDoColor)

                        setResult(RESULT_OK, intent)
                        finish()
                    }
                    else {
                        AlertDialog.Builder(this)
                            .setMessage("일정을 표기할 색상을 선택해주세요.")
                            .setPositiveButton("확인", null)
                            .show()

                        toDoCheck = false
                    }
                }
            }
        }
    }
}