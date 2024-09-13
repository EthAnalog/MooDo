package com.example.moodo.user

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
import com.example.moodo.MainActivity
import com.example.moodo.MainActivity_Statis
import com.example.moodo.R
import com.example.moodo.databinding.ActivityMainMyPageBinding
import com.example.moodo.databinding.DialogUserEditPassBinding
import com.example.moodo.db.MooDoClient
import com.example.moodo.db.MooDoUser
import com.example.moodo.mode.MainActivity_MooDo
import com.kakao.sdk.user.model.User
import retrofit2.Call
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class MainActivity_MyPage : AppCompatActivity() {
    lateinit var binding: ActivityMainMyPageBinding
    var user:MooDoUser?=null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainMyPageBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val userId = intent.getStringExtra("userId")
        // 오늘 날짜
        val calendar = Calendar.getInstance()
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        // 현재 날짜를 문자열로 변환
        val selectDate = dateFormat.format(calendar.time)

        loadUserInfo(userId!!)

        // profile
        // 사진 수정
        binding.btnImgUpdate.setOnClickListener {  }
        // 사진 삭제
        binding.btnImgDelete.setOnClickListener {  }

        // menu - moodo
        // 이달의 기록
        binding.btnMoodStats.setOnClickListener {
            val intent = Intent(this@MainActivity_MyPage, MainActivity_Statis::class.java)
            intent.putExtra("userId", userId)
            intent.putExtra("selectDate", selectDate)

            startActivity(intent)

        }
        // 캘린더 이동
        binding.btnCalendar.setOnClickListener {
            val intent = Intent(this@MainActivity_MyPage, MainActivity_MooDo::class.java)

            intent.putExtra("id", userId)

            startActivity(intent)
        }

        // menu = 회원
        // 로그아웃
        binding.btnLogout.setOnClickListener {
            AlertDialog.Builder(this).apply {
                setTitle("로그아웃")
                setMessage("로그아웃 하시겠습니까?")
                setPositiveButton("확인") { _,_ ->
                    val intent = Intent(this@MainActivity_MyPage, MainActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                    finish()
                }
                setNegativeButton("취소", null)
                show()
            }
        }

        // 회원정보 변경 intent 처리
        val activityUserUpdate = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == RESULT_OK) {
                val pass = it.data?.getStringExtra("pass").toString()
                val age = it.data?.getStringExtra("age").toString()

                Log.d("MooDoLog UserUp", age)

                MooDoClient.retrofit.changeUser(userId, pass, age).enqueue(object:retrofit2.Callback<Void>{
                    override fun onResponse(call: Call<Void>, response: Response<Void>) {
                        Log.d("MooDoLog UserCh", response.body().toString())
                        loadUserInfo(userId)
                    }
                    override fun onFailure(call: Call<Void>, t: Throwable) {
                        Log.d("MooDoLog UserCh fail", t.toString())
                    }
                })
            }
        }
        // 회원정보 변경
        binding.btnEditProfile.setOnClickListener {
            val userEditDialog = DialogUserEditPassBinding.inflate(layoutInflater)

            AlertDialog.Builder(this).run {
                setView(userEditDialog.root)
                setNegativeButton("취소", null)
                setPositiveButton("확인") { _, _ ->
                    val pass = userEditDialog.userPass.text.toString()

                    // 비밀번호 확인 요청
                    MooDoClient.retrofit.checkPw(userId, pass).enqueue(object: retrofit2.Callback<Boolean> {
                        override fun onResponse(call: Call<Boolean>, response: Response<Boolean>) {
                            val checkPass = response.body() ?: false
                            if (checkPass) {
                                val intent = Intent(this@MainActivity_MyPage, MainActivity_UserEdit::class.java).apply {
                                    putExtra("userId", userId)
                                    putExtra("userName", binding.userName.text.toString())
                                }
                                // startActivity(intent)

                                activityUserUpdate.launch(intent)
                            } else {
                                AlertDialog.Builder(this@MainActivity_MyPage)
                                    .setMessage("비밀번호가 다릅니다.")
                                    .setPositiveButton("확인", null)
                                    .show()
                            }
                        }

                        override fun onFailure(call: Call<Boolean>, t: Throwable) {
                            Log.d("MooDoLog Pass", t.toString())
                        }
                    })
                }
                show()
            }
        }
        // 뒤로 가기
        binding.btnClose.setOnClickListener {
            setResult(RESULT_CANCELED, null)
            finish()
        }
    }

    private fun loadUserInfo(userId: String) {
        MooDoClient.retrofit.getUserInfo(userId).enqueue(object : retrofit2.Callback<MooDoUser>{
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
}