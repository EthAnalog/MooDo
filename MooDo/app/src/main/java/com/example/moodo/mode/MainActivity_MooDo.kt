package com.example.moodo.mode

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.PagerSnapHelper
import com.example.moodo.MainActivity_Statis
import com.example.moodo.R
import com.example.moodo.calendar.MonthAdapter
import com.example.moodo.databinding.ActivityMainMooDoBinding
import com.example.moodo.db.MooDoClient
import com.example.moodo.db.MooDoToDo
import com.example.moodo.adapter.ToDoAdapter
import com.example.moodo.todolist.MainActivity_ToDo
import retrofit2.Call
import retrofit2.Response
import java.lang.Exception
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MainActivity_MooDo : AppCompatActivity() {
    lateinit var binding:ActivityMainMooDoBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainMooDoBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // 사용자 id
        val userId = intent.getStringExtra("id").toString()

        // 선택한 날짜 저장할 TextView 변수
        val saveDate = binding.saveDate

        // tdAdapter
        val todoAdapter = ToDoAdapter()
        binding.todoListLayout.adapter = todoAdapter
        binding.todoListLayout.layoutManager = LinearLayoutManager(this)

        // custom calendar 연결
        val monthListManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        val monthAdapter = MonthAdapter(userId).apply {
            // 날짜 선택
            onDaySelectedListener = object :MonthAdapter.OnDaySelectedListener{
                override fun onDaySelected(date: String) {
                    Log.d("MooDoLog Id", userId)
                    Log.d("MooDoLog day", date)

                    MooDoClient.retrofit.getTodoList(userId, date).enqueue(object :retrofit2.Callback<List<MooDoToDo>>{
                        override fun onResponse(
                            call: Call<List<MooDoToDo>>,
                            response: Response<List<MooDoToDo>>
                        ) {
                            if (response.isSuccessful) {
                                val todoList = response.body() ?: mutableListOf()

                                todoAdapter.todoList.clear()
                                todoAdapter.todoList.addAll(todoList)
                                todoAdapter.notifyDataSetChanged()
                            }else {
                                Log.d("MooDoLog", "Response is not successful: ${response.code()}")
                            }
                        }
                        override fun onFailure(call: Call<List<MooDoToDo>>, t: Throwable) {
                            Log.d("MooDoLog getTodo Fail", t.toString())
                        }
                    })
                    saveDate.text = date


                    val inputFormat = SimpleDateFormat("yyyy-MM-dd")
                    val parsedDate = inputFormat.parse(saveDate.text.toString())

                    val outputFormat = SimpleDateFormat("yyyy년 MM월 dd일")
                    val formattedDate = outputFormat.format(parsedDate)

                    binding.selectTxt.text = formattedDate
                }
            }
        }
        // custom calendar 연결
        binding.calendarCustom.apply {
            layoutManager = monthListManager
            adapter = monthAdapter
            scrollToPosition(Int.MAX_VALUE / 2)
        }

        val snap = PagerSnapHelper()
        snap.attachToRecyclerView(binding.calendarCustom)

        // tdList 수정, 저장, 삭제, 완료 후 tdList update
        val activityToDoListUpdate = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                val update = result.data?.getBooleanExtra("update", false) ?: false
                if (update) {
                    val date = saveDate.text.toString()
                    refreshTodoList(date)
                    monthAdapter.notifyDataSetChanged()
                }
            }
        }

        // to do list 작성 및 수정, 삭제
        binding.userMooDo.setOnClickListener {
            val intent = Intent(this, MainActivity_ToDo::class.java)
            val selectDate = saveDate.text.toString()

            intent.putExtra("userId", userId)
            intent.putExtra("selectDate", selectDate)

            // startActivity(intent)
            activityToDoListUpdate.launch(intent)
        }

        // 감정 작성 후 mode update
        val activityMoodListUpdate = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {result ->
            if (result.resultCode == RESULT_OK) {
                val update = result.data?.getBooleanExtra("update", false) ?: false
                if (update) {
                    monthAdapter.notifyDataSetChanged()
                }
            }
        }
        // mode 작성
        binding.moodWriteBtn.setOnClickListener {
            val intent = Intent(this, MainActivity_ModeWrite::class.java)
            val selectDate = saveDate.text.toString()

            val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

            try {
                val userSelected = dateFormat.parse(selectDate)!!
                val today = Date()

                if (userSelected.after(today)) {
                    // 오늘보다 미래인 경우
                    AlertDialog.Builder(binding.root.context)
                        .setMessage("선택한 날짜가 오늘보다 이후입니다. 오늘까지의 일기만 작성할 수 있어요.")
                        .setPositiveButton("확인", null)
                        .show()
                }
                else {
                    MooDoClient.retrofit.userMoodListCheck(userId, selectDate).enqueue(object:retrofit2.Callback<Boolean> {
                        override fun onResponse(call: Call<Boolean>, response: Response<Boolean>) {
                            if (response.isSuccessful) {
                                if (response.body() == true) {
                                    intent.putExtra("userId", userId)
                                    intent.putExtra("selectDate", selectDate)

                                    // startActivity(intent)
                                    activityMoodListUpdate.launch(intent)
                                }
                                else {
                                    AlertDialog.Builder(binding.root.context)
                                        .setMessage("이미 작성된 일기입니다.")
                                        .setPositiveButton("확인", null)
                                        .show()
                                }
                            }
                        }

                        override fun onFailure(call: Call<Boolean>, t: Throwable) {
                            Log.d("MooDoLog modeF", t.toString())
                        }

                    })
                }
            }
            catch(e:Exception) {
                e.printStackTrace()
                Log.d("MooDoLog ModeMove Error", e.toString())
            }
        }

        // 한달 통계
        binding.statisBtn.setOnClickListener {
            val intent = Intent(this@MainActivity_MooDo, MainActivity_Statis::class.java)

            val selectDate = saveDate.text.toString()

            intent.putExtra("userId", userId)
            intent.putExtra("selectDate", selectDate)

            startActivity(intent)
        }
    }

    override fun onResume() {
        super.onResume()
        val date = binding.saveDate.text.toString()
        refreshTodoList(date)
    }

    private fun refreshTodoList(date:String){
        val userId = intent.getStringExtra("id").toString()

        MooDoClient.retrofit.getTodoList(userId, date).enqueue(object : retrofit2.Callback<List<MooDoToDo>> {
            override fun onResponse(call: Call<List<MooDoToDo>>, response: Response<List<MooDoToDo>>) {
                if (response.isSuccessful) {
                    val todoList = response.body() ?: mutableListOf()
                    val todoAdapter = binding.todoListLayout.adapter as ToDoAdapter
                    todoAdapter.todoList.clear()
                    todoAdapter.todoList.addAll(todoList)
                    todoAdapter.notifyDataSetChanged()
                } else {
                    Log.d("MooDoLog", "Response is not successful: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<List<MooDoToDo>>, t: Throwable) {
                Log.d("MooDoLog getTodo Fail", t.toString())
            }
        })
    }
}