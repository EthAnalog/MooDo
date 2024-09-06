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

// 커스텀 JsonDeserializer: LocalDateTime을 Date로 변환
class LocalDateTimeDeserializer : JsonDeserializer<Date> {
    private val dateTimeFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())

    override fun deserialize(json: JsonElement?, typeOfT: Type?, context: JsonDeserializationContext?): Date {
        return dateTimeFormat.parse(json?.asString)
    }
}

// 커스텀 JsonDeserializer: LocalDate를 Date로 변환
class LocalDateDeserializer : JsonDeserializer<Date> {
    private val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

    override fun deserialize(json: JsonElement?, typeOfT: Type?, context: JsonDeserializationContext?): Date {
        return dateFormat.parse(json?.asString)
    }
}
        // 각자 포트번호로 바꾸셔야 합니다
object MooDoClient {
    // 서버에서 받은 날짜를 Date 타입으로 변환
//    val gson = GsonBuilder()
//        .setDateFormat("yyyy-MM-dd'T'HH:mm:ss")
//        .create()

    val gson = GsonBuilder()
        .registerTypeAdapter(Date::class.java, LocalDateTimeDeserializer())  // LocalDateTime 처리
        .registerTypeAdapter(Date::class.java, LocalDateDeserializer())      // LocalDate 처리
        .create()

    val retrofit:MooDoInterface = Retrofit.Builder()
        .baseUrl("http://10.100.105.204:8899/")
        .addConverterFactory(GsonConverterFactory.create(gson))
        .build()
        .create(MooDoInterface::class.java)
}