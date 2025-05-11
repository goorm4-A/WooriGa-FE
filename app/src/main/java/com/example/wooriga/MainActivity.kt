package com.example.wooriga

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.wooriga.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 기본 화면 설정
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, HomeFragment())
            .commit()

        // 하단바 클릭 처리
        binding.bottomNavigation.setOnItemSelectedListener {
            val fragment = when (it.itemId) {
                R.id.menu_home -> HomeFragment()
                R.id.menu_family_history -> FamilyHistoryFragment()
                R.id.menu_family_anniversary -> FamilyAnniversaryFragment()
                R.id.menu_family_culture -> FamilyCultureFragment()
                R.id.menu_family_diary -> FamilyDiaryFragment()
                else -> null
            }

            fragment?.let {
                supportFragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, it)
                    .commit()
            }
            true
        }
    }
}