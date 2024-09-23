package com.example.moodo

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.moodo.databinding.ActivityMainBinding
import com.example.moodo.db.MooDoClient
import com.example.moodo.db.MooDoUser
import com.example.moodo.mode.MainActivity_MooDo
import com.example.moodo.sign.MainActivity_SignIn
import com.example.moodo.sign.MainActivity_SignUp
import com.kakao.sdk.common.util.Utility
import retrofit2.Call
import retrofit2.Response

class MainActivity : AppCompatActivity() {
    lateinit var binding:ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        var keyHash = Utility.getKeyHash(this)
        Log.i("Hash", "keyHash: $keyHash")

        // 로그인 버튼 처리
        binding.signInBtn.setOnClickListener {
            val intent = Intent(this@MainActivity, MainActivity_SignIn::class.java)
            startActivity(intent)
        }

        // 회원가입 버튼 처리
        binding.signUpBtn.setOnClickListener {
            val intent = Intent(this@MainActivity, MainActivity_SignUp::class.java)

            startActivity(intent)
        }
    }
}