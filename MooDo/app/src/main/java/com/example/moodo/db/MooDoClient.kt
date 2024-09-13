package com.example.moodo.db

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

// 각자 포트번호로 바꾸셔야 합니다
object MooDoClient {

    val retrofit:MooDoInterface = Retrofit.Builder()
        .baseUrl("http://10.100.105.240:8899/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(MooDoInterface::class.java)
}