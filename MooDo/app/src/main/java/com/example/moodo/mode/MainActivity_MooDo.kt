package com.example.moodo.mode

import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.PagerSnapHelper
import com.bumptech.glide.Glide
import com.example.moodo.MainActivity
import com.example.moodo.MainActivity_Statis
import com.example.moodo.R
import com.example.moodo.adapter.CalendarToDoAdapter
import com.example.moodo.adapter.HolidayAdapter
import com.example.moodo.calendar.MonthAdapter
import com.example.moodo.databinding.ActivityMainMooDoBinding
import com.example.moodo.db.MooDoClient
import com.example.moodo.db.MooDoHoliday
import com.example.moodo.db.MooDoToDo
import com.example.moodo.db.MooDoUser
import com.example.moodo.todolist.MainActivity_ToDo
import com.example.moodo.todolist.MainActivity_ToDo_Search
import com.example.moodo.todolist.MainActivity_ToDo_Write
import com.example.moodo.user.MainActivity_MyPage
import com.google.android.material.navigation.NavigationView
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.text.SimpleDateFormat
import java.util.*


class MainActivity_MooDo : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {
    lateinit var binding:ActivityMainMooDoBinding
    lateinit var monthAdapter:MonthAdapter
    lateinit var drawerLayout: DrawerLayout
    lateinit var userId:String
    var user:MooDoUser? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainMooDoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // drawerLayout 설정
        drawerLayout = binding.drawerLayout
        binding.menuBtn.setOnClickListener {
            // 사이드 바 열기
            drawerLayout.openDrawer(GravityCompat.END)
        }

        // 네비게이션 메뉴 설정
        binding.navView.setNavigationItemSelectedListener(this)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // 사용자 id
        userId = intent.getStringExtra("id").toString()

        loadUserInfo(userId)

        // 선택한 날짜 저장할 TextView 변수
        val saveDate = binding.saveDate

        // tdAdapter
        val todoAdapter = CalendarToDoAdapter()
        binding.todoListLayout.adapter = todoAdapter
        binding.todoListLayout.layoutManager = LinearLayoutManager(this)

        // holidayAdapter
        val holidayAdapter = HolidayAdapter()
        binding.holidayLayout.adapter = holidayAdapter
        binding.holidayLayout.layoutManager = LinearLayoutManager(this)

