package com.example.moodo.mode

import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.ImageView
import android.widget.TextView
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
import com.example.moodo.calendar.MonthAdapter
import com.example.moodo.databinding.ActivityMainMooDoBinding
import com.example.moodo.db.MooDoClient
import com.example.moodo.db.MooDoToDo
import com.example.moodo.db.MooDoUser
import com.example.moodo.todolist.MainActivity_ToDo
import com.example.moodo.todolist.MainActivity_ToDo_Search
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
        val userAge = intent.getStringExtra("age").toString()

        loadUserInfo(userId)

        Log.d("MooDoLog UserInfo", userAge)

        // 선택한 날짜 저장할 TextView 변수
        val saveDate = binding.saveDate

        // tdAdapter
        val todoAdapter = CalendarToDoAdapter()
        binding.todoListLayout.adapter = todoAdapter
        binding.todoListLayout.layoutManager = LinearLayoutManager(this)

        // custom calendar 연결
        val monthListManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        monthAdapter = MonthAdapter(userId, userAge).apply {
            // 날짜 선택
            onDaySelectedListener = object :MonthAdapter.OnDaySelectedListener{
                override fun onDaySelected(date: String) {
                    Log.d("MooDoLog Id", userId)
                    Log.d("MooDoLog day", date)

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
        // btnWrite 버튼 이벤트
        binding.btnWrite.setOnClickListener {
            val intent = Intent(this, MainActivity_ToDo::class.java)
            val selectDate = saveDate.text.toString()

            intent.putExtra("userId", userId)
            intent.putExtra("selectDate", selectDate)
            val stats = "MooDo"
            intent.putExtra("stats", stats)

            // startActivity(intent)
            activityToDoListUpdate.launch(intent)
        }

        // 검색 기능
        binding.searchBtn.setOnClickListener {
            val intent = Intent(this@MainActivity_MooDo, MainActivity_ToDo_Search::class.java)
            intent.putExtra("userId", userId)
            intent.putExtra("userAge", userAge)

            startActivity(intent)
        }
    }
    override fun onNavigationItemSelected(item:MenuItem):Boolean {
        when(item.itemId) {
            R.id.nav_mood_write -> {
                // 감정 쓰기 클릭 이벤트
                val selectDate = binding.saveDate.text.toString()

                val intent = Intent(this, MainActivity_ModeWrite::class.java)

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
                                        val stats = "insert"
                                        intent.putExtra("stats", stats)

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
            R.id.nav_statis -> {
                // 한 달 총평
                val selectDate = binding.saveDate.text.toString()

                val intent = Intent(this@MainActivity_MooDo, MainActivity_Statis::class.java)

                intent.putExtra("userId", userId)
                intent.putExtra("selectDate", selectDate)

                activityStatisMood.launch(intent)
            }
            R.id.nav_mypage->{
                // my page
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
            val imageUrl = "C:\\fullstack\\AndroidProject\\MooDo_Spring\\"
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
                    val inputStream = response.body()?.byteStream()
                    val bitmap = BitmapFactory.decodeStream(inputStream)
                    userImg.setImageBitmap(bitmap)
                }
                Log.d("MooDoLog Img", userId.toString())
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Log.d("MooDoLog Img", userId.toString())
            }

        })
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