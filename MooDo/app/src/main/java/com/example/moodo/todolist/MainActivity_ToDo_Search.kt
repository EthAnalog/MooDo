package com.example.moodo.todolist

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.moodo.R
import com.example.moodo.adapter.CalendarToDoAdapter
import com.example.moodo.adapter.ToDoAdapter
import com.example.moodo.databinding.ActivityMainToDoSearchBinding
import com.example.moodo.db.MooDoClient
import com.example.moodo.db.MooDoToDo
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.Locale

class MainActivity_ToDo_Search : AppCompatActivity() {
    lateinit var binding:ActivityMainToDoSearchBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainToDoSearchBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val userId = intent.getStringExtra("userId").toString()

        val searchTxt = binding.searchTxt

        // adapter
        val toDoAdapter = ToDoAdapter()
        binding.searchRecycler.adapter = toDoAdapter
        binding.searchRecycler.layoutManager = LinearLayoutManager(this)

        // 뒤로 가기
        binding.btnClose.setOnClickListener {
            setResult(RESULT_CANCELED, null)
            finish()
        }

        binding.searchBtn.setOnClickListener {
            if (searchTxt.text?.isEmpty() == true){
                searchTxt.hint = "검색어를 입력해주세요."
            }
            else {
                val keyword = searchTxt.text.toString()
                Log.d("MooDoLog Search", keyword)
                MooDoClient.retrofit.searchTodos(userId, keyword).enqueue(object : Callback<List<MooDoToDo>> {
                    override fun onResponse(
                        call: Call<List<MooDoToDo>>,
                        response: Response<List<MooDoToDo>>
                    ) {
                        if (response.isSuccessful) {
                            if (response.body() == null) {
                                searchTxt.hint = "검색 결과가 없습니다."
                            }
                            val todoList = response.body() ?: mutableListOf()
                            Log.d("MooDoLog Search", todoList.toString())
                            toDoAdapter.todoList.clear()
                            toDoAdapter.todoList.addAll(todoList)
                            toDoAdapter.notifyDataSetChanged()
                        }else {
                            Log.d("MooDoLog", "Response is not successful: ${response.code()}")
                        }
                    }
                    override fun onFailure(call: Call<List<MooDoToDo>>, t: Throwable) {
                        Log.d("MooDoLog getTodo Fail", t.toString())
                    }
                })
            }

            searchTxt.setText(null)
        }

        // 일정 선택 시 To Do 로 이동
        toDoAdapter.onItemClickLister = object : ToDoAdapter.OnItemClickLister {
            override fun onItemClick(pos: Int) {
                val inputFormat = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
                val outputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

                val date = inputFormat.parse(toDoAdapter.todoList[pos].startDate)
                val selectDate = outputFormat.format(date!!)
                Log.d("MooDoLog Search", selectDate)

                val intent = Intent(this@MainActivity_ToDo_Search, MainActivity_ToDo::class.java)
                intent.putExtra("userId", userId)
                intent.putExtra("selectDate", selectDate)

                val stats = "Search"
                intent.putExtra("stats", stats)

                startActivity(intent)
            }
        }
    }
}