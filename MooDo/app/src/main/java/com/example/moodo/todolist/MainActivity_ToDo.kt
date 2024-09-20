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
import com.example.moodo.adapter.ToDoAdapter
import com.example.moodo.databinding.ActivityMainToDoBinding
import com.example.moodo.db.MooDoClient
import com.example.moodo.db.MooDoToDo
import com.example.moodo.db.MooDoUser
import com.example.moodo.mode.MainActivity_MooDo
import retrofit2.Call
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.Calendar
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
        var selectDate = intent.getStringExtra("selectDate")

        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

        val stats = intent.getStringExtra("stats")

        // 사용자 정보 가져오기
        loadUserInfo(userId!!)

        // to do list adapter
        toDoAdapter = ToDoAdapter()
        binding.tdListRecycler.adapter = toDoAdapter
        binding.tdListRecycler.layoutManager = LinearLayoutManager(this)

        val tdDateText = binding.tdDateText
        tdDateText.text = "${selectDate}"

        allTodoList(userId, selectDate!!)

        // 버튼 글자 색
        val defaultTextColor = resources.getColor(R.color.black, null)
        val selectedTextColor = resources.getColor(R.color.gray, null)
        // tdList 가 all 인지, complete 인지 active 인지 저장
        var tdListStats = "All"

        binding.btnBefore.setOnClickListener {
            val formatter = dateFormat.parse(tdDateText.text as String)

            val calendar = Calendar.getInstance()
            calendar.time = formatter
            calendar.add(Calendar.DAY_OF_MONTH, -1) // 하루 빼기

            tdDateText.text = dateFormat.format(calendar.time)
            selectDate = dateFormat.format(calendar.time).toString()

            allTodoList(userId, selectDate!!)
            btnVisible()
            binding.allList.setTextColor(selectedTextColor)
            binding.activeList.setTextColor(defaultTextColor)
            binding.completeList.setTextColor(defaultTextColor)
            tdListStats = "All"
        }

        binding.btnNext.setOnClickListener {
            val formatter = dateFormat.parse(tdDateText.text as String)

            val calendar = Calendar.getInstance()
            calendar.time = formatter
            calendar.add(Calendar.DAY_OF_MONTH, +1)

            tdDateText.text = dateFormat.format(calendar.time)
            selectDate = dateFormat.format(calendar.time).toString()

            allTodoList(userId, selectDate!!)
            btnVisible()
            binding.allList.setTextColor(selectedTextColor)
            binding.activeList.setTextColor(defaultTextColor)
            binding.completeList.setTextColor(defaultTextColor)
            tdListStats = "All"
        }


        // all, complete, active 에 따른 리스트 출력
        binding.allList.setOnClickListener {
            binding.allList.setTextColor(selectedTextColor)
            binding.activeList.setTextColor(defaultTextColor)
            binding.completeList.setTextColor(defaultTextColor)
            allTodoList(userId, selectDate!!)
            btnVisible()
            tdListStats = "All"
        }
        binding.activeList.setOnClickListener {
            binding.allList.setTextColor(defaultTextColor)
            binding.activeList.setTextColor(selectedTextColor)
            binding.completeList.setTextColor(defaultTextColor)
            activeTodoList(userId, selectDate!!)
            btnVisible()
            tdListStats = "Active"
        }
        binding.completeList.setOnClickListener {
            binding.allList.setTextColor(defaultTextColor)
            binding.activeList.setTextColor(defaultTextColor)
            binding.completeList.setTextColor(selectedTextColor)
            completeTodoList(userId, selectDate!!)
            btnVisible()
            tdListStats = "Complete"
        }

        // 클릭한 item pos 값 저장 변수
        var position = 0
        // tdList item 클릭 시 position 저장
        toDoAdapter.onItemClickLister = object :ToDoAdapter.OnItemClickLister{
            override fun onItemClick(pos: Int) {
                position = pos
                if (tdListStats != "Complete") {
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
                val toDoColor = it.data?.getStringExtra("toDoColor").toString()

                Log.d("MooDoLog sD fm", startDay)

                // 사용자 정보가 로드되었는지 확인 후 저장
                if (user != null) {
                    val insertList = MooDoToDo(0, user!!, toDoStr, startDay, endDay, null, null, toDoColor)
                    MooDoClient.retrofit.addTodo(insertList, userId.toString()).enqueue(object : retrofit2.Callback<MooDoToDo> {
                        override fun onResponse(call: Call<MooDoToDo>, response: Response<MooDoToDo>) {
                            if (response.isSuccessful) {
                                Log.d("MooDoLog ToDoSuccess", response.body().toString())

                                if (tdListStats == "All") {
                                    allTodoList(userId, selectDate!!)
                                }
                                else if(tdListStats == "Active") {
                                    activeTodoList(userId, selectDate!!)
                                }
                                else {
                                    completeTodoList(userId, selectDate!!)
                                }
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

        // 수정 intent 처리
        val activityUpdate = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == RESULT_OK) {
                val startDay = it.data?.getStringExtra("startDay").toString()
                val endDay = it.data?.getStringExtra("endDay").toString()
                val toDoStr = it.data?.getStringExtra("toDoStr").toString()
                val toDoColor = it.data?.getStringExtra("toDoColor").toString()

                Log.d("MooDoLog update sD fm", startDay)
                Log.d("MooDoLog ToDoColor", toDoColor)

                // 사용자 정보가 로드되었는지 확인 후 저장
                if (user != null) {
                    val idx = toDoAdapter.todoList[position].idx
                    val insertList = MooDoToDo(idx, user!!, toDoStr, startDay, endDay, "N", null, toDoColor)

                    MooDoClient.retrofit.updateTodo(idx, insertList)
                        .enqueue(object : retrofit2.Callback<MooDoToDo> {
                            override fun onResponse(
                                call: Call<MooDoToDo>,
                                response: Response<MooDoToDo>
                            ) {
                                if (response.isSuccessful) {
                                    Log.d("MooDoLog upToDoSuccess", response.body().toString())
                                    toDoAdapter.updateItem(position, insertList)
                                } else {
                                    Log.d(
                                        "MooDoLog upToDo Error",
                                        "Error: ${response.code()} - ${response.message()}"
                                    )
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
            btnVisible()
        }
        // 수정 버튼
        binding.btnUpdate.setOnClickListener {
            // 완료되지 않은 일정만 수정 가능
            if (toDoAdapter.todoList[position].tdCheck == "N") {
                val intent = Intent(this@MainActivity_ToDo, MainActivity_ToDo_Write::class.java)
                intent.putExtra("userId", userId)
                val stats = "update"
                intent.putExtra("stats", stats)

                val inputFormat = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
                val outputDateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                val outputTimeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())

                val startDate = toDoAdapter.todoList[position].startDate
                val endDate = toDoAdapter.todoList[position].endDate

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

                val tdStr = toDoAdapter.todoList[position].tdList
                intent.putExtra("tdStr", tdStr)

                val toDoColor = toDoAdapter.todoList[position].color
                intent.putExtra("toDoColor", toDoColor)

                activityUpdate.launch(intent)
            }
            else {
                AlertDialog.Builder(binding.root.context)
                    .setMessage("이미 완료된 일정은 수정할 수 없습니다.")
                    .setPositiveButton("확인", null)
                    .show()
            }
        }

        // 완료 버튼
        binding.btnComplete.setOnClickListener {
            if (toDoAdapter.todoList[position].tdCheck == "N") {
                val idx = toDoAdapter.todoList[position].idx

                MooDoClient.retrofit.updateCheck(idx)
                    .enqueue(object : retrofit2.Callback<MooDoToDo> {
                        override fun onResponse(
                            call: Call<MooDoToDo>,
                            response: Response<MooDoToDo>
                        ) {
                            if (response.isSuccessful) {
                                Log.d("MooDoLog y", response.body().toString())
                                if (tdListStats == "All") {
                                    allTodoList(userId, selectDate!!)
                                }
                                else if(tdListStats == "Active") {
                                    activeTodoList(userId, selectDate!!)
                                }
                            }
                        }
                        override fun onFailure(call: Call<MooDoToDo>, t: Throwable) {
                            Log.d("MooDoLog y", t.toString())
                        }
                    })

                btnVisible()
            }
            else {
                AlertDialog.Builder(binding.root.context)
                    .setMessage("이미 완료된 일정입니다.")
                    .setPositiveButton("확인", null)
                    .show()
            }
        }

        // 삭제 버튼
        binding.btnDelete.setOnClickListener {
            val deleteItem = toDoAdapter.todoList.get(position)

            MooDoClient.retrofit.deleteTodo(deleteItem.idx).enqueue(object:retrofit2.Callback<Void>{
                override fun onResponse(call: Call<Void>, response: Response<Void>) {
                    if (response.isSuccessful){
                        toDoAdapter.removeItem(position)
                    }
                }

                override fun onFailure(call: Call<Void>, t: Throwable) {
                    Log.d("MooDoLog Del Fail", t.toString())
                }
            })
            btnVisible()
        }

        // 뒤로가기
        binding.btnClose.setOnClickListener {
            if (stats == "MooDo") {
                val intent = Intent().apply {
                    putExtra("update", true)
                }
                setResult(RESULT_OK, intent)
                finish()
            }
            else {
                val intent = Intent(this@MainActivity_ToDo, MainActivity_MooDo::class.java)
                intent.putExtra("id", userId)
                startActivity(intent)
            }
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