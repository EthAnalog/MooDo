package com.example.moodo.todolist

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isGone
import androidx.core.view.isVisible
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
import java.util.Calendar
import java.util.Date
import java.util.Locale

class MainActivity_ToDo : AppCompatActivity() {
    // 사용자 정보 저장
    var user:MooDoUser? = null
    lateinit var toDoAdapter:ToDoAdapter

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
        // floatBtn
        fun btnVisible(){
            binding.btnComplete.isGone = true
            binding.btnUpdate.isGone = true
            binding.btnDelete.isGone = true
        }

        val userId = intent.getStringExtra("userId")
        val selectDate = intent.getStringExtra("selectDate")
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val formattedDate = SimpleDateFormat("yyyy-MM-dd").parse(selectDate)
            ?.let { dateFormat.format(it) }

        // 사용자 정보 가져오기
        loadUserInfo(userId!!)

        // to do list adapter
        toDoAdapter = ToDoAdapter()
        binding.tdListRecycler.adapter = toDoAdapter
        binding.tdListRecycler.layoutManager = LinearLayoutManager(this)
        allTodoList(userId, selectDate!!)


        // 버튼 글자 색
        val defaultTextColor = resources.getColor(R.color.black, null)
        val selectedTextColor = resources.getColor(R.color.gray, null)
        // tdList 가 all 인지, complete 인지 active 인지 저장
        var tdListStats = "All"

        // all, complete, active 에 따른 리스트 출력
        binding.allList.setOnClickListener {
            binding.allList.setTextColor(selectedTextColor)
            binding.activeList.setTextColor(defaultTextColor)
            binding.completeList.setTextColor(defaultTextColor)
            allTodoList(userId, selectDate)
            btnVisible()
            tdListStats = "All"
        }
        binding.activeList.setOnClickListener {
            binding.allList.setTextColor(defaultTextColor)
            binding.activeList.setTextColor(selectedTextColor)
            binding.completeList.setTextColor(defaultTextColor)
            activeTodoList(userId, selectDate)
            btnVisible()
            tdListStats = "Active"
        }
        binding.completeList.setOnClickListener {
            binding.allList.setTextColor(defaultTextColor)
            binding.activeList.setTextColor(defaultTextColor)
            binding.completeList.setTextColor(selectedTextColor)
            completeTodoList(userId, selectDate)
            btnVisible()
            tdListStats = "Complete"
        }

        // 클릭한 item pos 값 저장 변수
        var position = 0
        // tdList item 클릭 시 position 저장
        toDoAdapter.onItemClickLister = object :ToDoAdapter.OnItemClickLister{
            override fun onItemClick(pos: Int) {
                position = pos
                if (tdListStats != "Active") {
                    binding.btnUpdate.isVisible = true
                    binding.btnComplete.isVisible = true
                    binding.btnDelete.isVisible = true
                }
                else {
                    binding.btnDelete.isVisible = true
                }
            }
        }

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
                                response.body()?.let { it1 -> toDoAdapter.addItem(it1) }
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
            // 오늘보다 이전 날짜에서 작성 버튼 클릭 x
            val formatter = dateFormat.parse(selectDate!!)
            // 시간 제외 날짜만 비교
            val today = dateFormat.format(Calendar.getInstance().time)
            val formatterDate = dateFormat.format(formatter!!)

            if (formatterDate < today) {
                AlertDialog.Builder(binding.root.context)
                    .setMessage("선택한 날짜가 오늘보다 이전입니다. 오늘부터 To do list를 작성할 수 있어요.")
                    .setPositiveButton("확인", null)
                    .show()
            }
            else {
                val intent = Intent(this@MainActivity_ToDo, MainActivity_ToDo_Write::class.java)
                intent.putExtra("userId", userId)
                val stats = "insert"
                intent.putExtra("stats", stats)
                intent.putExtra("selectDate", selectDate)
                activityInsert.launch(intent)
            }
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
    // 전체 tdList
    private fun allTodoList(userId: String, selectDate:String) {
        MooDoClient.retrofit.getTodoList(userId, selectDate!!).enqueue(object:retrofit2.Callback<List<MooDoToDo>>{
            override fun onResponse(
                call: Call<List<MooDoToDo>>,
                response: Response<List<MooDoToDo>>
            ) {
                if (response.isSuccessful) {
                    val todoList = response.body() ?: mutableListOf()
                    toDoAdapter.todoList.clear()
                    toDoAdapter.todoList.addAll(todoList)
                    toDoAdapter.notifyDataSetChanged()
                } else {
                    Log.d("MooDoLog", "Response is not successful: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<List<MooDoToDo>>, t: Throwable) {
                Log.d("MooDoLog getTodo Fail", t.toString())
            }
        })
    }
    // 진행 중 tdList
    private fun activeTodoList(userId: String, selectDate:String) {
        MooDoClient.retrofit.getTodoListN(userId, selectDate).enqueue(object:retrofit2.Callback<List<MooDoToDo>>{
            override fun onResponse(
                call: Call<List<MooDoToDo>>,
                response: Response<List<MooDoToDo>>
            ) {
                if (response.isSuccessful) {
                    val todoList = response.body() ?: mutableListOf()
                    toDoAdapter.todoList.clear()
                    toDoAdapter.todoList.addAll(todoList)
                    toDoAdapter.notifyDataSetChanged()
                } else {
                    Log.d("MooDoLog", "Response is not successful: ${response.code()}")
                }
            }
            override fun onFailure(call: Call<List<MooDoToDo>>, t: Throwable) {
                Log.d("MooDoLog getTodo Fail", t.toString())
            }
        })
    }
    // 완료 tdList
    private fun completeTodoList(userId: String, selectDate:String) {
        MooDoClient.retrofit.getTodoListY(userId, selectDate).enqueue(object:retrofit2.Callback<List<MooDoToDo>>{
            override fun onResponse(
                call: Call<List<MooDoToDo>>,
                response: Response<List<MooDoToDo>>
            ) {
                if (response.isSuccessful) {
                    val todoList = response.body() ?: mutableListOf()
                    toDoAdapter.todoList.clear()
                    toDoAdapter.todoList.addAll(todoList)
                    toDoAdapter.notifyDataSetChanged()
                } else {
                    Log.d("MooDoLog", "Response is not successful: ${response.code()}")
                }
            }
            override fun onFailure(call: Call<List<MooDoToDo>>, t: Throwable) {
                Log.d("MooDoLog getTodo Fail", t.toString())
            }
        })
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