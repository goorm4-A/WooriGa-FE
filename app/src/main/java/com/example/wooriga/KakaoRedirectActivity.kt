package com.example.wooriga

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class KakaoRedirectActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("KakaoRedirect", "Activity 시작됨")

        val uri: Uri? = intent?.data
        val code = uri?.getQueryParameter("code")

        if (code != null) {
            // 백엔드에 인가코드 전달
            sendCodeToBackend(code)
        } else {
            // 실패 처리
            finish()
        }
    }

    private fun sendCodeToBackend(code: String) {

        Log.d("KakaoRedirect", "sendCodeToBackend 호출, code = $code")

        val retrofit = Retrofit.Builder()
            .baseUrl("http://54.180.104.168:8081/") //배포주소
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val service = retrofit.create(ApiService::class.java)
        service.loginWithKakao(code).enqueue(object : Callback<LoginResponse> {
            override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                Log.d("KakaoRedirect", "onResponse 호출, 성공 여부: ${response.isSuccessful}")
                if (response.isSuccessful) {
                    // 성공 시 사용자 정보 저장 및 다음 화면으로 (사용자에게 추가 정보 받는 화면)
                    Log.d("KakaoRedirect", "로그인 성공! 다음 화면으로 이동")
                    val intent = Intent(this@KakaoRedirectActivity, UserInfoActivity::class.java)
                    startActivity(intent)
                    finish()
                } else {
                    // 실패 처리
                    Log.d("KakaoRedirect", "로그인 실패: ${response.code()}")
                    finish()
                }
            }

            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                // 통신 오류 처리
                Log.d("KakaoRedirect", "통신 실패: ${t.message}")
                finish()
            }
        })
    }
}
