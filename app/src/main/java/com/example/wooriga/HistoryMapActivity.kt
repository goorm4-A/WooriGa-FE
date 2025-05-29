package com.example.wooriga

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.wooriga.databinding.ActivityHistoryMapBinding

class HistoryMapActivity : AppCompatActivity() {
    private lateinit var binding: ActivityHistoryMapBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHistoryMapBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 상단바의 < 뒤로가기 버튼 클릭 시 이전 액티비티로 이동
        binding.backButton.setOnClickListener {
            finish()
        }
    }
}