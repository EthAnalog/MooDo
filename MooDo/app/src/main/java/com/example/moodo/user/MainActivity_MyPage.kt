package com.example.moodo.user

import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.moodo.R
import com.example.moodo.databinding.ActivityMainMyPageBinding
import com.example.moodo.db.MooDoClient
import com.example.moodo.db.MooDoUser
import com.kakao.sdk.user.model.User
import retrofit2.Call
import retrofit2.Response

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

        loadUserInfo(userId!!)
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