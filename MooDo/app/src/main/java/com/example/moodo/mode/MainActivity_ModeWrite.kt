package com.example.moodo.mode

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.moodo.R
import com.example.moodo.databinding.ActivityMainModeWriteBinding
import com.example.moodo.db.MooDoClient
import com.example.moodo.db.MooDoMode
import com.example.moodo.db.MooDoUser
import retrofit2.Call
import retrofit2.Response
import java.text.SimpleDateFormat

class MainActivity_ModeWrite : AppCompatActivity() {
    lateinit var binding:ActivityMainModeWriteBinding
    var user:MooDoUser? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainModeWriteBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // userId
        val userId = intent.getStringExtra("userId")
        val selectDate = intent.getStringExtra("selectDate")

        Log.d("MooDoLog Mode", userId.toString())
        Log.d("MooDoLog Mode", selectDate.toString())

        // 사용자 정보 가져오기
        loadUserInfo(userId!!)

        val inputFormat = SimpleDateFormat("yyyy-MM-dd")
        val parsedDate = inputFormat.parse(selectDate.toString())
        val outputFormat = SimpleDateFormat("yyyy년 MM월 dd일")
        val formattedDate = outputFormat.format(parsedDate)

        binding.selectDay.text = formattedDate

        val edtWrite = binding.writeDaily
        var moodInt = 0
        var weather = 0

        // 기분
        val moodButtons = listOf(
            binding.btnMood1,
            binding.btnMood2,
            binding.btnMood3,
            binding.btnMood4,
            binding.btnMood5
        )

        moodButtons.forEachIndexed { index, button ->
            button.setOnClickListener {
                Toast.makeText(this, "선택한 기분: ${index + 1}", Toast.LENGTH_SHORT).show()
                moodInt = index + 1
            }
        }
        // 날씨
        val weatherButtons = listOf(
            binding.btnWeather1,
            binding.btnWeather2,
            binding.btnWeather3,
            binding.btnWeather4
        )

        weatherButtons.forEachIndexed { index, button ->
            button.setOnClickListener {
                Toast.makeText(this, "선택한 날씨: ${index + 1}", Toast.LENGTH_SHORT).show()
                weather = index + 1
            }
        }

        // 저장 버튼
        binding.btnSave.setOnClickListener {
            if (moodInt !=  0 && weather != 0 && edtWrite.text.isNotEmpty() && user!=null) {
                val edtTxt = edtWrite.text
                val mode = MooDoMode(0, user!!, moodInt, selectDate!!, weather, edtTxt.toString())
                MooDoClient.retrofit.insertMode(mode).enqueue(object :retrofit2.Callback<MooDoMode>{
                    override fun onResponse(call: Call<MooDoMode>, response: Response<MooDoMode>) {
                        if (response.isSuccessful){
                            Log.d("MooDoLog ModeIn", response.body().toString())
                        }
                    }

                    override fun onFailure(call: Call<MooDoMode>, t: Throwable) {
                        Log.d("MooDoLog ModeIn F", t.toString())
                    }
                })
                setResult(RESULT_OK, null)
                finish()
            }
            else {
                AlertDialog.Builder(binding.root.context)
                    .setMessage("기분과 날씨, 한 줄 일기를 모두 작성해주세요.")
                    .setPositiveButton("확인", null)
                    .show()
            }
        }

        // 뒤로 가기
        binding.btnClose.setOnClickListener {
            setResult(RESULT_CANCELED, null)
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