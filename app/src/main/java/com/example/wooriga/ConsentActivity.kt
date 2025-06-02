package com.example.wooriga

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.wooriga.databinding.ActivityConsentBinding

class ConsentActivity : AppCompatActivity() {
    private lateinit var binding: ActivityConsentBinding

    override fun onCreate(savedInstanceState: Bundle?) {

        binding = ActivityConsentBinding.inflate(layoutInflater)

        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)

        // 버튼 클릭하면 메인 홈 화면으로 이동
        binding.submitButton.setOnClickListener() {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish() // ConsentActivity 제거
        }

    }

}