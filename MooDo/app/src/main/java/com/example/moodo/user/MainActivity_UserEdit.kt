package com.example.moodo.user

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.moodo.R
import com.example.moodo.databinding.ActivityMainUserEditBinding
import com.example.moodo.db.MooDoClient
import com.example.moodo.db.MooDoUser
import retrofit2.Call
import retrofit2.Response

class MainActivity_UserEdit : AppCompatActivity() {
    lateinit var binding: ActivityMainUserEditBinding
    var user:MooDoUser? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainUserEditBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val userName = intent.getStringExtra("userName").toString()
        val userId = intent.getStringExtra("userId").toString()

        loadUserInfo(userId)

        val txtAge = binding.edtAge
        val txtPw = binding.edtPw
        val txtCheckPw = binding.edtPwCheck

        var checkPw = true
        var checkPwSame = false
        var checkAge = true

        if (userName != null) {
            binding.edtName.setText(userName)
            binding.edtId.setText(userId)
        }

        // 비밀번호 길이 및 비밀번호 확인 부분
        txtPw.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun afterTextChanged(p0: Editable?) {
                if (txtPw.length() > 3) {
                    binding.checkPw.text = "사용 가능한 비밀번호 입니다."
                    binding.checkPw.setTextColor(Color.rgb(69, 69, 69))
                    checkPw = true
                }
                else {
                    binding.checkPw.text = "영문, 숫자 4~20자 이내로 입력하세요."
                    binding.checkPwSame.setTextColor(Color.rgb(255, 82, 82))
                    checkPw = false
                }

                // 비밀번호 확인 필드와의 일치 여부 체크
                if (txtPw.text.toString() == txtCheckPw.text.toString()) {
                    binding.checkPwSame.text = "비밀번호가 일치합니다."
                    binding.checkPwSame.setTextColor(Color.rgb(69, 69, 69))
                    checkPwSame = true
                } else {
                    binding.checkPwSame.text = "비밀번호가 일치하지 않습니다."
                    binding.checkPwSame.setTextColor(Color.rgb(255, 82, 82))
                    checkPwSame = false
                }
            }
        })
        // 비밀번호 확인 부분
        txtCheckPw.addTextChangedListener(object :TextWatcher{
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun afterTextChanged(p0: Editable?) {
                if (txtPw.text.toString() == txtCheckPw.text.toString()) {
                    binding.checkPwSame.text = "비밀번호가 일치합니다."
                    binding.checkPwSame.setTextColor(Color.rgb(69, 69, 69))
                    checkPwSame = true
                }
                else {
                    binding.checkPwSame.text = "비밀번호가 일치하지 않습니다."
                    binding.checkPwSame.setTextColor(Color.rgb(255, 82, 82))
                    checkPwSame = false
                }
            }
        })
        // 생년월일
        txtAge.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                // 생년월일 입력 시 포맷에 맞게 / 삽입
                val input = s.toString()
                val length = input.length

                val textPattern = StringBuilder(input)

                // 직접 '/' 삽입 없이 숫자만 입력하도록 설정
                var i = 0
                while (i < textPattern.length) {
                    if (textPattern[i] == '/') {
                        textPattern.deleteCharAt(i)
                    } else {
                        i++
                    }
                }
                // 자동 '/' 삽입할 구간 설정
                if (length > 6) {
                    textPattern.insert(4, '/')
                    if (length > 8) {
                        textPattern.insert(7, '/')
                    }
                } else if (length > 4) {
                    textPattern.insert(4, '/')
                }
                // 10자리 초과하지 않도록 take(10) 삽입
                val inputText = textPattern.toString().take(10)
                // 날짜 수정입력 시 무한루프 방지
                if (txtAge.text.toString() != inputText) {
                    txtAge.removeTextChangedListener(this)
                    txtAge.text = Editable.Factory.getInstance().newEditable(inputText)
                    txtAge.setSelection(inputText.length)
                    txtAge.addTextChangedListener(this)
                }

                val datePattern = "^\\d{4}/\\d{2}/\\d{2}$"  // YYYY/MM/DD 형식
                val inputDate = txtAge.text.toString()

                if (inputDate.matches(datePattern.toRegex())) {
                    binding.checkAge.text = ""
                    binding.checkAge.setTextColor(Color.rgb(69, 69, 69))
                    checkAge = true

                } else {
                    binding.checkAge.text = "YYYY/MM/DD 형식으로 입력하세요."
                    binding.checkAge.setTextColor(Color.rgb(255, 82, 82))
                    checkAge = false
                }
            }
        })

        // 수정 버튼 클릭
        binding.btnUpdate.setOnClickListener {
            if (checkPw == true && checkAge == true && checkPwSame == true) {
                val pass = txtPw.text.toString()
                val age = txtAge.text.toString()

                val intent = Intent()
                intent.putExtra("pass", pass)
                intent.putExtra("age", age)

                MooDoClient.retrofit.changeUser(userId, pass, age).enqueue(object :retrofit2.Callback<Void>{
                    override fun onResponse(call: Call<Void>, response: Response<Void>) {
                        if(response.isSuccessful) {
                            Log.d("MooDoLog Userch", "chUser: $user")
                            setResult(RESULT_OK, intent)
                            finish()
                        } else {
                            Log.d("MooDoLog Userch", "Error: ${response.code()}-${response.message()}")
                        }
                    }

                    override fun onFailure(call: Call<Void>, t: Throwable) {
                        Log.d("MooDoLog UserCh fail", t.toString())
                    }

                })

            } else {
                AlertDialog.Builder(this)
                    .setMessage("회원정보 수정을 위해 양식에 맞춰 입력해주세요.")
                    .setPositiveButton("확인", null)
                    .show()
            }
        }
    }


    private fun loadUserInfo(userId: String) {
        MooDoClient.retrofit.getUserInfo(userId).enqueue(object : retrofit2.Callback<MooDoUser>{
            override fun onResponse(call: Call<MooDoUser>, response: Response<MooDoUser>) {
                if (response.isSuccessful) {
                    user = response.body()
                    binding.edtAge.setText(user!!.age.toString())
                    binding.edtPw.setText(user!!.pass.toString())
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