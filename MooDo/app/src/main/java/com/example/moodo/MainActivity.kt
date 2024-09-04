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
import com.example.moodo.databinding.DialogLoginBinding
import com.example.moodo.databinding.DialogSingupBinding
import com.example.moodo.db.MooDoClient
import com.example.moodo.db.MooDoUser
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

        // 로그인 버튼 처리
        binding.singInBtn.setOnClickListener {
            val dialogBinding = DialogLoginBinding.inflate(layoutInflater)
            val loginDialog = AlertDialog.Builder(this).apply {
                setTitle("Sing In")
                setView(dialogBinding.root)
                setNegativeButton("닫기", null)
                setPositiveButton("로그인", null)
            }.create()

            loginDialog.show()

            loginDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {
                val id = dialogBinding.edtId.text.toString()
                val pw = dialogBinding.edtPw.text.toString()

                if (id.isNotEmpty() && pw.isNotEmpty()) {
                    Log.d("MooDoLog Login", "$id / $pw")
                    loginDialog.dismiss()  // 다이얼로그 닫기

                    // Client 서버로 로그인 확인
                    val loginUser = MooDoUser(id, pw, null, null)
                    MooDoClient.retrofit.login(loginUser).enqueue(object:retrofit2.Callback<MooDoUser>{
                        override fun onResponse(
                            call: Call<MooDoUser>,
                            response: Response<MooDoUser>
                        ) {
                            Log.d("MooDoLog login Success", response.body().toString())

                            // Calendar 로 넘어감
                            val intent = Intent(this@MainActivity, MainActivity_Calendar::class.java)
                            intent.putExtra("id", id)
                            startActivity(intent)
                        }

                        override fun onFailure(call: Call<MooDoUser>, t: Throwable) {
                            Log.d("MooDoLog login Fail", t.toString())
                        }

                    })
                } else {
                    AlertDialog.Builder(this)
                        .setMessage("아이디와 비밀번호를 모두 입력해주세요.")
                        .setPositiveButton("확인", null)
                        .show()
                }
            }
        }

        // 회원가입 버튼 처리
        binding.singUpBtn.setOnClickListener {
            val dialogBinding = DialogSingupBinding.inflate(layoutInflater)
            val singUpDialog = AlertDialog.Builder(this).apply {
                setTitle("Sing Up")
                setView(dialogBinding.root)
                setNegativeButton("닫기", null)
                setPositiveButton("회원가입", null)
            }.create()

            singUpDialog.show()

            singUpDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {
                val name = dialogBinding.edtName.text.toString()
                val id = dialogBinding.edtId.text.toString()
                val pw = dialogBinding.edtPw.text.toString()
                val pwCheck = dialogBinding.edtPwCheck.text.toString()
                val age = dialogBinding.edtAge.text.toString()

                if (name.isNotEmpty() && id.isNotEmpty() && pw.isNotEmpty() && pwCheck.isNotEmpty() && age.isNotEmpty()) {
                    if (pw.equals(pwCheck)) {
                        Log.d("MooDoLog SingUp", "${name} / ${id} / ${pw} / ${age}")

                        val singup = MooDoUser(id, pw, name, age)
                        MooDoClient.retrofit.singUp(singup).enqueue(object :retrofit2.Callback<MooDoUser>{
                            override fun onResponse(
                                call: Call<MooDoUser>,
                                response: Response<MooDoUser>
                            ) {
                                Log.d("MooDoLog SingUp Success", response.body().toString())

                                singUpDialog.dismiss()
                            }

                            override fun onFailure(call: Call<MooDoUser>, t: Throwable) {
                                Log.d("MooDoLog SingUp Fail", t.toString())
                            }
                        })
                    }
                    else {
                        AlertDialog.Builder(this)
                            .setMessage("비밀번호가 다릅니다.")
                            .setPositiveButton("확인", null)
                            .show()
                    }
                }
                else {
                    AlertDialog.Builder(this)
                        .setMessage("회원가입에 필요한 양식을 모두 입력해주세요.")
                        .setPositiveButton("확인", null)
                        .show()
                }
            }
        }
    }
}