package com.example.moodo

import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isGone
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.moodo.adapter.MoodAdapter
import com.example.moodo.adapter.ToDoAdapter
import com.example.moodo.databinding.ActivityMainStatisBinding
import com.example.moodo.db.MooDoClient
import com.example.moodo.db.MooDoMode
import com.example.moodo.db.MooDoUser
import com.example.moodo.mode.MainActivity_ModeWrite
import com.example.moodo.mode.MainActivity_MooDo
import retrofit2.Call
import retrofit2.Response
import java.util.Calendar
import java.util.Optional

class MainActivity_Statis : AppCompatActivity() {
    // 사용자 정보 저장
    var user:MooDoUser? = null
    lateinit var moodAdapter: MoodAdapter

    lateinit var binding:ActivityMainStatisBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainStatisBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        fun btnVisible(year: Int, month:Int) {
            val calendar = Calendar.getInstance()
            val currentYear = calendar.get(Calendar.YEAR) // 현재 년도 가져오기
            val currentMonth = calendar.get(Calendar.MONTH) + 1

            if (currentYear.equals(year) && currentMonth.equals(month)) {
                binding.btnNext.isInvisible = true
            }
            else {
                binding.btnNext.isVisible = true
            }
        }

        val userId = intent.getStringExtra("userId")
        val selectDate = intent.getStringExtra("selectDate")

        // 사용자 정보 가져오기
        loadUserInfo(userId!!)

        // selectDate "-"로 분리
        val dateParts = selectDate!!.split("-")

        var year = 0
        var month = 0
        if (dateParts.size >= 2) {
            year = dateParts[0].toInt() // 년도 추출
            month = dateParts[1].toInt() // 월 추출
        }

        btnVisible(year, month)

        binding.btnNext.setOnClickListener {
            if (month.equals(12)) {
                month = 1
                year += 1
            }
            else {
                month += 1
            }
            btnVisible(year, month)
            getMoodList(userId, year, month)
            moodNumByMonth(userId, year, month)
            getMonthTdCnt(userId, year, month)
        }
        binding.btnBefore.setOnClickListener {
            if (month.equals(1)) {
                month = 12
                year -= 1
            }
            else {
                month -= 1
            }
            btnVisible(year, month)
            getMoodList(userId, year, month)
            moodNumByMonth(userId, year, month)
            getMonthTdCnt(userId, year, month)
        }

        // adapter
        moodAdapter = MoodAdapter()
        binding.recyclerView.adapter = moodAdapter
        binding.recyclerView.layoutManager = LinearLayoutManager(this)

        getMoodList(userId, year, month)
        moodNumByMonth(userId, year, month)
        getMonthTdCnt(userId, year, month)