        // custom calendar 연결
        val monthListManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        monthAdapter = MonthAdapter(userId).apply {
            // 날짜 선택
            onDaySelectedListener = object :MonthAdapter.OnDaySelectedListener{
                override fun onDaySelected(date: String) {
                    Log.d("MooDoLog Id", userId)
                    Log.d("MooDoLog day", date)

                    MooDoClient.retrofit.getHoliday(date).enqueue(object:retrofit2.Callback<List<MooDoHoliday>> {
                        override fun onResponse(
                            call: Call<List<MooDoHoliday>>,
                            response: Response<List<MooDoHoliday>>
                        ) {
                            if (response.isSuccessful) {
                                val hdList = response.body() ?: mutableListOf()
                                holidayAdapter.holidayList.clear()
                                holidayAdapter.holidayList.addAll(hdList)
                                holidayAdapter.notifyDataSetChanged()
                            }
                            else {
                                Log.d("MooDoLog holiday", response.code().toString())
                            }
                        }

                        override fun onFailure(call: Call<List<MooDoHoliday>>, t: Throwable) {
                            Log.d("MooDoLog holiday", t.toString())
                        }

                    })

                    MooDoClient.retrofit.getTodoListN(userId, date).enqueue(object :retrofit2.Callback<List<MooDoToDo>>{
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
                    Log.d("MooDoLog saveDate", saveDate.text.toString())
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

        // to do list 클릭 이벤트
        todoAdapter.onItemClickLister = object :CalendarToDoAdapter.OnItemClickLister {
            override fun onItemClick(pos: Int) {
                val intent = Intent(this@MainActivity_MooDo, MainActivity_ToDo::class.java)
                val selectDate = saveDate.text.toString()

                intent.putExtra("userId", userId)
                intent.putExtra("selectDate", selectDate)
                val stats = "MooDo"
                intent.putExtra("stats", stats)

                // startActivity(intent)
                activityToDoListUpdate.launch(intent)
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
                    MooDoClient.retrofit.addTodo(insertList, userId).enqueue(object : retrofit2.Callback<MooDoToDo> {
                        override fun onResponse(call: Call<MooDoToDo>, response: Response<MooDoToDo>) {
                            if (response.isSuccessful) {
                                Log.d("MooDoLog ToDoSuccess", response.body().toString())
                                val date = saveDate.text.toString()
                                refreshTodoList(date)
                                monthAdapter.notifyDataSetChanged()
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
        // btnWrite 버튼 이벤트 -> 바로 작성 페이지 이동 (현재 날짜)
        binding.btnWrite.setOnClickListener {
            val intent = Intent(this, MainActivity_ToDo_Write::class.java)
            // 현재 날짜와 시간을 가져오기
            val calendar = Calendar.getInstance()
            // 날짜 포맷 정의
            val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            // 현재 날짜를 문자열로 변환
            val selectDate = dateFormat.format(calendar.time)

            val stats = "insert"
            intent.putExtra("stats", stats)
            intent.putExtra("selectDate", selectDate)
            intent.putExtra("userId", userId)

            // activityToDoListUpdate.launch(intent)
            activityInsert.launch(intent)
        }

        // 검색 기능
        binding.searchBtn.setOnClickListener {
            val intent = Intent(this@MainActivity_MooDo, MainActivity_ToDo_Search::class.java)
            intent.putExtra("userId", userId)

            startActivity(intent)
        }
    }
    override fun onNavigationItemSelected(item:MenuItem):Boolean {
        when(item.itemId) {
            R.id.nav_mood_write -> {
                val intent = Intent(this@MainActivity_MooDo, MainActivity_Mood_Calendar::class.java)

                intent.putExtra("userId", userId)

                startActivity(intent)
            }
            R.id.nav_statis -> {
                // 한 달 총평
                val selectDate = binding.saveDate.text.toString()

                val intent = Intent(this@MainActivity_MooDo, MainActivity_Statis::class.java)

                intent.putExtra("userId", userId)
                intent.putExtra("selectDate", selectDate)

                startActivity(intent)

                // activityStatisMood.launch(intent)
            }
            R.id.nav_mypage->{
                // my page
                val intent = Intent(this@MainActivity_MooDo, MainActivity_MyPage::class.java)

                intent.putExtra("userId", userId)
                // startActivity(intent)
                activityProfileUpdate.launch(intent)
            }
            R.id.nav_logout -> {
                // 로그아웃
                AlertDialog.Builder(this).apply {
                    setTitle("로그아웃")
                    setMessage("로그아웃 하시겠습니까?")
                    setPositiveButton("확인") { _,_ ->
                        val intent = Intent(this@MainActivity_MooDo, MainActivity::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        startActivity(intent)
                        finish()
                    }
                    setNegativeButton("취소", null)
                    show()
                }
            }
        }
        // 사이드 바 메뉴 닫기
        drawerLayout.closeDrawer(GravityCompat.END)
        return true
    }

    override fun onResume() {
        super.onResume()
        val date = binding.saveDate.text.toString()
        refreshTodoList(date)
    }
    private fun refreshTodoList(date:String){
        val userId = intent.getStringExtra("id").toString()

        MooDoClient.retrofit.getTodoListN(userId, date).enqueue(object : retrofit2.Callback<List<MooDoToDo>> {
            override fun onResponse(call: Call<List<MooDoToDo>>, response: Response<List<MooDoToDo>>) {
                if (response.isSuccessful) {
                    val todoList = response.body() ?: mutableListOf()
                    val todoAdapter = binding.todoListLayout.adapter as CalendarToDoAdapter
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

    // 사용자 정보를 비동기적으로 로드
    private fun loadUserInfo(userId: String) {
        val headerView = binding.navView.getHeaderView(0) // 헤더 레이아웃의 첫 번째 뷰를 가져옴
        val userName = headerView.findViewById<TextView>(R.id.userName)
        val userImg = headerView.findViewById<ImageView>(R.id.userImg)
        MooDoClient.retrofit.getUserInfo(userId).enqueue(object : retrofit2.Callback<MooDoUser> {
            override fun onResponse(call: Call<MooDoUser>, response: Response<MooDoUser>) {
                if (response.isSuccessful) {
                    user = response.body()
                    userName.text = user!!.name.toString()
                    Log.d("MooDoLog UserInfo", "User: $user")

                } else {
                    Log.d("MooDoLog UserInfo", "Error: ${response.code()} - ${response.message()}")
                }
            }

            override fun onFailure(call: Call<MooDoUser>, t: Throwable) {
                Log.d("MooDoLog UserInfo", t.toString())
            }
        })

        MooDoClient.retrofit.getUserImg(userId).enqueue(object:Callback<ResponseBody>{
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful) {
                    if (response.body() != null) {
                        val inputStream = response.body()?.byteStream()
                        val bitmap = BitmapFactory.decodeStream(inputStream)
                        userImg.setImageBitmap(bitmap)
                    }
                    else {
                        userImg.setImageResource(R.drawable.default_profile_image)
                    }
                }
                else {
                    userImg.setImageResource(R.drawable.default_profile_image)
                }
                Log.d("MooDoLog Img", response.body().toString())
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Log.d("MooDoLog ImgFail", t.toString())
                userImg.setImageResource(R.drawable.default_profile_image)
            }

        })
    }

    // 프로필 사진
    val activityProfileUpdate = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {result ->
        if (result.resultCode == RESULT_OK) {
            val update = result.data?.getBooleanExtra("update", false) ?: false
            if (update) {
                loadUserInfo(userId)
            }
        }
    }

    // mood intent
    val activityMoodListUpdate = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {result ->
        if (result.resultCode == RESULT_OK) {
            val update = result.data?.getBooleanExtra("update", false) ?: false
            if (update) {
                monthAdapter.notifyDataSetChanged()
            }
        }
    }
    // 한 달 기록 intent
    val activityStatisMood = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {result ->
        if (result.resultCode == RESULT_OK) {
            val update = result.data?.getBooleanExtra("update", false) ?: false
            if (update) {
                monthAdapter.notifyDataSetChanged()
            }
        }
    }
}