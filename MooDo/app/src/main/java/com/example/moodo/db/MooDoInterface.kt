package com.example.moodo.db

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface MooDoInterface {
    // 회원가입
    @POST("api/user/signup")
    fun singUp(@Body user:MooDoUser):Call<MooDoUser>

    // 로그인
    @POST("api/user/login")
    fun login(@Body user:MooDoUser):Call<MooDoUser>
}