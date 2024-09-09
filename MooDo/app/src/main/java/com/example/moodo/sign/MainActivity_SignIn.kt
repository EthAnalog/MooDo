package com.example.moodo.sign

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.moodo.mode.MainActivity_MooDo
import com.example.moodo.R
import com.example.moodo.databinding.ActivityMainSignInBinding
import com.example.moodo.db.MooDoClient
import com.example.moodo.db.MooDoUser
import retrofit2.Call
import retrofit2.Response

class MainActivity_SignIn : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val binding = ActivityMainSignInBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // 로그인 버튼
        binding.btnSignIn.setOnClickListener {
            val id = binding.edtId.text.toString()
            val pw = binding.edtPw.text.toString()

            val loginUser = MooDoUser(id, pw, null, null)
            MooDoClient.retrofit.login(loginUser).enqueue(object:retrofit2.Callback<MooDoUser>{
                override fun onResponse(call: Call<MooDoUser>, response: Response<MooDoUser>) {
                    if (response.isSuccessful) {
                        // main Page 이동
                        val intent = Intent(this@MainActivity_SignIn, MainActivity_MooDo::class.java)
                        intent.putExtra("id", id)
                        startActivity(intent)
                    }
                    // 로그인 실패
                    else {
                        AlertDialog.Builder(binding.root.context)
                            .setMessage("아이디와 비밀번호를 확인하세요.")
                            .setPositiveButton("확인", null)
                            .show()
                    }
                }

                override fun onFailure(call: Call<MooDoUser>, t: Throwable) {
                    Log.d("MooDoLog Login Fail", t.toString())
                }
            })
        }

        // 회원가입 버튼
        binding.btnSignUp.setOnClickListener {
            val intent = Intent(this@MainActivity_SignIn, MainActivity_SignUp::class.java)

            startActivity(intent)
        }

        // 뒤로가기 버튼
        binding.btnClose.setOnClickListener {
            setResult(RESULT_CANCELED, intent)
            finish()
        }
    }
}