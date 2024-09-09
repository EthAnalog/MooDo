package com.example.moodo.db

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query
import java.util.Optional

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

    // 사용자 정보 가져오기
    @GET("api/user/userInfo/{id}")
    fun getUserInfo(@Path("id") id:String):Call<MooDoUser>

    // 회원 to do list 조회
    @GET("api/todo/list/{userId}/{date}")
    fun getTodoList(@Path("userId") userId:String, @Path("date") date:String):Call<List<MooDoToDo>>

    // 회원 to do list 조회 + tdCheck = Y
    @GET("api/todo/listY/{userId}/{date}")
    fun getTodoListY(@Path("userId") userId: String, @Path("date") date:String):Call<List<MooDoToDo>>

    // 회원 to do list 조회 + tdCheck = N
    @GET("api/todo/listN/{userId}/{date}")
    fun getTodoListN(@Path("userId") userId: String, @Path("date") date:String):Call<List<MooDoToDo>>

    // to do list 저장
    @POST("api/todo/add/{userId}")
    fun addTodo(@Body todo:MooDoToDo, @Path("userId")userId: String):Call<MooDoToDo>

    // to do list 수정
    @PUT("api/todo/update/{id}")
    fun updateTodo(@Path("id")id:Long, @Body todo:MooDoToDo):Call<MooDoToDo>

    // to do list 삭제
    @DELETE("api/todo/delete/{id}")
    fun deleteTodo(@Path("id")id:Long):Call<Void>

    // 할 일 완료했는지 체크
    @PUT("api/todo/check/{id}")
    fun updateCheck(@Path("id") id:Long):Call<MooDoToDo>

    // 유저별 기분기록 조회
    @GET("api/mood/list/{userId}")
    fun userMoodList(@Path("userId") userId: String): Call<Map<String, Any>>

    // 특정 날짜 유저 기분 기록 조회
    @GET("api/mood/list/{userId}/{date}")
    fun userMoodList(@Path("userId") userId: String, @Path("date") date: String): Call<Optional<MooDoMode>>

    // 기분 추가
    @POST("api/mood/insert")
    fun insertMode(@Body mood:MooDoMode):Call<MooDoMode>

    // 기록된 기분이 있는지 조회
    @GET("api/mood/listCheck/{userId}/{date}")
    fun userMoodListCheck(@Path("userId") userId: String, @Path("date") date:String):Call<Boolean>
}