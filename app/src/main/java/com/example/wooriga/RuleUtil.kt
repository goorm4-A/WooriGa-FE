package com.example.wooriga

fun RuleDto.toUiModel(): Rule {

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
        date = DateUtils.formatIsoDate(createdAt)
    )
}
