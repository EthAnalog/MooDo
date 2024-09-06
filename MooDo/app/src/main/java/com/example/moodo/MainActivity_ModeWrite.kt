package com.example.moodo

import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.moodo.databinding.ActivityMainModeWriteBinding
import java.text.SimpleDateFormat

class MainActivity_ModeWrite : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val binding = ActivityMainModeWriteBinding.inflate(layoutInflater)
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

        val inputFormat = SimpleDateFormat("yyyy-MM-dd")
        val parsedDate = inputFormat.parse(selectDate.toString())

        val outputFormat = SimpleDateFormat("yyyy년 MM월 dd일")
        val formattedDate = outputFormat.format(parsedDate)

        binding.selectDay.text = formattedDate

        // 뒤로 가기
        binding.btnClose.setOnClickListener {
            setResult(RESULT_CANCELED, null)
            finish()
        }
    }
}