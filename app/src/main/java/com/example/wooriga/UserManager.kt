package com.example.wooriga

import android.content.Context
import android.content.SharedPreferences
import com.example.wooriga.model.FamilyGroupWrapper
import com.google.gson.Gson

object UserManager {
    private const val PREFS_NAME = "USER_PREFS"
    private lateinit var prefs: SharedPreferences



    fun init(context: Context) {
        prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }

    var accessToken: String?
        get() = prefs.getString("accessToken", null)
        set(value) = prefs.edit().putString("accessToken", value).apply()

    var refreshToken: String?
        get() = prefs.getString("refreshToken", null)
        set(value) = prefs.edit().putString("refreshToken", value).apply()

    var isLoggedIn: Boolean
        get() = prefs.getBoolean("isLoggedIn", false)
        set(value) = prefs.edit().putBoolean("isLoggedIn", value).apply()

    data class UserInfo(
        val userId: Long,
        val name: String,
        val status: String,
        val image: String?,
        val phone: String? = null,
        val birthDate: String? = null,
        val userFamilies: List<FamilyGroupSelected>? = emptyList()
    )
    data class FamilyGroupSelected(
        val familyGroupId: Long,
        val familyName: String
    )

    // 사용자 정보 저장
    fun saveUserInfo(userInfo: UserInfo?) {
        val userInfoJson = Gson().toJson(userInfo)
        prefs.edit().putString("userInfo", userInfoJson).apply()
    }
    // 사용자 정보 불러오기
    fun loadUserInfo(): UserInfo? {
        val userInfoJson = prefs.getString("userInfo", null) ?: return null
        return Gson().fromJson(userInfoJson, UserInfo::class.java)
    }
    // 로그아웃
    fun logout() {
        prefs.edit().clear().apply()
        accessToken = null
        refreshToken = null
        isLoggedIn = false
    }

}
data class TodayImagesResponse(
    val isSuccess: Boolean,
    val code: String,
    val message: String,
    val result: TodayImagesResult
)

data class TodayImagesResult(
    val userName: String,
    val userImage: String,
    val familyNames: List<String>,
    val latestFamilyImage: String,
    val todayImages: List<String>
)