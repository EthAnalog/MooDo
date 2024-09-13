package com.example.moodo.user

import android.app.AlertDialog
import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.moodo.R
import com.example.moodo.databinding.ActivityMainMyPageBinding
import com.example.moodo.db.MooDoClient
import com.example.moodo.db.MooDoUser
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File

class MainActivity_MyPage : AppCompatActivity() {
    lateinit var binding: ActivityMainMyPageBinding
    var user: MooDoUser? = null

    private val pickImage = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        uri?.let {
            val file = File(getPathFromUri(uri) ?: return@let)
            val requestBody = file.asRequestBody("image/*".toMediaTypeOrNull())
            val part = MultipartBody.Part.createFormData("file", file.name, requestBody)
            uploadProfilePicture(user!!.id, part)
        }
    }

    private val takePicture = registerForActivityResult(ActivityResultContracts.TakePicture()) { success ->
        if (success) {
            val file = File(currentPhotoPath)
            val requestBody = file.asRequestBody("image/*".toMediaTypeOrNull())
            val part = MultipartBody.Part.createFormData("file", file.name, requestBody)
            uploadProfilePicture(user!!.id, part)
        } else {
            Toast.makeText(this@MainActivity_MyPage, "사진 촬영 실패", Toast.LENGTH_SHORT).show()
        }
    }

    private var currentPhotoPath: String = ""

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

        // 프로필 사진 수정 클릭 이벤트
        binding.userProfileEdit.setOnClickListener {
            chooseImage()
        }

        // 프로필 사진 삭제 클릭 이벤트
        binding.btnDeleteImg.setOnClickListener {
            deleteProfilePicture(userId)
        }
    }

    // 유저 정보 로드
    private fun loadUserInfo(userId: String) {
        MooDoClient.retrofit.getUserInfo(userId).enqueue(object : Callback<MooDoUser> {
            override fun onResponse(call: Call<MooDoUser>, response: Response<MooDoUser>) {
                if (response.isSuccessful) {
                    user = response.body()
                    binding.userName.text = user!!.name.toString()
                    loadProfilePicture(userId)
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
                    1 -> dispatchTakePictureIntent() // 카메라
                }
            }
            .show()
    }

    // 프로필 사진 촬영 인텐트
    private fun dispatchTakePictureIntent() {
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
            takePictureIntent.resolveActivity(packageManager)?.also {
                val photoFile = createImageFile()
                photoFile?.let {
                    currentPhotoPath = it.absolutePath
                    val photoURI = Uri.fromFile(it)
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                    takePicture.launch(photoURI)
                }
            }
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
        MooDoClient.retrofit.uploadProfilePicture(userId, file).enqueue(object : Callback<ResponseBody> {
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
        MooDoClient.retrofit.deleteProfilePicture(userId).enqueue(object : Callback<ResponseBody> {
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
        MooDoClient.retrofit.getUserImg(userId).enqueue(object : Callback<ResponseBody> {
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
}

