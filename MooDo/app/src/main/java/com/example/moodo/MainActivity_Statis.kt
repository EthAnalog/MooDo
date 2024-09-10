package com.example.moodo

import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.moodo.adapter.MoodAdapter
import com.example.moodo.adapter.ToDoAdapter
import com.example.moodo.databinding.ActivityMainStatisBinding
import com.example.moodo.db.MooDoClient
import com.example.moodo.db.MooDoMode
import com.example.moodo.db.MooDoUser
import com.example.moodo.mode.MainActivity_ModeWrite
import retrofit2.Call
import retrofit2.Response
import java.util.Optional

class MainActivity_Statis : AppCompatActivity() {
    // 사용자 정보 저장
    var user:MooDoUser? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val binding = ActivityMainStatisBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
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

        // adapter
        val moodAdapter = MoodAdapter()
        binding.recyclerView.adapter = moodAdapter
        binding.recyclerView.layoutManager = LinearLayoutManager(this)

        // 한달 동안 기록 가져오기
        MooDoClient.retrofit.getUserMoodListByMonth(userId, year, month).enqueue(object:retrofit2.Callback<List<MooDoMode>>{
            override fun onResponse(
                call: Call<List<MooDoMode>>,
                response: Response<List<MooDoMode>>
            ) {
                if (response.isSuccessful) {
                    val modeList = response.body() ?: mutableListOf()
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

        // 달성률 표시
        var todoNum = "0"
        var completeTodoNum = "0"
        MooDoClient.retrofit.getTodoCountForMonth(userId, year, month).enqueue(object:retrofit2.Callback<Int>{
            override fun onResponse(call: Call<Int>, response: Response<Int>) {
                if (response.isSuccessful){
                    Log.d("MooDoLog todoCnt", response.body().toString())
                    todoNum = response.body().toString()
                    binding.todoNum.text = "/${todoNum}"
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
                    completeTodoNum = response.body().toString()
                    binding.completeTodo.text = completeTodoNum
                }
                else {
                    Log.d("MooDoLog todoCnt", "Response is not successful: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<Int>, t: Throwable) {
                Log.d("MooDoLog todoCnt Fail", t.toString())
            }
        })

        // 한달 동안 기록된 가장 많은 감정
        MooDoClient.retrofit.getUserMoodNumByMonth(userId, year, month).enqueue(object:retrofit2.Callback<Int>{
            override fun onResponse(call: Call<Int>, response: Response<Int>) {
                if (response.isSuccessful) {
                    when(response.body()) {
                        1-> binding.tvMoodMax.setImageResource(R.drawable.angry)
                        2-> binding.tvMoodMax.setImageResource(R.drawable.sad)
                        3-> binding.tvMoodMax.setImageResource(R.drawable.meh)
                        4-> binding.tvMoodMax.setImageResource(R.drawable.s_happy)
                        5-> binding.tvMoodMax.setImageResource(R.drawable.happy)
                        else-> binding.tvMoodMax.setImageResource(R.drawable.no_mood)
                    }
                }else {
                    Log.d("MooDoLog moodIcon", "Response is not successful: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<Int>, t: Throwable) {
                Log.d("MooDoLog moodIcon Fail", t.toString())
            }

        })

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
                intent.putExtra("userId", userId)
                intent.putExtra("stats", stats)
                intent.putExtra("selectDate", selectDate)
                intent.putExtra("diary", diary)

                activityUpdate.launch(intent)
            }

            // 롱 클릭
            override fun onItemLongClcik(pos: Int) {
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