        var position = 0
        // 수정 intent 처리
        val activityUpdate = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
            if (it.resultCode == RESULT_OK) {
                val update = it.data?.getBooleanExtra("update", false) ?: false
                val mdDaily = it.data?.getStringExtra("mdDaily")
                val mdMode = it.data?.getIntExtra("mdMode", 0) ?: 0
                val weather = it.data?.getIntExtra("weather", 0) ?: 0

                Log.d("MooDoLog mode", update.toString())
                if (update && user != null) {
                    val idx = moodAdapter.moodList[position].idx
                    val createDate = moodAdapter.moodList[position].createdDate

                    val updateMode = MooDoMode(idx, user!!, mdMode, createDate, weather, mdDaily.toString())
                    MooDoClient.retrofit.update(idx, updateMode).enqueue(object:retrofit2.Callback<Optional<MooDoMode>>{
                        override fun onResponse(
                            call: Call<Optional<MooDoMode>>,
                            response: Response<Optional<MooDoMode>>
                        ) {
                            if (response.isSuccessful) {
                                moodAdapter.updateItem(position, updateMode)
                                moodAdapter.notifyDataSetChanged()
                                moodNumByMonth(userId, year, month)
                                Log.d("MooDoLog modeUp", response.body().toString())
                            }
                            else {
                                Log.d("MooDoLog modeUp Error", "Error: ${response.code()} - ${response.message()}")
                            }
                        }

                        override fun onFailure(call: Call<Optional<MooDoMode>>, t: Throwable) {
                            Log.d("MooDoLog modeUp Fail", t.toString())
                        }
                    })
                }
            }
        }
        // 수정 및 삭제
        moodAdapter.onItemClickLister = object :MoodAdapter.OnItemClickLister{
            // 클릭
            override fun onItemClick(pos: Int) {
                position = pos

                val intent = Intent(this@MainActivity_Statis, MainActivity_ModeWrite::class.java)

                val stats = "update"
                val diary = moodAdapter.moodList[position].mdDaily
                val mdMode = moodAdapter.moodList[position].mdMode
                val weather = moodAdapter.moodList[position].weather
                val selectDate = moodAdapter.moodList[position].createdDate

                intent.putExtra("userId", userId)
                intent.putExtra("stats", stats)
                intent.putExtra("selectDate", selectDate)
                intent.putExtra("diary", diary)
                intent.putExtra("mdMode", mdMode)
                intent.putExtra("weather", weather)

                activityUpdate.launch(intent)
            }

            // 롱 클릭
            override fun onItemLongClick(pos: Int) {
                position = pos
                AlertDialog.Builder(binding.root.context).run {
                    setMessage("해당 기록을 삭제하시겠습니까?")
                    setNegativeButton("취소", null)
                    setPositiveButton("확인",object:DialogInterface.OnClickListener{
                        override fun onClick(p0: DialogInterface?, p1: Int) {
                            val idx = moodAdapter.moodList[position].idx
                            MooDoClient.retrofit.delete(idx).enqueue(object:retrofit2.Callback<Void>{
                                override fun onResponse(
                                    call: Call<Void>,
                                    response: Response<Void>
                                ) {
                                    if (response.isSuccessful) {
                                        moodAdapter.removeItem(position)
                                    }
                                }

                                override fun onFailure(call: Call<Void>, t: Throwable) {
                                    Log.d("MooDoLog deleteMood Fail", t.toString())
                                }
                            })
                        }
                    })
                    show()

                }
            }
        }

        // 뒤로 가기
        binding.btnClose.setOnClickListener {
            val intent = Intent(this@MainActivity_Statis, MainActivity_MooDo::class.java)
            intent.putExtra("id", userId)

            startActivity(intent)
        }
    }

    // 사용자 정보를 비동기적으로 로드
    private fun loadUserInfo(userId: String) {
        MooDoClient.retrofit.getUserInfo(userId).enqueue(object : retrofit2.Callback<MooDoUser> {
            override fun onResponse(call: Call<MooDoUser>, response: Response<MooDoUser>) {
                if (response.isSuccessful) {
                    user = response.body()
                    binding.userName.text = user!!.name.toString()
                    Log.d("MooDoLog UserInfo", "User: $user")
                } else {
                    Log.d("MooDoLog UserInfo", "Error: ${response.code()} - ${response.message()}")
                }
            }

            override fun onFailure(call: Call<MooDoUser>, t: Throwable) {
                Log.d("MooDoLog UserInfo", t.toString())
            }
        })
    }

    // 한달 간 기록
    private fun getMoodList(userId:String, year: Int, month: Int) {
        MooDoClient.retrofit.getUserMoodListByMonth(userId, year, month).enqueue(object:retrofit2.Callback<List<MooDoMode>>{
            override fun onResponse(
                call: Call<List<MooDoMode>>,
                response: Response<List<MooDoMode>>
            ) {
                if (response.isSuccessful) {
                    val modeList = response.body() ?: mutableListOf()
                    moodAdapter.moodList.clear()
                    moodAdapter.moodList.addAll(modeList)
                    moodAdapter.notifyDataSetChanged()
                } else {
                    Log.d("MooDoLog statis", "Response is not successful: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<List<MooDoMode>>, t: Throwable) {
                Log.d("MooDoLog Statis Fail", t.toString())
            }

        })

    }

    // 달성률
    private fun getMonthTdCnt(userId: String, year: Int, month: Int) {
        var allTodo = "0"
        var completeTodo = "0"
        MooDoClient.retrofit.getTodoCountForMonth(userId, year, month).enqueue(object:retrofit2.Callback<Int>{
            override fun onResponse(call: Call<Int>, response: Response<Int>) {
                if (response.isSuccessful){
                    Log.d("MooDoLog todoCnt", response.body().toString())
                    allTodo = response.body().toString()
                    binding.allTodo.text = "/${allTodo}"
                }
                else {
                    Log.d("MooDoLog todoCnt", "Response is not successful: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<Int>, t: Throwable) {
                Log.d("MooDoLog todoCnt Fail", t.toString())
            }
        })
        MooDoClient.retrofit.getCompletedTodoCountForMonth(userId, year, month).enqueue(object : retrofit2.Callback<Int> {
            override fun onResponse(call: Call<Int>, response: Response<Int>) {
                if (response.isSuccessful){
                    Log.d("MooDoLog todoCnt", response.body().toString())
                    completeTodo = response.body().toString()
                    binding.completeTodo.text = completeTodo
                }
                else {
                    Log.d("MooDoLog todoCnt", "Response is not successful: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<Int>, t: Throwable) {
                Log.d("MooDoLog todoCnt Fail", t.toString())
            }
        })
    }

    // 색상, 이모지 설정
    private fun updateEmoji(emojiImg: Int, mood:String, colorRes:Int, backColorRes:Int) {
        binding.tvMoodMax.setImageResource(emojiImg)
        binding.txtEmotion.text = mood

        val textColor = ContextCompat.getColor(this, colorRes)

        binding.topInfo.setBackgroundColor(textColor)
        binding.moodColorLayout.backgroundTintList = ContextCompat.getColorStateList(this, backColorRes)
    }
    private fun moodNumByMonth(userId: String, year:Int, month:Int) {
        // 한달 동안 기록된 가장 많은 감정
        MooDoClient.retrofit.getUserMoodNumByMonth(userId, year, month).enqueue(object:retrofit2.Callback<Int>{
            override fun onResponse(call: Call<Int>, response: Response<Int>) {
                if (response.isSuccessful) {
                    when(response.body()) {
                        1-> {
                            updateEmoji(R.drawable.ic_emotion_angry, "${month}월달은 최악의 기분을 느낀 날이 많았어요.", R.color.e_red, R.color.angry)
                        }
                        2-> {
                            updateEmoji(R.drawable.ic_emotion_sad, "${month}월달은 기분이 나빴던 날이 많았어요.", R.color.e_blue, R.color.sad)
                        }
                        3-> {
                            updateEmoji(R.drawable.ic_emotion_meh, "${month}월달은 기분이 평온했어요.", R.color.e_apricot, R.color.meh)
                        }
                        4-> {
                            updateEmoji(R.drawable.ic_emotion_s_happy, "${month}월달은 기분 좋은 날이 가장 많았어요.", R.color.e_green, R.color.s_happy)
                        }
                        5-> {
                            updateEmoji(R.drawable.ic_emotion_happy, "${month}월달은 기분이 최고였어요!", R.color.e_yellow, R.color.happy)
                        }
                        else-> {
                            updateEmoji(R.drawable.no_mood, "${month}월달은 기분이 기록되지 않았어요.", R.color.noStats, R.color.noStats2)
                        }
                    }
                }else {
                    Log.d("MooDoLog moodIcon", "Response is not successful: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<Int>, t: Throwable) {
                Log.d("MooDoLog moodIcon Fail", t.toString())
            }

        })

    }
}