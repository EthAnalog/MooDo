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
import com.example.moodo.MainActivity
import com.example.moodo.MainActivity_Statis
import com.example.moodo.R
import com.example.moodo.adapter.CalendarMoodAdapter
import com.example.moodo.calendar.MoodMonthAdapter
import com.example.moodo.databinding.ActivityMainMoodCalendarBinding
import com.example.moodo.db.MooDoClient
import com.example.moodo.db.MooDoMode
import com.example.moodo.db.MooDoUser
import com.example.moodo.user.MainActivity_MyPage
import com.google.android.material.navigation.NavigationView
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.Optional


class MainActivity_Mood_Calendar : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {
    lateinit var binding:ActivityMainMoodCalendarBinding
    lateinit var monthAdapter:MoodMonthAdapter
    lateinit var drawerLayout: DrawerLayout
    lateinit var userId:String
    var user:MooDoUser? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityMainMoodCalendarBinding.inflate(layoutInflater)
        setContentView(binding.root)

        drawerLayout = binding.drawerLayout
        binding.menuBtn.setOnClickListener {
            drawerLayout.openDrawer(GravityCompat.END)
        }
        binding.navView.setNavigationItemSelectedListener(this)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // 사용자 id
        userId = intent.getStringExtra("userId").toString()
        loadUserInfo(userId)

        // 선택한 날짜 저장할 TextView 변수
        val saveDate = binding.saveDate

        val moodAdapter = CalendarMoodAdapter()
        binding.moodListLayout.adapter = moodAdapter
        binding.moodListLayout.layoutManager = LinearLayoutManager(this)

        val monthListManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        monthAdapter = MoodMonthAdapter(userId).apply {
            // 날짜 클릭
            onDaySelectedListener = object:MoodMonthAdapter.OnDaySelectedListener{
                override fun onDaySelected(date: String) {
                    MooDoClient.retrofit.getUserDayMood(userId, date).enqueue(object:retrofit2.Callback<List<MooDoMode>>{
                        override fun onResponse(
                            call: Call<List<MooDoMode>>,
                            response: Response<List<MooDoMode>>
                        ) {
                            if (response.isSuccessful) {

                                val moodList = response.body() ?: mutableListOf()

                                moodAdapter.moodList.clear()
                                moodAdapter.moodList.addAll(moodList)
                                moodAdapter.notifyDataSetChanged()
                            }else {
                                Log.d("MooDoLog Mood", "Response is not successful: ${response.code()}")
                            }
                        }

                        override fun onFailure(call: Call<List<MooDoMode>>, t: Throwable) {
                            Log.d("MooDoLog Mood Fail", t.toString())
                        }
                    })
                    saveDate.text = date
                }
            }
        }

        binding.calendarCustom.apply {
            layoutManager = monthListManager
            adapter = monthAdapter
            scrollToPosition(Int.MAX_VALUE / 2)
        }

        val snap = PagerSnapHelper()
        snap.attachToRecyclerView(binding.calendarCustom)

