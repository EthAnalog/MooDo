package com.example.moodo.user

import android.content.DialogInterface
import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.moodo.MainActivity
import com.example.moodo.MainActivity_Statis
import android.Manifest
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.content.FileProvider
import com.example.moodo.R
import com.example.moodo.databinding.ActivityMainMyPageBinding
import com.example.moodo.databinding.DialogUserEditPassBinding
import com.example.moodo.db.MooDoClient
import com.example.moodo.db.MooDoUser
import com.example.moodo.mode.MainActivity_MooDo
import com.example.moodo.mode.MainActivity_Mood_Calendar
import com.kakao.sdk.user.model.User
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Response
import java.io.File
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class MainActivity_MyPage : AppCompatActivity() {
    private lateinit var binding: ActivityMainMyPageBinding
    private var user:MooDoUser?=null
    private var currentPhotoPath: String = ""

    private val pickImage = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        uri?.let {
            val file = File(getPathFromUri(uri) ?: return@let)
            val requestBody = file.asRequestBody("image/*".toMediaTypeOrNull())
            val part = MultipartBody.Part.createFormData("file", file.name, requestBody)
            uploadProfilePicture(user!!.id, part)
            loadProfilePicture(user!!.id)
        }
    }

    private val takePicture = registerForActivityResult(ActivityResultContracts.TakePicture()) { success ->
        if (success) {
            val file = File(currentPhotoPath)
            val requestBody = file.asRequestBody("image/*".toMediaTypeOrNull())
            val part = MultipartBody.Part.createFormData("file", file.name, requestBody)
            uploadProfilePicture(user!!.id, part)
            loadProfilePicture(user!!.id)
        } else {
            Toast.makeText(this@MainActivity_MyPage, "사진 촬영 실패", Toast.LENGTH_SHORT).show()
        }
    }



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
        // 오늘 날짜
        val calendar = Calendar.getInstance()
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        // 현재 날짜를 문자열로 변환
        val selectDate = dateFormat.format(calendar.time)

        loadUserInfo(userId!!)
        loadProfilePicture(userId)

        // profile
        // 사진 수정
        binding.btnImgUpdate.setOnClickListener {
            chooseImage()
        }
        // 사진 삭제
        binding.btnImgDelete.setOnClickListener {
            deleteProfilePicture(userId)
        }

        // menu - moodo
        // 이달의 기록
        binding.btnMoodStats.setOnClickListener {
            val intent = Intent(this@MainActivity_MyPage, MainActivity_Statis::class.java)
            intent.putExtra("userId", userId)
            intent.putExtra("selectDate", selectDate)

            startActivity(intent)

        }
        // 캘린더 이동
        binding.btnCalendar.setOnClickListener {
            val intent = Intent(this@MainActivity_MyPage, MainActivity_MooDo::class.java)

            intent.putExtra("id", userId)

            startActivity(intent)
        }
        // 감정 기록 남기기
        binding.btnMoodTracker.setOnClickListener {
            val intent = Intent(this@MainActivity_MyPage, MainActivity_Mood_Calendar::class.java)
            intent.putExtra("userId", userId)

            startActivity(intent)
        }

        // menu = 회원
        // 로그아웃
        binding.btnLogout.setOnClickListener {
            AlertDialog.Builder(this).apply {
                setTitle("로그아웃")
                setMessage("로그아웃 하시겠습니까?")
                setPositiveButton("확인") { _,_ ->
                    val intent = Intent(this@MainActivity_MyPage, MainActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                    finish()
                }
                setNegativeButton("취소", null)
                show()
            }
        }

        // 회원정보 변경 intent 처리
        val activityUserUpdate = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == RESULT_OK) {
                val pass = it.data?.getStringExtra("pass").toString()
                val age = it.data?.getStringExtra("age").toString()

                Log.d("MooDoLog UserUp", age)

                MooDoClient.retrofit.changeUser(userId, pass, age).enqueue(object:retrofit2.Callback<Void>{
                    override fun onResponse(call: Call<Void>, response: Response<Void>) {
                        Log.d("MooDoLog UserCh", response.body().toString())
                        loadUserInfo(userId)
                    }
                    override fun onFailure(call: Call<Void>, t: Throwable) {
                        Log.d("MooDoLog UserCh fail", t.toString())
                    }
                })
            }
        }
        // 회원정보 변경
        binding.btnEditProfile.setOnClickListener {
            val userEditDialog = DialogUserEditPassBinding.inflate(layoutInflater)

            AlertDialog.Builder(this).run {
                setView(userEditDialog.root)
                setNegativeButton("취소", null)
                setPositiveButton("확인") { _, _ ->
                    val pass = userEditDialog.userPass.text.toString()

                    // 비밀번호 확인 요청
                    MooDoClient.retrofit.checkPw(userId, pass).enqueue(object: retrofit2.Callback<Boolean> {
                        override fun onResponse(call: Call<Boolean>, response: Response<Boolean>) {
                            val checkPass = response.body() ?: false
                            if (checkPass) {
                                val intent = Intent(this@MainActivity_MyPage, MainActivity_UserEdit::class.java).apply {
                                    putExtra("userId", userId)
                                    putExtra("userName", binding.userName.text.toString())
                                }
                                // startActivity(intent)

                                activityUserUpdate.launch(intent)
                            } else {
                                AlertDialog.Builder(this@MainActivity_MyPage)
                                    .setMessage("비밀번호가 다릅니다.")
                                    .setPositiveButton("확인", null)
                                    .show()
                            }
                        }

                        override fun onFailure(call: Call<Boolean>, t: Throwable) {
                            Log.d("MooDoLog Pass", t.toString())
                        }
                    })
                }
                show()
            }
        }
        // 뒤로 가기
        binding.btnClose.setOnClickListener {
            val update = true
            intent.putExtra("update", update)
            setResult(RESULT_OK, intent)
            finish()
        }

        //탈퇴 버튼
        binding.btnDeleteAccount.setOnClickListener {
            AlertDialog.Builder(this).apply {
                setTitle("회원 탈퇴")
                setMessage("탈퇴 시 모든 데이터가 삭제됩니다. 탈퇴 하시겠습니까?")
                setPositiveButton("확인") {_,_ ->
                    MooDoClient.retrofit.deleteUser(userId).enqueue(object : retrofit2.Callback<Void>{
                        override fun onResponse(call: Call<Void>, response: Response<Void>) {
                            if(response.isSuccessful){
                                Log.d("MooDoLog DeleteUser", "deleted User : $user")
                                AlertDialog.Builder(this@MainActivity_MyPage)
                                    .setMessage("탈퇴가 완료되었습니다.")
                                    .setPositiveButton("확인"){_,_ ->
                                        val intent = Intent(this@MainActivity_MyPage, MainActivity::class.java)
                                        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_CLEAR_TASK
                                        startActivity(intent)
                                        finish()
                                    }
                                    .show()
                            } else {
                                Log.d("MoodoLog DeleteUser", "Error:${response.code()}-${response.message()}")
                                AlertDialog.Builder(this@MainActivity_MyPage)
                                    .setMessage("회원 탈퇴에 실패했습니다.")
                                    .setPositiveButton("확인", null)
                                    .show()
                            }
                        }

                        override fun onFailure(call: Call<Void>, t: Throwable) {
                            Log.d("MooDoLog Delete fail", t.toString())
                            AlertDialog.Builder(this@MainActivity_MyPage)
                                .setMessage("오류 발생으로 탈퇴에 실패했습니다. 다시 시도해주세요")
                                .setPositiveButton("확인", null)
                                .show()
                        }
                    })
                }
                setNegativeButton("취소", null)
                show()
            }
        }
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

    // 이미지 선택
    private fun chooseImage() {
        val options = arrayOf("갤러리", "카메라")
        AlertDialog.Builder(this)
            .setTitle("프로필 사진 변경")
            .setItems(options) { _, which ->
                when (which) {
                    0 -> pickImage.launch("image/*") // 갤러리
//                    1 -> dispatchTakePictureIntent() // 카메라
                    1 -> checkCameraPermission() // 카메라
                }
            }
            .show()
    }

    private fun checkCameraPermission() {
        val permissions = arrayOf(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE)
        if (permissions.any {ContextCompat.checkSelfPermission(this, it) != PackageManager.PERMISSION_GRANTED }) {
            ActivityCompat.requestPermissions(this, permissions, CAMERA_REQUEST_CODE)
        } else {
            dispatchTakePictureIntent()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == CAMERA_REQUEST_CODE) {
            if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                dispatchTakePictureIntent()
            } else {
                Toast.makeText(this, "카메라 권한이 필요합니다.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // 프로필 사진 촬영 인텐트
    private fun dispatchTakePictureIntent() {
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
//            takePictureIntent.resolveActivity(packageManager)?.also {
                val photoFile = createImageFile()
                photoFile?.let {
                    currentPhotoPath = it.absolutePath
//                    val photoURI = Uri.fromFile(it)
                    val photoURI = FileProvider.getUriForFile(this, "${packageName}.fileprovider", it)
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                    takePicture.launch(photoURI)
                }
//            }
        }
    }

    // 이미지 파일 생성
    private fun createImageFile(): File? {
        val timeStamp: String = System.currentTimeMillis().toString()
        val storageDir: File? = getExternalFilesDir(null)
        return try {
            File.createTempFile("JPEG_${timeStamp}_", ".jpg", storageDir).apply {
                // Save a file: path for use with ACTION_VIEW intents
                currentPhotoPath = absolutePath
            }
        } catch (ex: Exception) {
            null
        }
    }

    private fun uploadProfilePicture(userId: String, file: MultipartBody.Part) {
        MooDoClient.retrofit.uploadProfilePicture(userId, file).enqueue(object : retrofit2.Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful) {
                    Toast.makeText(this@MainActivity_MyPage, "프로필 사진 업로드 성공", Toast.LENGTH_SHORT).show()
                    loadProfilePicture(userId)
                } else {
                    val errorBody = response.errorBody()?.string() ?: "알 수 없는 오류 발생"
                    Toast.makeText(this@MainActivity_MyPage, "업로드 실패: $errorBody", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Toast.makeText(this@MainActivity_MyPage, "업로드 실패: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun deleteProfilePicture(userId: String) {
        MooDoClient.retrofit.deleteProfilePicture(userId).enqueue(object : retrofit2.Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful) {
                    Toast.makeText(this@MainActivity_MyPage, "프로필 사진 삭제 성공", Toast.LENGTH_SHORT).show()
                    binding.userProfile.setImageResource(R.drawable.default_profile_image)
                } else {
                    val errorBody = response.errorBody()?.string() ?: "알 수 없는 오류 발생"
                    Toast.makeText(this@MainActivity_MyPage, "삭제 실패: $errorBody", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Toast.makeText(this@MainActivity_MyPage, "삭제 실패: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    // 프로필 사진 불러오기
    private fun loadProfilePicture(userId: String) {
        MooDoClient.retrofit.getUserImg(userId).enqueue(object : retrofit2.Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful) {
                    val imageBytes = response.body()?.bytes()
                    imageBytes?.let {
                        binding.userProfile.setImageBitmap(BitmapFactory.decodeByteArray(it, 0, it.size))
                    } ?: Toast.makeText(this@MainActivity_MyPage, "이미지가 없습니다", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this@MainActivity_MyPage, "프로필 사진 로드 실패: ${response.code()} - ${response.message()}", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Toast.makeText(this@MainActivity_MyPage, "사진 로드 실패: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    // URI로부터 파일 경로 얻기
    private fun getPathFromUri(uri: Uri): String? {
        val projection = arrayOf(MediaStore.Images.Media.DATA)
        contentResolver.query(uri, projection, null, null, null)?.use { cursor ->
            val columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
            if (cursor.moveToFirst()) {
                return cursor.getString(columnIndex)
            }
        }
        return null
    }

    companion object {
        private const val CAMERA_REQUEST_CODE = 100
    }
}