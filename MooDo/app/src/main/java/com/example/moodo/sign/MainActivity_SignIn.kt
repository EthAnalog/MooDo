package com.example.moodo.sign

import android.content.Context
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
import com.kakao.sdk.user.UserApiClient
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

            val loginUser = MooDoUser(id, pw, null, null, null)
            MooDoClient.retrofit.login(loginUser).enqueue(object:retrofit2.Callback<MooDoUser>{
                override fun onResponse(call: Call<MooDoUser>, response: Response<MooDoUser>) {
                    if (response.isSuccessful) {
                        // main Page 이동
                        Log.d("MooDoLog UserInfo", response.body().toString())
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

        // 카카오 로그인 버튼 클릭 처리
        binding.btnKakaoSignIn.setOnClickListener {
            KakaoLogin(this)
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

    // 카카오 로그인 함수
    fun KakaoLogin(_context: Context) {
        // 카카오톡으로 로그인 시도
        UserApiClient.instance.loginWithKakaoTalk(_context) { token, error ->
            if (error != null) {
                Log.e("Kakao", "카카오톡으로 로그인 실패", error)

                // 카카오톡이 설치되어 있지 않을 때 카카오 계정으로 로그인 시도
                UserApiClient.instance.loginWithKakaoAccount(_context) { accountToken, accountError ->
                    if (accountError != null) {
                        Log.e("Kakao", "카카오 계정으로 로그인 실패", accountError)
                    } else if (accountToken != null) {
                        Log.i("Kakao", "카카오 계정으로 로그인 성공 ${accountToken.accessToken}")
                        handleKakaoLoginSuccess(accountToken.accessToken)
                    }
                }
            } else if (token != null) {
                Log.i("Kakao", "카카오톡으로 로그인 성공 ${token.accessToken}")
                handleKakaoLoginSuccess(token.accessToken)
            }
        }
    }

    // 로그인 성공 후 처리
    private fun handleKakaoLoginSuccess(accessToken: String) {
        UserApiClient.instance.me { user, error ->
            if (error != null) {
                Log.e("Kakao", "사용자 정보 요청 실패", error)
            } else if (user != null) {
                Log.i("Kakao", "사용자 정보 요청 성공: ${user.kakaoAccount?.profile?.nickname}")

                // 로그인 성공 시 다음 화면으로 이동
                val intent = Intent(this@MainActivity_SignIn, MainActivity_MooDo::class.java)
                intent.putExtra("id", user.id.toString()) // 사용자 아이디 또는 이메일 정보 넘기기
                startActivity(intent)
            }
        }
    }
}