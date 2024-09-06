package com.example.moodo.db

import com.google.gson.GsonBuilder
import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.lang.reflect.Type
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

// 각자 포트번호로 바꾸셔야 합니다
object MooDoClient {
    val retrofit:MooDoInterface = Retrofit.Builder()
        .baseUrl("http://10.100.105.204:8899/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(MooDoInterface::class.java)
}