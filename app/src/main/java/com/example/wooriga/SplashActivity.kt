package com.example.wooriga

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity

class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_splash)

        UserManager.init(this)

        // 로그인 상태 확인
        UserManager.accessToken?.let {
            // accessToken이 존재하면 로그인 상태로 간주
            UserManager.isLoggedIn = true
        } ?: run {
            // accessToken이 없으면 로그인되지 않은 상태로 간주
            UserManager.isLoggedIn = false
        }

        Handler(Looper.getMainLooper()).postDelayed({

            if (UserManager.isLoggedIn) {
                // 이미 로그인된 상태라면 ConsentActivity로 이동
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                finish() // SplashActivity 제거
            } else {
                // 로그인되지 않은 상태라면 SignUpActivity로 이동
                val intent = Intent(this, SignUpActivity::class.java)
                startActivity(intent)
                finish() // SplashActivity 제거
            }
        }, 4000)
    }
}