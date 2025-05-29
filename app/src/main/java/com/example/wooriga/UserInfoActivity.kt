package com.example.wooriga

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.wooriga.databinding.ActivityUserInfoBinding

class UserInfoActivity : AppCompatActivity() {

    private lateinit var binding: ActivityUserInfoBinding

    override fun onCreate(savedInstanceState: Bundle?) {

        binding = ActivityUserInfoBinding.inflate(layoutInflater)

        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)

        // 회원가입 버튼 클릭하면 일단 사용자 정보 받는 화면으로 이동
        binding.submitButton.setOnClickListener() {
            val intent = Intent(this, ConsentActivity::class.java)
            startActivity(intent)
            finish()
        }

    }

}