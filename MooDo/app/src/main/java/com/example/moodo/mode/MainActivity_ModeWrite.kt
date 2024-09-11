package com.example.moodo.mode

import android.animation.ObjectAnimator
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
import java.util.Optional

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
        val stats = intent.getStringExtra("stats")

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

        // 기분 버튼 배열
        val moodButtons = listOf(
            binding.btnMood1,
            binding.btnMood2,
            binding.btnMood3,
            binding.btnMood4,
            binding.btnMood5
        )
        // 날씨 버튼 배열
        val weatherButtons = listOf(
            binding.btnWeather1,
            binding.btnWeather2,
            binding.btnWeather3,
            binding.btnWeather4
        )

        moodButtons.forEachIndexed { index, button ->
            button.setOnClickListener {
                // 선택된 버튼 크기 크게
                ObjectAnimator.ofFloat(button, "scaleX", 1.1f).apply {
                    duration = 300
                    start()
                }
                ObjectAnimator.ofFloat(button, "scaleY", 1.1f).apply {
                    duration = 300
                    start()
                }

                //  나머지 버튼 크기 작게
                moodButtons.forEachIndexed{ i, otherBtn ->
                    if (i != index) {
                        ObjectAnimator.ofFloat(otherBtn, "scaleX", 0.9f).apply {
                            duration = 300
                            start()
                        }
                        ObjectAnimator.ofFloat(otherBtn, "scaleY", 0.9f).apply {
                            duration = 300
                            start()
                        }
                    }
                }
                moodInt = index + 1
            }
        }
        weatherButtons.forEachIndexed { index, button ->
            button.setOnClickListener {
                // 선택된 버튼 크기 크게
                ObjectAnimator.ofFloat(button, "scaleX", 1.1f).apply {
                    duration = 300
                    start()
                }
                ObjectAnimator.ofFloat(button, "scaleY", 1.1f).apply {
                    duration = 300
                    start()
                }

                //  나머지 버튼 크기 작게
                weatherButtons.forEachIndexed{ i, otherBtn ->
                    if (i != index) {
                        ObjectAnimator.ofFloat(otherBtn, "scaleX", 0.9f).apply {
                            duration = 300
                            start()
                        }
                        ObjectAnimator.ofFloat(otherBtn, "scaleY", 0.9f).apply {
                            duration = 300
                            start()
                        }
                    }
                }
                weather = index + 1
            }
        }

        if (stats == "insert") {
            binding.btnSave.text = "저장"
        }
        else if (stats == "update") {
            binding.btnSave.text = "수정"
            val diary = intent.getStringExtra("diary")
            moodInt = intent.getIntExtra("mdMode", 0)
            weather = intent.getIntExtra("weather", 0)
            edtWrite.setText(diary)

            // 기분 버튼 크기 변경 (선택된 버튼 크기 증가)
            if (moodInt > 0) {
                for (i in 0..4) {
                    val selectedButton = moodButtons[i]
                    if (i == (moodInt-1)) {
                        ObjectAnimator.ofFloat(selectedButton, "scaleX", 1.1f).apply {
                            duration = 300
                            start()
                        }
                        ObjectAnimator.ofFloat(selectedButton, "scaleY", 1.1f).apply {
                            duration = 300
                            start()
                        }
                    }
                    else {
                        ObjectAnimator.ofFloat(selectedButton, "scaleX", 0.9f).apply {
                            duration = 300
                            start()
                        }
                        ObjectAnimator.ofFloat(selectedButton, "scaleY", 0.9f).apply {
                            duration = 300
                            start()
                        }
                    }
                }
            }
            // 날씨 버튼 크기 변경 (선택된 버튼 크기 증가)
            if (weather > 0) {
                for (i in 0..3) {
                    val selectedButton = weatherButtons[i]
                    if (i == (weather-1)) {
                        ObjectAnimator.ofFloat(selectedButton, "scaleX", 1.1f).apply {
                            duration = 300
                            start()
                        }
                        ObjectAnimator.ofFloat(selectedButton, "scaleY", 1.1f).apply {
                            duration = 300
                            start()
                        }
                    }
                    else {
                        ObjectAnimator.ofFloat(selectedButton, "scaleX", 0.9f).apply {
                            duration = 300
                            start()
                        }
                        ObjectAnimator.ofFloat(selectedButton, "scaleY", 0.9f).apply {
                            duration = 300
                            start()
                        }
                    }
                }
            }
        }

        // 저장 버튼
        binding.btnSave.setOnClickListener {
            if (binding.btnSave.text == "저장") {
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
                    val intent = Intent().apply {
                        putExtra("update", true)
                    }
                    setResult(RESULT_OK, intent)
                    finish()
                }
                else {
                    AlertDialog.Builder(binding.root.context)
                        .setMessage("기분과 날씨, 한 줄 일기를 모두 작성해주세요.")
                        .setPositiveButton("확인", null)
                        .show()
                }
            }
            else if (binding.btnSave.text == "수정") {
                if (moodInt !=  0 && weather != 0 && edtWrite.text.isNotEmpty() && user!=null) {
                    val mdDaily = edtWrite.text.toString()
                    val intent = Intent().apply {
                        putExtra("update", true)
                        putExtra("mdMode", moodInt)
                        putExtra("weather", weather)
                        putExtra("mdDaily", mdDaily)
                    }
                    setResult(RESULT_OK, intent)
                    finish()
                }
                else {
                    AlertDialog.Builder(binding.root.context)
                        .setMessage("기분과 날씨, 한 줄 일기를 모두 작성해주세요.")
                        .setPositiveButton("확인", null)
                        .show()
                }
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