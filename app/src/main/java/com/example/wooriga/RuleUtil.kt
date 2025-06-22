package com.example.wooriga

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

fun RuleDto.toUiModel(): Rule {
    val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSX", Locale.KOREA)
    inputFormat.timeZone = TimeZone.getTimeZone("UTC")

    val outputFormat = SimpleDateFormat("yyyy년 M월 d일 E요일", Locale.KOREA)

    val parsedDate = try {
        val date: Date = inputFormat.parse(createdAt)
        outputFormat.format(date)
    } catch (e: Exception) {
        "날짜 오류"
    }

    val type = when (ruleType) {
        "REQUIRED" -> "필수 규칙"
        "RECOMMENDED" -> "권장 사항"
        "PROHIBITED" -> "금기 사항"
        else -> "기타"
    }

    return Rule(
        family = familyName,
        type = type,
        title = title,
        description = "", // 현재 API 응답엔 없음
        date = parsedDate
    )
}
