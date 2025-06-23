package com.example.wooriga

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.wooriga.databinding.ActivitySignUpBinding

class SignUpActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySignUpBinding

    override fun onCreate(savedInstanceState: Bundle?) {

        binding = ActivitySignUpBinding.inflate(layoutInflater)

        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        // 회원가입 버튼 클릭하면 카카오로 회원가입이 되도록
        binding.kakaoSignup.setOnClickListener() {
            val kakaoLoginUrl = "http://54.180.104.168:8081/oauth/kakao" // 실제 백엔드 주소로 변경
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(kakaoLoginUrl))
            startActivity(intent)
        }

    }

}