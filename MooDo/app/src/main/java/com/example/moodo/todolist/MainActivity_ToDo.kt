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
import com.example.moodo.databinding.ActivityMainToDoBinding
import com.example.moodo.db.MooDoClient
import com.example.moodo.db.MooDoToDo
import com.example.moodo.adapter.ToDoAdapter
import com.example.moodo.db.MooDoUser
import retrofit2.Call
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MainActivity_ToDo : AppCompatActivity() {
    // 사용자 정보 저장
    var user:MooDoUser? = null

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
        // 수정, 삭제, 완료 버튼 비활성화
        fun resetBtn() {
            binding.btnDelete.isEnabled = false
            binding.btnUpdate.isEnabled = false
            binding.btnSuccess.isEnabled = false
        }

        val userId = intent.getStringExtra("userId")
        val selectDate = intent.getStringExtra("selectDate")
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val formattedDate = SimpleDateFormat("yyyy-MM-dd").parse(selectDate)
            ?.let { dateFormat.format(it) }

        // 사용자 정보 가져오기
        loadUserInfo(userId!!)

        // check = Y
        val todoAdapterY = ToDoAdapter()
        binding.toDoComplete.adapter = todoAdapterY
        binding.toDoComplete.layoutManager = LinearLayoutManager(this)

        // check = N
        val todoAdapterN = ToDoAdapter()
        binding.toDoList.adapter = todoAdapterN
        binding.toDoList.layoutManager = LinearLayoutManager(this)

        // 클릭한 item pos 값 저장 변수
        var position = 0

        // check = N list item click
        todoAdapterN.onItemClickLister = object :ToDoAdapter.OnItemClickLister{
            override fun onItemClick(pos: Int) {
                position = pos

                binding.btnDelete.isEnabled = true
                binding.btnUpdate.isEnabled = true
                binding.btnSuccess.isEnabled = true
            }
        }

        // list item 채워넣기
        // check = N
        MooDoClient.retrofit.getTodoListN(userId, formattedDate!!).enqueue(object:retrofit2.Callback<List<MooDoToDo>>{
            override fun onResponse(
                call: Call<List<MooDoToDo>>,
                response: Response<List<MooDoToDo>>
            ) {
                if (response.isSuccessful) {
                    val todoList = response.body() ?: mutableListOf()
                    todoAdapterN.todoList.addAll(todoList)
                    todoAdapterN.notifyDataSetChanged()
                } else {
                    Log.d("MooDoLog", "Response is not successful: ${response.code()}")
                }
            }
            override fun onFailure(call: Call<List<MooDoToDo>>, t: Throwable) {
                Log.d("MooDoLog getTodo Fail", t.toString())
            }
        })
        // check = Y
        MooDoClient.retrofit.getTodoListY(userId, formattedDate!!).enqueue(object:retrofit2.Callback<List<MooDoToDo>>{
            override fun onResponse(
                call: Call<List<MooDoToDo>>,
                response: Response<List<MooDoToDo>>
            ) {
                if (response.isSuccessful) {
                    val todoList = response.body() ?: mutableListOf()
                    todoAdapterY.todoList.addAll(todoList)
                    todoAdapterY.notifyDataSetChanged()
                } else {
                    Log.d("MooDoLog", "Response is not successful: ${response.code()}")
                }
            }
            override fun onFailure(call: Call<List<MooDoToDo>>, t: Throwable) {
                Log.d("MooDoLog getTodo Fail", t.toString())
            }
        })

        // 작성 intent 처리
        val activityInsert = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == RESULT_OK) {
                val startDay = it.data?.getStringExtra("startDay").toString()
                val endDay = it.data?.getStringExtra("endDay").toString()
                val toDoStr = it.data?.getStringExtra("toDoStr").toString()


                Log.d("MooDoLog sD fm", startDay)

                // 사용자 정보가 로드되었는지 확인 후 저장
                if (user != null) {
                    val insertList = MooDoToDo(0, user!!, toDoStr, startDay, endDay, null, null)
                    MooDoClient.retrofit.addTodo(insertList, userId.toString()).enqueue(object : retrofit2.Callback<MooDoToDo> {
                        override fun onResponse(call: Call<MooDoToDo>, response: Response<MooDoToDo>) {
                            if (response.isSuccessful) {
                                Log.d("MooDoLog ToDoSuccess", response.body().toString())
                                response.body()?.let { it1 -> todoAdapterN.addItem(it1) }
                            } else {
                                Log.d("MooDoLog ToDo Error", "Error: ${response.code()} - ${response.message()}")
                            }
                        }

                        override fun onFailure(call: Call<MooDoToDo>, t: Throwable) {
                            Log.d("MooDoLog Response ToDoFail", t.toString())
                        }
                    })
                } else {
                    Log.d("MooDoLog Error", "User is null, unable to save ToDo")
                }
            }
        }
        // 작성 버튼
        binding.btnWrite.setOnClickListener {
            val intent = Intent(this@MainActivity_ToDo, MainActivity_ToDo_Write::class.java)
            intent.putExtra("userId", userId)
            val stats = "insert"
            intent.putExtra("stats", stats)
            intent.putExtra("selectDate", selectDate)
            activityInsert.launch(intent)
        }

        // 수정 intent 처리
        val activityUpdate = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == RESULT_OK) {
                val startDay = it.data?.getStringExtra("startDay").toString()
                val endDay = it.data?.getStringExtra("endDay").toString()
                val toDoStr = it.data?.getStringExtra("toDoStr").toString()

                Log.d("MooDoLog update sD fm", startDay)

                // 사용자 정보가 로드되었는지 확인 후 저장
                if (user != null) {
                    val idx = todoAdapterN.todoList[position].idx
                    val insertList = MooDoToDo(idx, user!!, toDoStr, startDay, endDay, null, null)

                    MooDoClient.retrofit.updateTodo(idx, insertList).enqueue(object:retrofit2.Callback<MooDoToDo>{
                        override fun onResponse(
                            call: Call<MooDoToDo>,
                            response: Response<MooDoToDo>
                        ) {
                            if (response.isSuccessful) {
                                Log.d("MooDoLog upToDoSuccess", response.body().toString())
                                todoAdapterN.updateItem(position, insertList)
                            } else {
                                Log.d("MooDoLog upToDo Error", "Error: ${response.code()} - ${response.message()}")
                            }
                        }

                        override fun onFailure(call: Call<MooDoToDo>, t: Throwable) {
                            Log.d("MooDoLog Response upToDoFail", t.toString())
                        }

                    })
                } else {
                    Log.d("MooDoLog Error", "User is null, unable to save ToDo")
                }
            }

            resetBtn()
        }
        // 수정 버튼
        binding.btnUpdate.setOnClickListener {
            val intent = Intent(this@MainActivity_ToDo, MainActivity_ToDo_Write::class.java)
            intent.putExtra("userId", userId)
            val stats = "update"
            intent.putExtra("stats", stats)

            val inputFormat = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
            val outputDateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val outputTimeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())

            val startDate = todoAdapterN.todoList[position].startDate
            val endDate = todoAdapterN.todoList[position].endDate

            val date = inputFormat.parse(startDate)
            val date2 = inputFormat.parse(endDate)

            // Date 객체를 날짜와 시간 문자열 변환
            val startDay = date?.let { outputDateFormat.format(it) } ?: ""
            val startTime = date?.let { outputTimeFormat.format(it) } ?: ""
            val endDay = date2?.let { outputDateFormat.format(it) } ?: ""
            val endTime = date2?.let { outputTimeFormat.format(it) } ?: ""

            intent.putExtra("startDay", startDay)
            intent.putExtra("startTime", startTime)
            intent.putExtra("endDay", endDay)
            intent.putExtra("endTime", endTime)

            val tdStr = todoAdapterN.todoList[position].tdList
            intent.putExtra("tdStr", tdStr)

            activityUpdate.launch(intent)
        }

        // 완료 버튼
        binding.btnSuccess.setOnClickListener {
            val idx = todoAdapterN.todoList[position].idx

            MooDoClient.retrofit.updateCheck(idx).enqueue(object:retrofit2.Callback<MooDoToDo>{
                override fun onResponse(call: Call<MooDoToDo>, response: Response<MooDoToDo>) {
                    if (response.isSuccessful){
                        Log.d("MooDoLog y", response.body().toString())
                        // check = N 리스트 새로 가져오기
                        MooDoClient.retrofit.getTodoListN(userId, formattedDate!!).enqueue(object : retrofit2.Callback<List<MooDoToDo>> {
                            override fun onResponse(call: Call<List<MooDoToDo>>, response: Response<List<MooDoToDo>>) {
                                if (response.isSuccessful) {
                                    val todoList = response.body() ?: mutableListOf()
                                    todoAdapterN.todoList.clear()
                                    todoAdapterN.todoList.addAll(todoList)
                                    todoAdapterN.notifyDataSetChanged()
                                } else {
                                    Log.d("MooDoLog", "Response is not successful: ${response.code()}")
                                }
                            }

                            override fun onFailure(call: Call<List<MooDoToDo>>, t: Throwable) {
                                Log.d("MooDoLog", t.toString())
                            }
                        })

                        // check = Y 리스트 새로 가져오기
                        MooDoClient.retrofit.getTodoListY(userId, formattedDate!!).enqueue(object : retrofit2.Callback<List<MooDoToDo>> {
                            override fun onResponse(call: Call<List<MooDoToDo>>, response: Response<List<MooDoToDo>>) {
                                if (response.isSuccessful) {
                                    val todoList = response.body() ?: mutableListOf()
                                    todoAdapterY.todoList.clear()
                                    todoAdapterY.todoList.addAll(todoList)
                                    todoAdapterY.notifyDataSetChanged()
                                } else {
                                    Log.d("MooDoLog", "Response is not successful: ${response.code()}")
                                }
                            }

                            override fun onFailure(call: Call<List<MooDoToDo>>, t: Throwable) {
                                Log.d("MooDoLog", t.toString())
                            }
                        })
                    }
                }

                override fun onFailure(call: Call<MooDoToDo>, t: Throwable) {
                    Log.d("MooDoLog y", t.toString())
                }
            })

            resetBtn()
        }

        // 삭제 버튼
        binding.btnDelete.setOnClickListener {
            val deleteItem = todoAdapterN.todoList.get(position)

            MooDoClient.retrofit.deleteTodo(deleteItem.idx).enqueue(object:retrofit2.Callback<Void>{
                override fun onResponse(call: Call<Void>, response: Response<Void>) {
                    if (response.isSuccessful){
                        todoAdapterN.removeItem(position)
                    }
                }

                override fun onFailure(call: Call<Void>, t: Throwable) {
                    Log.d("MooDoLog Del Fail", t.toString())
                }
            })

            resetBtn()
        }

        // 뒤로가기
        binding.btnClose.setOnClickListener {
            val intent = Intent().apply {
                putExtra("update", true)
            }
            setResult(RESULT_OK, intent)
            finish()
        }
    }

    // 사용자 정보를 비동기적으로 로드
    private fun loadUserInfo(userId: String) {
        MooDoClient.retrofit.getUserInfo(userId).enqueue(object : retrofit2.Callback<MooDoUser> {
            override fun onResponse(call: Call<MooDoUser>, response: Response<MooDoUser>) {
                if (response.isSuccessful) {
                    user = response.body()
                    Log.d("MooDoLog Response", "User: $user")
                } else {
                    Log.d("MooDoLog Response", "Error: ${response.code()} - ${response.message()}")
                }
            }

            override fun onFailure(call: Call<MooDoUser>, t: Throwable) {
                Log.d("MooDoLog Response", t.toString())
            }
        })
    }
}