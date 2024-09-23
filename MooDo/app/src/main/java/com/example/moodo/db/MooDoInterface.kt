package com.example.moodo.db

import okhttp3.MultipartBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query
import java.util.Optional

interface MooDoInterface {
    // 회원가입
    @POST("api/user/signup")
    fun signUp(@Body user:MooDoUser):Call<MooDoUser>

    // 로그인
    @POST("api/user/login")
    fun login(@Body user:MooDoUser):Call<MooDoUser>

    // 아이디 중복 확인
    @GET("api/user/check-id/{id}")
    fun checkId(@Path("id") id:String):Call<Boolean>

    // 사용자 정보 가져오기
    @GET("api/user/userInfo/{id}")
    fun getUserInfo(@Path("id") id:String):Call<MooDoUser>

    // 프로필 사진 업로드+수정
    @Multipart
    @POST("api/user/uploadProfilePicture/{userId}")
    fun uploadProfilePicture(@Path("userId") userId: String, @Part file: MultipartBody.Part):Call<ResponseBody>

    // 프로필 사진 삭제
    @DELETE("api/user/deleteProfilePicture/{userId}")
    fun deleteProfilePicture(@Path("userId") userId: String):Call<ResponseBody>

    // 유저 사진 가져오기
    @GET("api/user/userProfile/{id}")
    fun getUserImg(@Path("id") id: String): Call<ResponseBody>

    // 비밀번호 확인
    @POST("api/user/check-pw/{id}/{pass}")
    fun checkPw(@Path("id") id:String, @Path("pass") pass:String):Call<Boolean>

    // 사용자 정보 수정 (비밀번호 + 생일)
    @PUT("api/user/changeUser/{id}")
    fun changeUser(@Path("id") id:String, @Query("pass") pass:String, @Query("age") age:String): Call<Void>

    // 회원 탈퇴
    @DELETE("api/user/deleteUser/{id}")
    fun deleteUser(@Path("id") id:String):Call<Void>

    // 회원 to do list 조회
    @GET("api/todo/list/{userId}/{date}")
    fun getTodoList(@Path("userId") userId:String, @Path("date") date:String):Call<List<MooDoToDo>>

    // 캘린더 표시할 to do list 개수 조회
    @GET("api/todo/count/day/{userId}/{date}")
    fun getTodoCountForDay(@Path("userId") userId: String, @Path("date") date:String):Call<Int>

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

    // 한 달 동안 기록된 계획 개수
    @GET("api/todo/count/{userId}/{year}/{month}")
    fun getTodoCountForMonth(@Path("userId") userId: String, @Path("year") year: Int, @Path("month") month:Int): Call<Int>

    // 한 달 동안 완료된 계획 개수 (tdCheck가 'Y')
    @GET("api/todo/completed/count/{userId}/{year}/{month}")
    fun getCompletedTodoCountForMonth(@Path("userId") userId: String, @Path("year") year:Int, @Path("month") month: Int): Call<Int>

    // 검색해서 할 일 조회
    @GET("api/todo/search/{userId}")
    fun searchTodos(@Path("userId") userId: String, @Query("keyword") keyword: String): Call<List<MooDoToDo>>

    // mode
    // 전체 일기 list 및 가장 많은 기분 값
    @GET("api/mood/list/{userId}")
    fun userMoodList(@Path("userId") userId: String): Call<Map<String, Any>>

    // 특정 날짜 일기 조회
    @GET("api/mood/list/{userId}/{date}")
    fun getUserDayMood(@Path("userId") userId:String, @Path("date") date:String):Call<List<MooDoMode>>

    // 특정 날짜 기분값 조회
    @GET("api/mood/list/mdMode/{userId}/{date}")
    fun getMdMode(@Path("userId") userId: String, @Path("date") date: String): Call<Int>

    // 한달 동안 일기 조회
    @GET("api/mood/list/month/{userId}/{year}/{month}")
    fun getUserMoodListByMonth(@Path("userId") userId:String, @Path("year") year:Int, @Path("month") month:Int):Call<List<MooDoMode>>

    // 한 달 동안 기록된 가장 많은 감정
    @GET("api/mood/moreMood/{userId}/{year}/{month}")
    fun getUserMoodNumByMonth(@Path("userId") userId:String, @Path("year") year:Int, @Path("month") month: Int):Call<Int>

    // 일기 추가
    @POST("api/mood/insert")
    fun insertMode(@Body mood:MooDoMode):Call<MooDoMode>

    // 기록된 일기가 있는지 조회
    @GET("api/mood/listCheck/{userId}/{date}")
    fun userMoodListCheck(@Path("userId") userId: String, @Path("date") date:String):Call<Boolean>

    // 기록된 일기 수정
    @PUT("api/mood/update/{id}")
    fun update(@Path("id") id:Long, @Body mood: MooDoMode):Call<Optional<MooDoMode>>

    // 일기 삭제
    @DELETE("api/mood/delete/{id}")
    fun delete(@Path("id") id:Long):Call<Void>

    // 달력 일정 및 감정 조회
    @GET("api/calendar/count/day/{userId}/{date}")
    fun getDay(@Path("userId") userId: String, @Path("date") date:String):Call<MoodoCalendar>

    // 공휴일 정보
    @GET("api/calendar/getHoliday/{date}")
    fun getHoliday(@Path("date") date:String):Call<List<MooDoHoliday>>
}