        var position = 0
        val activityMoodListUpdate = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
            if (it.resultCode == RESULT_OK) {
                val update = it.data?.getBooleanExtra("update", false) ?: false
                val mdDaily = it.data?.getStringExtra("mdDaily")
                val mdMode = it.data?.getIntExtra("mdMode", 0) ?: 0
                val weather = it.data?.getIntExtra("weather", 0) ?: 0

                Log.d("MooDoLog mode", update.toString())
                if (update && user != null) {
                    val idx = moodAdapter.moodList[position].idx
                    val createDate = moodAdapter.moodList[position].createdDate

                    val updateMode =
                        MooDoMode(idx, user!!, mdMode, createDate, weather, mdDaily.toString())
                    MooDoClient.retrofit.update(idx, updateMode)
                        .enqueue(object : retrofit2.Callback<Optional<MooDoMode>> {
                            override fun onResponse(
                                call: Call<Optional<MooDoMode>>,
                                response: Response<Optional<MooDoMode>>
                            ) {
                                if (response.isSuccessful) {
                                    val date = saveDate.text.toString()
                                    refreshMoodList(date)
                                    monthAdapter.notifyDataSetChanged()
                                } else {
                                    Log.d(
                                        "MooDoLog modeUp Error",
                                        "Error: ${response.code()} - ${response.message()}"
                                    )
                                }
                            }

                            override fun onFailure(call: Call<Optional<MooDoMode>>, t: Throwable) {
                                Log.d("MooDoLog modeUp Fail", t.toString())
                            }
                        })
                }
            }
        }
        // mood list 클릭 이벤트
        moodAdapter.onItemClickLister = object :CalendarMoodAdapter.OnItemClickLister {
            override fun onItemClick(pos: Int) {
                position = pos
                val intent = Intent(this@MainActivity_Mood_Calendar, MainActivity_ModeWrite::class.java)
                val selectDate = saveDate.text.toString()

                intent.putExtra("userId", userId)
                intent.putExtra("selectDate", selectDate)
                val stats = "update"
                intent.putExtra("stats", stats)

                val mdMode = moodAdapter.moodList[pos].mdMode
                val weather = moodAdapter.moodList[pos].weather
                val diary = moodAdapter.moodList[pos].mdDaily

                intent.putExtra("mdMode", mdMode)
                intent.putExtra("weather", weather)
                intent.putExtra("diary", diary)

                // startActivity(intent)
                activityMoodListUpdate.launch(intent)
            }
        }

        // mood 저장 업데이트
        val activityMoodListInsert = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                val update = result.data?.getBooleanExtra("update", false) ?: false
                if (update) {
                    val date = saveDate.text.toString()
                    refreshMoodList(date)
                    monthAdapter.notifyDataSetChanged()
                }
            }
        }
        binding.btnWrite.setOnClickListener {
            val selectDate = saveDate.text.toString()

            val intent = Intent(this@MainActivity_Mood_Calendar, MainActivity_ModeWrite::class.java)

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
                                    activityMoodListInsert.launch(intent)
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
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            // 캘린더
            R.id.nav_to_do_list -> {
                val intent = Intent(this@MainActivity_Mood_Calendar, MainActivity_MooDo::class.java)
                intent.putExtra("id", userId)

                startActivity(intent)
            }
            // 한달 총평
            R.id.nav_statis -> {
                // 한 달 총평
                val selectDate = binding.saveDate.text.toString()

                val intent = Intent(this@MainActivity_Mood_Calendar, MainActivity_Statis::class.java)

                intent.putExtra("userId", userId)
                intent.putExtra("selectDate", selectDate)

                startActivity(intent)
                // activityStatisMood.launch(intent)
            }
            // 마이페이지
            R.id.nav_mypage->{
                // my page
                val intent = Intent(this@MainActivity_Mood_Calendar, MainActivity_MyPage::class.java)

                intent.putExtra("userId", userId)
                // startActivity(intent)
                activityProfileUpdate.launch(intent)
            }

            // 로그아웃
            R.id.nav_logout -> {
                // 로그아웃
                AlertDialog.Builder(this).apply {
                    setTitle("로그아웃")
                    setMessage("로그아웃 하시겠습니까?")
                    setPositiveButton("확인") { _,_ ->
                        val intent = Intent(this@MainActivity_Mood_Calendar, MainActivity::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        startActivity(intent)
                        finish()
                    }
                    setNegativeButton("취소", null)
                    show()
                }
            }
        }
        drawerLayout.closeDrawer(GravityCompat.END)
        return true
    }
    override fun onResume() {
        super.onResume()
        val date = binding.saveDate.text.toString()
        refreshMoodList(date)
    }
    private fun refreshMoodList(date:String){
        val userId = intent.getStringExtra("id").toString()

        MooDoClient.retrofit.getUserDayMood(userId, date).enqueue(object:retrofit2.Callback<List<MooDoMode>>{
            override fun onResponse(
                call: Call<List<MooDoMode>>,
                response: Response<List<MooDoMode>>
            ) {
                if (response.isSuccessful) {
                    val moodAdapter = binding.moodListLayout.adapter as CalendarMoodAdapter
                    val moodList = response.body() ?: mutableListOf()
                    moodAdapter.moodList.clear()
                    moodAdapter.moodList.addAll(moodList)
                    moodAdapter.notifyDataSetChanged()
                }else {
                    Log.d("MooDoLog Mood", "Response is not successful: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<List<MooDoMode>>, t: Throwable) {
                Log.d("MooDoLog Mood Fail", t.toString())
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

        MooDoClient.retrofit.getUserImg(userId).enqueue(object: Callback<ResponseBody> {
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
}