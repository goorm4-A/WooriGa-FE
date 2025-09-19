package com.example.wooriga

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.example.wooriga.databinding.ActivityUserInfoBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class UserInfoActivity : AppCompatActivity() {

    private lateinit var binding: ActivityUserInfoBinding
    private lateinit var service: ApiService

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        binding = ActivityUserInfoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        service = RetrofitClient2.apiService


        // 버튼 클릭하면 사용자 정보 저장 및 PUT한 후 정보 동의 화면으로 이동
        binding.submitButton.setOnClickListener {

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
            service.updateUserInfo(body)
                .enqueue(object : Callback<Void> {
                    override fun onResponse(call: Call<Void>, response: Response<Void>) {
                        if (response.isSuccessful) {
                            val intent = Intent(this@UserInfoActivity, ConsentActivity::class.java)
                            startActivity(intent)
                            finish()
                        } else {
                            // 실패 처리 (토스트 등)
                            Log.d("UserInfoActivity", "PUT fail: ${response.code()}")
                        }
                    }

                    override fun onFailure(call: Call<Void>, t: Throwable) {
                        // 실패 처리 (토스트 등)
                        Log.d("UserInfoActivity", "network fail: ${t.message}")
                    }
                })
        }
    }
}