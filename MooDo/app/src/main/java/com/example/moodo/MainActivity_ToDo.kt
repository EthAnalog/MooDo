package com.example.moodo

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.moodo.databinding.ActivityMainToDoBinding
import com.example.moodo.db.MooDoClient
import com.example.moodo.db.MooDoMode
import com.example.moodo.db.MooDoToDo
import com.example.moodo.recycler.ToDoAdapter
import retrofit2.Call
import retrofit2.Response
import java.text.SimpleDateFormat
import java.time.format.DateTimeFormatter
import java.util.Calendar
import java.util.Locale
import java.util.Optional

class MainActivity_ToDo : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val binding = ActivityMainToDoBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val userId = intent.getStringExtra("userId")
        val selectDate = intent.getStringExtra("selectDate")

        Log.d("MooDoLog ToDo", userId.toString())
        Log.d("MooDoLog ToDo", selectDate.toString())

        // tdList Adapter
        val todoAdapter = ToDoAdapter()
        binding.toDorecycler.adapter = todoAdapter
        binding.toDorecycler.layoutManager = LinearLayoutManager(this)

        // 선택한 날짜 tdList 불러오기
        MooDoClient.retrofit.getTodoList(userId.toString(), selectDate.toString()).enqueue(object :retrofit2.Callback<List<MooDoToDo>>{
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
        
        
        // 기록된 감정 있는지 조회
        // 아직 안됨
//        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
//        val select = dateFormat.parse(selectDate.toString())
//        MooDoClient.retrofit.userMoodList(userId.toString(), select!!).enqueue(object :
//            retrofit2.Callback<Optional<MooDoMode>> {
//            override fun onResponse(
//                call: Call<Optional<MooDoMode>>,
//                response: Response<Optional<MooDoMode>>
//            ) {
//                if (response.isSuccessful) {
//                    Log.d("MooDoLog Mood body", response.body().toString())
//                    Log.d("MooDoLog Mood call", call.toString())
//                } else {
//                    Log.d("MooDoLog Mood", "Response is not successful: ${response.code()}")
//                }
//            }
//
//            override fun onFailure(call: Call<Optional<MooDoMode>>, t: Throwable) {
//                Log.d("MooDoLog userMoodList Fail", t.toString())
//            }
//
//        })
    }
}