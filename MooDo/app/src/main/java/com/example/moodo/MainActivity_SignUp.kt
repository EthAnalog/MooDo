package com.example.moodo

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
import com.example.moodo.databinding.ActivityMainSingUpBinding
import com.example.moodo.db.MooDoClient
import com.example.moodo.db.MooDoUser
import retrofit2.Call
import retrofit2.Response

class MainActivity_SignUp : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val binding = ActivityMainSingUpBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        var checkId = false
        var checkPw = false
        var checkPwSame = false
        var checkAge = false

        val txtId = binding.edtId
        val txtPw = binding.edtPw
        val txtCheckPw = binding.edtPwCheck
        val txtAge = binding.edtAge

        // 아이디 중복 및 비밀번호 확인
        txtId.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun afterTextChanged(p0: Editable?) {
                if (txtId.length() > 3) {
                    val id = txtId.text.toString()
                    MooDoClient.retrofit.checkId(id).enqueue(object:retrofit2.Callback<Boolean>{
                        override fun onResponse(call: Call<Boolean>, response: Response<Boolean>) {
                            val idState = response.body()
                            if (idState == true) {
                                binding.checkId.text = "사용 가능한 아이디입니다."
                                binding.checkId.setTextColor(Color.rgb(69, 69, 69))
                                checkId = true
                            }
                            else {
                                binding.checkId.text = "사용할 수 없는 아이디입니다."
                                binding.checkId.setTextColor(Color.rgb(255, 82, 82))
                                checkId = false
                            }
                        }

                        override fun onFailure(call: Call<Boolean>, t: Throwable) {
                            Log.d("MooDoLog Id checkFail", t.toString())
                        }

                    })
                }
                else {
                    binding.checkId.text = "영문, 숫자 4~20자 이내로 입력하세요."
                    binding.checkId.setTextColor(Color.rgb(255, 82, 82))
                    checkId = false
                }
            }
        })

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

        // 회원가입 버튼
        binding.btnSingUp.setOnClickListener {
            if (checkId == true && checkPw == true && checkPwSame == true && checkAge == true && binding.edtName.text.isNotEmpty()) {
                val singUp = MooDoUser(txtId.text.toString(), txtPw.text.toString(), binding.edtName.text.toString(), txtAge.text.toString())
                MooDoClient.retrofit.singUp(singUp).enqueue(object:retrofit2.Callback<MooDoUser>{
                    override fun onResponse(call: Call<MooDoUser>, response: Response<MooDoUser>) {
                        setResult(RESULT_OK, intent)
                        finish()
                    }

                    override fun onFailure(call: Call<MooDoUser>, t: Throwable) {
                        Log.d("MooDoLog SingUp Fail", t.toString())
                    }
                })
            }
            else {
                AlertDialog.Builder(this)
                    .setMessage("회원가입 양식을 확인해주세요.")
                    .setPositiveButton("확인", null)
                    .show()
            }
        }

        // 뒤로가기 버튼
        binding.btnClose.setOnClickListener {
            setResult(RESULT_CANCELED, intent)
            finish()
        }
    }
}