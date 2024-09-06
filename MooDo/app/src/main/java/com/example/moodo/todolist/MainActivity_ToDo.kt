package com.example.moodo.todolist

import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.moodo.R
import com.example.moodo.databinding.ActivityMainToDoBinding
import com.example.moodo.db.MooDoClient
import com.example.moodo.db.MooDoToDo
import com.example.moodo.adapter.ToDoAdapter
import retrofit2.Call
import retrofit2.Response

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
        binding.toDoList.adapter = todoAdapter
        binding.toDoList.layoutManager = LinearLayoutManager(this)

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

        // 뒤로 가기 버튼
        binding.btnClose.setOnClickListener {
            setResult(RESULT_CANCELED, null)
            finish()
        }
    }
}