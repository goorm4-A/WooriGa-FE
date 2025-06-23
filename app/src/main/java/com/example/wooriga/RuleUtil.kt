package com.example.wooriga

fun RuleDto.toUiModel(): Rule {

    val type = when (ruleType) {
        "REQUIRED" -> "필수 규칙"
        "RECOMMENDED" -> "권장 사항"
        "PROHIBITED" -> "금기 사항"
        else -> "기타"
    }

    return Rule(
        id = id,
        family = familyName,
        type = type,
        title = title,
        description = description ?: "",
        date = DateUtils.formatIsoDate(createdAt)
    )
}
