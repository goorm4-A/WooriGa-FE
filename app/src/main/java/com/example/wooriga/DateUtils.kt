package com.example.wooriga

import java.text.SimpleDateFormat
import java.util.Locale

object DateUtils {

    // 날짜 변환 함수
    fun formatIsoDate(inputDate: String, toFormat: String = "yyyy-MM-dd"): String {
        return try {
            val inputFormatter = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
            val outputFormatter = SimpleDateFormat(toFormat, Locale.getDefault())
            val date = inputFormatter.parse(inputDate)
            outputFormatter.format(date!!)
        } catch (e: Exception) {
            inputDate.take(10) // "2025-06-23"
        }
    }
}
