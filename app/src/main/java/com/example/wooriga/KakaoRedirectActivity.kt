package com.example.wooriga

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class KakaoRedirectActivity : AppCompatActivity() {

    private lateinit var apiService: ApiService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val uri: Uri? = intent?.data
        Log.d("KakaoRedirect", "URI: $uri")

        val accessToken = uri?.getQueryParameter("accessToken")
        val refreshToken = uri?.getQueryParameter("refreshToken")

        if (accessToken.isNullOrEmpty()) {
            Log.e("KakaoRedirect", "accessToken X")
            finish()
            return
        }

        // UserManager에 토큰 저장
        UserManager.accessToken = accessToken
        UserManager.refreshToken = refreshToken
        UserManager.isLoggedIn = true

        apiService = RetrofitClient.apiService

        // 유저 정보 요청 후 → 다음 화면으로 이동
        getUserInfo()
        Log.d("KakaoRedirect", "accessToken 저장 완료: $accessToken")
    }

    private fun getUserInfo() {
        apiService.getUserInfo().enqueue(object : Callback<ApiResponse<UserManager.UserInfo>> {
            override fun onResponse(
                call: Call<ApiResponse<UserManager.UserInfo>>,
                response: Response<ApiResponse<UserManager.UserInfo>>
            ) {
                if (response.isSuccessful) {
                    val userInfo = response.body()?.result
                    if (userInfo != null) {
                        UserManager.saveUserInfo(userInfo)
                    }
                    // 다음 화면으로 이동
                    startActivity(Intent(this@KakaoRedirectActivity, UserInfoActivity::class.java))
                    finish()
                } else {
                    Log.e("KakaoRedirect", "user info fail: ${response.code()}")
                    finish()
                }
            }

            override fun onFailure(call: Call<ApiResponse<UserManager.UserInfo>>, t: Throwable) {
                Log.e("KakaoRedirect", "유저 정보 통신 실패: ${t.message}")
                finish()
            }
        })
    }
}