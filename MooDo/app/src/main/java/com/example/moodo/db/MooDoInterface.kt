package com.example.moodo.db

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface MooDoInterface {
    // 회원가입
    @POST("api/user/signup")
    fun singUp(@Body user:MooDoUser):Call<MooDoUser>

    // 로그인
    @POST("api/user/login")
    fun login(@Body user:MooDoUser):Call<MooDoUser>

    // 아이디 중복 확인
    @GET("api/user/check-id/{id}")
    fun checkId(@Path("id") id:String):Call<Boolean>

    // 회원 to do list 조회
    @GET("api/todo/list/{userId}/{date}")
    fun getTodoList(@Path("userId") userId:String, @Path("date") date:String):Call<List<MooDoToDo>>
}