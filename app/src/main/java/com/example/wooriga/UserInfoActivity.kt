package com.example.wooriga

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.wooriga.databinding.ActivityUserInfoBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class UserInfoActivity : AppCompatActivity() {

    private lateinit var binding: ActivityUserInfoBinding
    private lateinit var service: ApiService

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        binding = ActivityUserInfoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Retrofit 초기화
        val retrofit = Retrofit.Builder()
            .baseUrl("http://54.180.104.168:8081") // 서버 주소 확인
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        service = retrofit.create(ApiService::class.java)

        // 버튼 클릭하면 사용자 정보 저장 및 PUT한 후 정보 동의 화면으로 이동
        binding.submitButton.setOnClickListener() {

            Toast.makeText(this, "클릭됨", Toast.LENGTH_SHORT).show()
            Log.d("KakaoRedirect", "submit button clicked")

            val name = binding.userName.text.toString()
            val phone = binding.userPhone.text.toString()
            val birth = binding.userBirth.text.toString()

            val currentUser = UserManager.loadUserInfo()
            if (currentUser != null) {
                val updatedUser = currentUser.copy(
                    name = name,
                    phone = phone,
                    birthDate = birth
                )
                UserManager.saveUserInfo(updatedUser)
            }

            // /users/me/info PUT 요청
            val body = mapOf(
                "name" to name,
                "phone" to phone,
                "birthDate" to birth
            )
            val accessToken = UserManager.accessToken
            Log.d("UserInfoActivity", "accessToken: $accessToken")
            if (accessToken == null) {
                // 토큰 없으면 예외 처리
                Log.d("UserInfoActivity", "login fail")

                return@setOnClickListener
            }
            service.updateUserInfo("Bearer $accessToken", body)
                .enqueue(object : Callback<Void> {
                    override fun onResponse(call: Call<Void>, response: Response<Void>) {
                        if (response.isSuccessful) {
                            val intent = Intent(this@UserInfoActivity, ConsentActivity::class.java)
                            startActivity(intent)
                            finish()
                        } else {
                            // 실패 처리 (토스트 등)
                            Log.d("UserInfoActivity", "PUT fail")

                        }
                    }

                    override fun onFailure(call: Call<Void>, t: Throwable) {
                        // 실패 처리 (토스트 등)
                        Log.d("UserInfoActivity", "network fail")

                    }
                })
        }

    }

}